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
    useJarJar.set(true)
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-tweaked",
            "peripheralium" to "peripheralium",
        ),
    )
    shake()
}

configurations {
    minecraftLibrary { extendsFrom(minecraftEmbed.get()) }
}

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
    // So, this is here because it isn't suppose to be used right now
    // but this should be changed in future!
    implementation(libs.bundles.webframework)

    minecraftEmbed(libs.bundles.db) {
        jarJar(this)
        exclude("org.jetbrains", "annotations")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
//        isTransitive = false
    }
    minecraftEmbed(libs.bundles.math) {
        jarJar(this)
        exclude("org.jetbrains", "annotations")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
//        isTransitive = false
    }
    minecraftEmbed(libs.bundles.metrics) {
        jarJar(this)
        exclude("org.jetbrains", "annotations")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
//        isTransitive = false
    }

    libs.bundles.forge.cc.get().map { implementation(fg.deobf(it)) }

    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }
}

modPublishing {
    output.set(tasks.jarJar)
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "kotlin-for-forge",
            "peripheralium",
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
