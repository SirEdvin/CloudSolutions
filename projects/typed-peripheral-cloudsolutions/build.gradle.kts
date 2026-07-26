import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version.set("22.14.0")
    download.set(true)
    nodeProjectDir.set(projectDir)
    workDir.set(rootProject.layout.projectDirectory.dir(".gradle/nodejs"))
    npmWorkDir.set(rootProject.layout.projectDirectory.dir(".gradle/npm"))
    npmInstallCommand.set("ci")
}

val compileTypeScript by tasks.registering(NpmTask::class) {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.files(fileTree(projectDir) {
        include("package.json", "package-lock.json", "tsconfig.json", "**/*.ts")
        exclude("node_modules/**")
    })
    outputs.files(
        file("crafkaBroker.d.ts"),
        file("crafkaBroker.lua"),
        file("events/crafkaBrokerMessage.d.ts"),
        file("events/crafkaBrokerMessage.lua"),
        file("events/kvStorageKeyChanged.d.ts"),
        file("events/kvStorageKeyChanged.lua"),
        file("events/kvStorageKeyDeleted.d.ts"),
        file("events/kvStorageKeyDeleted.lua"),
        file("kvStorage.d.ts"),
        file("kvStorage.lua"),
        file("lualib_bundle.lua"),
        file("statsdBridge.d.ts"),
        file("statsdBridge.lua"),
    )
}

tasks.assemble {
    dependsOn(compileTypeScript)
}

tasks.clean {
    delete(fileTree(projectDir) {
        include("**/*.lua")
    })
}
