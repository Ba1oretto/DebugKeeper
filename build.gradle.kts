@file:Suppress("VulnerableLibrariesLocal")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("7.1.2")
    id("com.baioretto.specialsource").version("1.0.0")
}

group = "com.baioretto"
version = "1.0.0-SNAPSHOT"
val minecraftVersion = "1.18.2-R0.1-SNAPSHOT"
val kyoriVersion = "4.11.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot:${minecraftVersion}:remapped-mojang") // maven local

    implementation("net.kyori:adventure-api:${kyoriVersion}") // maven central
    implementation("net.kyori:adventure-text-serializer-legacy:${kyoriVersion}") // maven central
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType(JavaCompile::class).configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = LinkedHashMap<String, Any>()
    props["version"] = version
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

sourceSets {
    main {
        resources.srcDir("src/main/resource/")
    }
}

tasks.shadowJar {
    minimize()
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
    archiveExtension.set("jar")
}

val mcversion = "1.18.2-R0.1-SNAPSHOT"
val normalJarName = "${project.name}-${version}.jar"
val normalJarPath = File(project.buildDir, "libs/${normalJarName}")
val obfuscatedJarName = "${project.name}-${version}-obf.jar"
val obfuscatedJarPath = File(project.buildDir, "libs/${obfuscatedJarName}")

val shadowJar by tasks.existing(ShadowJar::class)

tasks.register<com.baioretto.specialsource.task.MojangMappingToMojangObfuscated>("mmtmo") {
    group = "specialsource"

    mustRunAfter(shadowJar)
    minecraftVersion.set(mcversion)
    input.set(normalJarPath)
}
val mmtmo by tasks.existing(com.baioretto.specialsource.task.MojangMappingToMojangObfuscated::class)

tasks.register<com.baioretto.specialsource.task.MojangObfuscatedToSpigotObfuscated>("motso") {
    group = "specialsource"

    mustRunAfter(mmtmo)
    minecraftVersion.set(mcversion)
    input.set(obfuscatedJarPath)
}
val motso by tasks.existing(com.baioretto.specialsource.task.MojangObfuscatedToSpigotObfuscated::class)

tasks.register("copyJar", Copy::class) {
    group = "specialsource"

    from(normalJarPath)
    findProperty("server.plugin.folder")?.let { into(it) }

    rename {
        "${project.name}.jar"
    }

    mustRunAfter(motso)
}
val copyJar by tasks.existing(Copy::class)

tasks.register("compile") {
    group = "specialsource"

    dependsOn(shadowJar)
    dependsOn(mmtmo)
    dependsOn(motso)
    dependsOn(copyJar)
}