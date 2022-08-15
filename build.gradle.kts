@file:Suppress("VulnerableLibrariesLocal")

plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "com.baioretto"
version = "1.3.0-SNAPSHOT"
val kyoriVersion = "4.11.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT") // maven local

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

val shadowJar = tasks.getByName("shadowJar")

val copyJar = task<MultiCopy>("copyJar") {
    mustRunAfter(shadowJar)
    group = "debugkeeper"
    from(layout.buildDirectory.dir("libs/${rootProject.name}-${rootProject.version}.jar").get().asFile)
    (findProperty("server.plugin.folder") as String?)?.let(::destDir)
    separator("|")
    rename(".*(.jar)", "${rootProject.name}$1")
}

tasks.register("compile") {
    group = "debugkeeper"
    dependsOn(shadowJar)
    dependsOn(copyJar)
}

abstract class MultiCopy : Copy() {
    private lateinit var destinationDirectories: String

    private lateinit var directorySeparator: String

    private fun createCopyActions(): Array<org.gradle.api.internal.file.copy.CopyAction> {
        if (!::destinationDirectories.isInitialized) return arrayOf()

        val destDirList = destinationDirectories.split(if (::directorySeparator.isInitialized) directorySeparator else "|")

        if (destDirList.isEmpty()) {
            return arrayOf(
                org.gradle.api.internal.file.copy.FileCopyAction(
                    fileLookup.getFileResolver(
                        File(
                            destinationDirectories
                        )
                    )
                )
            )
        }

        val fileCopyActions = mutableListOf<org.gradle.api.internal.file.copy.FileCopyAction>()
        destDirList.forEach { dir ->
            fileCopyActions.add(org.gradle.api.internal.file.copy.FileCopyAction(fileLookup.getFileResolver(File(dir))))
        }
        return fileCopyActions.toTypedArray()
    }

    fun destDir(destinationDirectories: String): MultiCopy {
        this.destinationDirectories = destinationDirectories
        return this
    }

    fun separator(directorySeparator: String): MultiCopy {
        this.directorySeparator = directorySeparator
        return this
    }

    @TaskAction
    override fun copy() {
        val copyActionExecutor = createCopyActionExecuter()
        val copyActions = createCopyActions()
        var didWork = true
        copyActions.forEach { action ->
            if (!copyActionExecutor.execute(rootSpec, action).didWork) didWork = false
        }
        setDidWork(didWork)
    }

    @OutputDirectory
    @Optional
    override fun getDestinationDir(): File {
        return File("")
    }
}