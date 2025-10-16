import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.function.BiConsumer

plugins {
    java
    id("site.siredvin.root") version "0.8.18"
    id("site.siredvin.release") version "0.8.18"
    id("com.dorongold.task-tree") version "2.1.1"
}

subprojectShaking {
    withKotlin.set(true)
    kotlinVersion.set("2.0.20")
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    setupSubproject(this)
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            compilerOptions.allWarningsAsErrors.set(false)
        }
    }
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