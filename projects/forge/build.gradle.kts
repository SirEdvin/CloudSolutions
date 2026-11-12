import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.neoforged.moddevgradle.legacyforge.dsl.ObfuscationExtension
import site.siredvin.peripheralium.gradle.mavenDependencies

// ForgeGradle 6 (site.siredvin.forge) does not run on Gradle 9, which Loom 1.18 requires, so Forge uses ModDevGradle.
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("net.neoforged.moddev.legacyforge")
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
}

val commonProject = project(":core")

baseShaking {
    projectPart.set("forge")
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += commonProject.sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += commonProject.sourceSets["testMod"].output
}

// Plain libraries shipped with jar-in-jar; in development they are unpacked into the mod itself.
val minecraftEmbed = configurations.create("minecraftEmbed") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
configurations.compileOnly { extendsFrom(minecraftEmbed) }

val forgeRuntimeLibrariesDir = layout.buildDirectory.dir("generated/forgeRuntimeLibraries")
val unpackForgeRuntimeLibraries = tasks.register<Sync>("unpackForgeRuntimeLibraries") {
    from({ minecraftEmbed.map(::zipTree) })
    exclude("META-INF/MANIFEST.MF", "module-info.class", "META-INF/versions/**/module-info.class")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    into(forgeRuntimeLibrariesDir)
}

val devRuntime = sourceSets.create("devRuntime") {
    resources.srcDir(forgeRuntimeLibrariesDir)
}
tasks.named(devRuntime.processResourcesTaskName) { dependsOn(unpackForgeRuntimeLibraries) }

legacyForge {
    enable { forgeVersion = "${project.extra["minecraftVersion"]}-${libs.versions.forge.asProvider().get()}" }
    parchment {
        minecraftVersion = libs.versions.parchmentMc.get()
        mappingsVersion = libs.versions.parchment.get()
    }
    validateAccessTransformers.set(true)
    setAccessTransformers(sourceSets.main.map { it.resources.matching { include("META-INF/accesstransformer.cfg") } })
    mods {
        create("cloudsolutions") {
            sourceSet(sourceSets.main.get())
            sourceSet(devRuntime)
        }
        create("cloudsolutions_testmod") {
            sourceSet(testMod)
            sourceSet(commonProject.sourceSets["testMod"])
        }
    }
    runs {
        create("client") {
            client()
            gameDirectory = file("run")
            loadedMods.add(mods.named("cloudsolutions"))
        }
        create("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
            loadedMods.add(mods.named("cloudsolutions"))
        }
        create("data") {
            data()
            gameDirectory = file("run")
            programArguments.addAll(
                "--mod", "cloudsolutions", "--all", "--output", file("src/generated/resources").absolutePath,
                "--existing", commonProject.file("src/main/resources").absolutePath,
                "--existing", file("src/main/resources").absolutePath,
            )
            loadedMods.add(mods.named("cloudsolutions"))
        }
        create("gameTestServer") {
            type = "gameTestServer"
            sourceSet.set(testMod)
            gameDirectory = file("run/cloudsolutions-gametest")
            systemProperty("forge.enabledGameTestNamespaces", "cloudsolutionsgametests")
            systemProperty("testiarium.tags", "cloudsolutions")
            systemProperty("testiarium.structures", commonProject.layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", commonProject.file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", commonProject.layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/cloudsolutions-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            loadedMods.add(mods.named("cloudsolutions"))
            loadedMods.add(mods.named("cloudsolutions_testmod"))
        }
    }
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") { content { includeGroup("thedarkcolour") } }
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
    maven {
        url = uri("https://www.jitpack.io")
        content {
            includeGroup("com.github.artbits")
        }
    }
}

val obfuscation = extensions.getByType<ObfuscationExtension>()
val modImplementation = configurations.getByName("modImplementation")
val modRuntimeOnly = configurations.getByName("modRuntimeOnly")
val modTestImplementation = obfuscation.createRemappingConfiguration(configurations.getByName(testMod.implementationConfigurationName))

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)
    compileOnly(project(":core")) {
        exclude("cc.tweaked")
        exclude("fuzs.forgeconfigapiport")
    }

    for (bundle in listOf(libs.bundles.db, libs.bundles.metrics)) {
        // Excludes stay on the dependencies: configuration excludes would also strip Kotlin from compileOnly.
        minecraftEmbed(bundle) {
            exclude("org.jetbrains", "annotations")
            exclude("org.slf4j", "slf4j-api")
            exclude("org.jetbrains.kotlin")
            exclude("org.jetbrains.kotlinx")
        }
    }
    jarJar(libs.bundles.db) { isTransitive = false }
    jarJar(libs.bundles.metrics) { isTransitive = false }

    add(modImplementation.name, libs.bundles.forge.cc)
    add(modImplementation.name, libs.bundles.forge.include)
    jarJar(libs.bundles.forge.jjar) { isTransitive = false }

    add(modRuntimeOnly.name, libs.bundles.externalMods.forge.runtime)

    add(modTestImplementation.name, libs.testiarium.forge)
    for (feature in listOf("test-mod", "cct-test-mod")) {
        add(modTestImplementation.name, libs.testiarium.forge.get().toString()) {
            capabilities { requireCapability("site.siredvin:testiarium-forge-$feature") }
        }
    }
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") { source(commonProject.sourceSets.main.get().allSource) }
tasks.named<JavaCompile>("compileJava") { source(commonProject.sourceSets.main.get().allSource) }
tasks.processResources {
    from(commonProject.sourceSets.main.get().resources)
    val versions = mapOf(
        "version" to project.version,
        "file" to mapOf("jarVersion" to project.version),
        "forgeVersion" to libs.versions.forge.asProvider().get(),
        "computercraftVersion" to libs.versions.cc.tweaked.get(),
        "tweakiumVersion" to libs.versions.tweakium.asProvider().get(),
        "broccoliumVersion" to libs.versions.broccolium.asProvider().get(),
    )
    inputs.properties(versions)
    filesMatching("META-INF/mods.toml") { expand(versions) }
    exclude(".cache")
}
tasks.jar { manifest.attributes["MixinConfigs"] = "cloudsolutions.mixins.json" }
extra["releaseJar"] = "reobfJar"

configurations.named("reobfRuntimeElements") {
    extendsFrom(configurations.implementation.get(), modImplementation)
}

modPublishing {
    output.set(tasks.named<Jar>("reobfJar"))
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "kotlin-for-forge",
        ),
    )
    shake()
}

// Buildenv infers the loader from ForgeGradle, which this project no longer applies.
modrinth { loaders.add("forge") }

publishingShaking {
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}

tasks.named<TaskPublishCurseForge>("publishCurseForge") {
    uploadArtifacts.forEach {
        it.addEnvironment("Client", "Server")
        it.addModLoader("Forge")
    }
}
