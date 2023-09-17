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
    accessWideners.add("src/main/resources/datafortress-common.accesswidener")
    accessWideners.add("src/main/resources/datafortress.accesswidener")
    shake()
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    implementation(libs.bundles.db)
    api(libs.bundles.apicommon)
}

publishingShaking {
    shake()
}
