import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.api.artifacts.ExternalModuleDependency
import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.forge")
}

baseShaking {
    projectPart.set("forge")
    shake()
}

forgeShaking {
    commonProjectName.set("core")
    useAT.set(true)
    useMixins.set(true)
    useJarJar.set(true)
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
            "broccolium" to "broccolium",
            "tweakium" to "tweakium",
        ),
    )
    shake()
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":core").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":core").sourceSets["testMod"].output
}

val forgeRuntimeLibrariesDir = layout.buildDirectory.dir("generated/forgeRuntimeLibraries")
val unpackForgeRuntimeLibraries = tasks.register<Sync>("unpackForgeRuntimeLibraries") {
    from({ configurations.minecraftEmbed.get().map(::zipTree) })
    exclude("META-INF/MANIFEST.MF", "module-info.class", "META-INF/versions/**/module-info.class")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    into(forgeRuntimeLibrariesDir)
}

val devRuntime = sourceSets.create("devRuntime") {
    resources.srcDir(forgeRuntimeLibrariesDir)
}
tasks.named(devRuntime.processResourcesTaskName) { dependsOn(unpackForgeRuntimeLibraries) }

repositories {
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

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.forge.raw)

    minecraftEmbed(libs.bundles.db) {
        jarJar(this) {
            isTransitive = false
        }
        exclude("org.jetbrains", "annotations")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
//        isTransitive = false
    }
    minecraftEmbed(libs.bundles.metrics) {
        jarJar(this) {
            isTransitive = false
        }
        exclude("org.jetbrains", "annotations")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
//        isTransitive = false
    }
    libs.bundles.forge.cc.get().map { implementation(fg.deobf(it)) }
    libs.bundles.forge.include.get().map { implementation(fg.deobf(it)) }

    jarJar(libs.bundles.forge.jjar) {
        isTransitive = false
    }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }

    listOf(
        "site.siredvin:testiarium-forge-1.20.1:0.1.1",
        "site.siredvin:testiarium-forge-1.20.1:0.1.1:cct-test-mod@jar",
    ).forEach { notation ->
        add(
            testMod.implementationConfigurationName,
            fg.deobf((project.dependencies.create(notation) as ExternalModuleDependency).apply { isTransitive = false }),
        )
    }
}

minecraft {
    runs {
        create("gameTestServer") {
            workingDirectory(file("run/cloudsolutions-gametest"))
            property("forge.enabledGameTestNamespaces", "cloudsolutionsgametests")
            property("testiarium.tags", "cloudsolutions")
            property("testiarium.structures", project(":core").layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            property("testiarium.fixture-source", project(":core").file("src/testMod/resources/gameteststructures").absolutePath)
            property("testiarium.cct-fixtures", project(":core").layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            property("testiarium.gametest-report", layout.buildDirectory.file("test-results/cloudsolutions-gametest.xml").get().asFile.absolutePath)
            jvmArgs("-ea")
            args("--nogui")
            mods {
                create("cloudsolutions") {
                    source(sourceSets.main.get())
                    source(devRuntime)
                }
                create("cloudsolutions_testmod") {
                    source(testMod)
                    source(project(":core").sourceSets["testMod"])
                }
            }
        }
    }
}

modPublishing {
    output.set(tasks.jarJar)
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "kotlin-for-forge",
        ),
    )
    shake()
}

publishingShaking {
    shake()
    project.publishing {
        publications {
            named<MavenPublication>("maven") {
                fg.component(this)
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}

tasks.named<TaskPublishCurseForge>("publishCurseForge") {
    uploadArtifacts.forEach { it.addEnvironment("Client", "Server") }
}
