@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

baseShaking {
    projectPart.set("common")
    shake()
}

vanillaShaking {
    accessWideners.add("src/main/resources/cloudsolutions-common.accesswidener")
    accessWideners.add("src/main/resources/cloudsolutions.accesswidener")
    shake()
}

repositories {
    maven {
        url = uri("https://www.jitpack.io")
        content {
            includeGroup("com.github.artbits")
        }
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    implementation(libs.bundles.db) {
        exclude("org.jetbrains.kotlin")
    }
    implementation(libs.bundles.math)
    implementation(libs.bundles.metrics)
    api(libs.bundles.apicommon)
}

publishingShaking {
    shake()
}
