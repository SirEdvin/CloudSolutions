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
