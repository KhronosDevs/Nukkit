plugins {
    id("dev.welbyseely.gradle-cmake-plugin") version "0.1.0"
    java
}

val buildDirectory: String = layout.buildDirectory.asFile.get().absolutePath
val targetLibsDir: String = "${buildDirectory}/cmake/build"

version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

cmake {
    workingFolder.set(file("${buildDirectory}/cmake"))
    sourceFolder.set(file("${projectDir}/src/main/c"))

    defs.put("BUILD_ARCH", System.getProperty("os.arch"))

    buildSharedLibs.set(true)
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

sourceSets {
    main {
        resources {
            srcDirs(targetLibsDir)
        }
    }
}


tasks.withType(JavaCompile::class.java) {
    dependsOn("cmakeBuild")
    options.encoding = "UTF-8"
}

tasks.withType(ProcessResources::class.java) {
    mustRunAfter("cmakeBuild")
    mustRunAfter("cmakeConfigure")
}