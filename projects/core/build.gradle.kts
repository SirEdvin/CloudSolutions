@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
    // Loom 1.18 consumes non-Loom project dependencies through the companion's exported classpath.
    id("net.fabricmc.fabric-loom-companion")
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

val testMod = sourceSets.create("testMod") {
    compileClasspath += sourceSets.main.get().compileClasspath
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    runtimeClasspath += sourceSets.main.get().output
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    implementation(libs.bundles.db)
    implementation(libs.bundles.metrics)
    api(libs.bundles.apicommon)
    compileOnly(libs.fabric.config) {
        isTransitive = false
    }
    add(testMod.implementationConfigurationName, libs.testiarium.core)
    for (feature in listOf("test-mod", "cct-test-mod")) {
        add(testMod.implementationConfigurationName, libs.testiarium.core.get().toString()) {
            capabilities { requireCapability("site.siredvin:testiarium-core-$feature") }
        }
    }
    add(testMod.compileOnlyConfigurationName, libs.bundles.cccommon)
}

tasks.named<ProcessResources>(testMod.processResourcesTaskName) {
    dependsOn(":typescript-tests:compileTestLua")
    from(project(":typescript-tests").layout.buildDirectory.dir("generated/test-lua")) {
        into("computer/tests")
    }
}

publishingShaking {
    shake()
}
