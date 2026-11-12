import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.function.BiConsumer

plugins {
    java
    // CC:Tweaked 1.121.0 Fabric jars are built with Loom 1.18; buildenv alone would resolve an older Loom.
    id("fabric-loom") version "1.18.4" apply false
    id("site.siredvin.root") version "0.9.4"
    id("site.siredvin.release") version "0.9.4"
    id("com.dorongold.task-tree") version "4.0.0"
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.20")
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    if (name !in setOf("typed-peripheral-cloudsolutions", "typescript-tests")) {
        setupSubproject(this)
    }
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            compilerOptions.allWarningsAsErrors.set(false)
        }
    }
}

tasks.register("gameTest") {
    group = "verification"
    description = "Runs CloudSolutions GameTests on Forge and Fabric."
    dependsOn(":forge:runGameTestServer", ":fabric:runCloudSolutionsGameTest")
}

githubShaking {
    modBranch.set("1.20")
    projectRepo.set("CloudSolutions")
    useForgeJarJar.set(true)
//    mastodonProjectName.set("UnlimitedPeripheralWorks")
    shake()
}


repositories {
    mavenCentral()
}
