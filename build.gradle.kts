import java.util.function.BiConsumer

plugins {
    java
    id("site.siredvin.root") version "0.4.12"
    id("site.siredvin.release") version "0.4.12"
    id("com.dorongold.task-tree") version "2.1.1"
}

subprojectShaking {
    withKotlin.set(true)
}

val setupSubproject = subprojectShaking::setupSubproject

subprojects {
    setupSubproject(this)
}

githubShaking {
    modBranch.set("1.20")
    projectRepo.set("DataFortress")
//    mastodonProjectName.set("UnlimitedPeripheralWorks")
    shake()
}


repositories {
    mavenCentral()
}