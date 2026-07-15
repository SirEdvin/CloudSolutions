import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Sync
import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.publishing")
    id("site.siredvin.mod-publishing")
    id("site.siredvin.neoforge")
}

baseShaking {
    projectPart.set("forge")
    shake()
}

neoforgeShaking {
    commonProjectName.set("core")
    useAT.set(true)
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
            "broccolium" to "broccolium",
            "tweakium" to "tweakium",
        ),
    )
    shake()
}

val embeddedGameLibraries by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}
val developmentRuntime = configurations.create("developmentRuntime")
configurations.runtimeClasspath {
    extendsFrom(developmentRuntime)
}

val embeddedGameLibrariesDirectory = layout.buildDirectory.dir("generated/embeddedGameLibraries")
val unpackEmbeddedGameLibraries = tasks.register<Sync>("unpackEmbeddedGameLibraries") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(provider { embeddedGameLibraries.map(::zipTree) })
    into(embeddedGameLibrariesDirectory)
    exclude("META-INF/MANIFEST.MF")
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}

sourceSets.main {
    output.dir(mapOf("builtBy" to unpackEmbeddedGameLibraries), embeddedGameLibrariesDirectory)
}

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    compileClasspath += project(":core").sourceSets["testMod"].output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += project(":core").sourceSets["testMod"].output
}

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
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

    compileOnly(libs.bundles.db)
    libs.bundles.db.get().forEach {
        val runtimeDependency = project.dependencies.create(it) as ExternalModuleDependency
        runtimeDependency.isTransitive = false
        add(embeddedGameLibraries.name, runtimeDependency)
    }
    compileOnly(libs.bundles.metrics)
    libs.bundles.metrics.get().forEach {
        val runtimeDependency = project.dependencies.create(it) as ExternalModuleDependency
        runtimeDependency.isTransitive = false
        add(embeddedGameLibraries.name, runtimeDependency)
    }
    implementation(libs.bundles.forge.cc)
    implementation(libs.bundles.forge.include) {
        isTransitive = false
    }

    jarJar(libs.bundles.forge.jjar) {
        isTransitive = false
    }

    runtimeOnly(libs.bundles.externalMods.forge.runtime)

    listOf(
        "site.siredvin:testiarium-forge-1.21.1:0.1.1",
        "site.siredvin:testiarium-forge-1.21.1:0.1.1:test-mod@jar",
        "site.siredvin:testiarium-forge-1.21.1:0.1.1:cct-test-mod@jar",
    ).forEach { notation ->
        add(testMod.implementationConfigurationName, notation) {
            isTransitive = false
        }
        add(developmentRuntime.name, notation) {
            isTransitive = false
        }
    }
}

neoForge {
    val cloudsolutions = mods.named("cloudsolutions")
    val cloudsolutionsTestMod by mods.registering {
        sourceSet(testMod)
        sourceSet(project(":core").sourceSets["testMod"])
    }
    runs {
        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run/cloudsolutions-gametest")
            systemProperty("neoforge.enabledGameTestNamespaces", "cloudsolutions_testmod")
            systemProperty("testiarium.tags", "cloudsolutions")
            systemProperty("testiarium.structures", project.project(":core").layout.buildDirectory.dir("resources/testMod/gameteststructures").get().asFile.absolutePath)
            systemProperty("testiarium.fixture-source", project.project(":core").file("src/testMod/resources/gameteststructures").absolutePath)
            systemProperty("testiarium.cct-fixtures", project.project(":core").layout.buildDirectory.dir("resources/testMod/computer").get().asFile.absolutePath)
            systemProperty("testiarium.gametest-report", layout.buildDirectory.file("test-results/cloudsolutions-gametest.xml").get().asFile.absolutePath)
            jvmArgument("-ea")
            programArgument("--nogui")
            loadedMods.add(cloudsolutions.get())
            loadedMods.add(cloudsolutionsTestMod.get())
        }
    }
}

modPublishing {
    output.set(tasks.jar)
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
                mavenDependencies {
                    exclude(dependencies.create("site.siredvin:"))
                    exclude(libs.jei.forge.get())
                }
            }
        }
    }
}
