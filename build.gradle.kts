import proguard.gradle.ProGuardTask

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "me.bristermitten"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.1") {
            exclude("com.android.tools.build")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.bristermitten.me/repository/maven-releases/")
    maven("https://repo.bristermitten.me/repository/maven-snapshots/")
    maven("https://repo.triumphteam.dev/releases/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

val shade: Configuration by configurations.creating {
    configurations["implementation"].extendsFrom(this)
    isCanBeResolved = true
}
val onClasspath: Configuration by configurations.creating {
    isCanBeResolved = true
    configurations["compileOnly"].extendsFrom(this)
}

val proguard: Configuration by configurations.creating {
    isCanBeResolved = true
}


dependencies {
    onClasspath("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    proguard("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

//    compileOnly("net.luckperms:api:5.4")

    val mittenLibVersion = "1.0.12-SNAPSHOT"
    shade("me.bristermitten:mittenlib-core:$mittenLibVersion")
    shade("me.bristermitten:mittenlib-minimessage:$mittenLibVersion")
    shade("me.bristermitten:mittenlib-papi:$mittenLibVersion")
    annotationProcessor("me.bristermitten:mittenlib-annotation-processor:$mittenLibVersion")

    shade("dev.triumphteam:triumph-gui:3.1.2")
    @Suppress("GradlePackageUpdate") // Keeping this lower to support Java 8
    shade("com.zaxxer:HikariCP:4.0.3")

    shade("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    shade("de.tr7zw:item-nbt-api:2.9.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("com.h2database:h2:2.1.212")
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }
    test {
        useJUnitPlatform()
    }

    shadowJar {
        listOf(
            "com.google.inject",
            "com.google.common",
            "com.google.gson",
            "com.zaxxer",
            "com.mysql.cj",
            "dev.triumphteam",
            "co.aikar.locales",
            "co.aikar.commands",
            "net.kyori",
            "de.tr7zw.changeme.nbtapi",
            "me.bristermitten.mittenlib"
        ).forEach {
            relocate(it, "me.bristermitten.vouchers.$it")
        }
        minimize()
        archiveFileName.set("app.jar")
    }
}

tasks.register<Copy>("copyJarToServerPlugins") {
    from(tasks.getByPath("proguardJar"))
    into(layout.projectDirectory.dir("server/plugins"))
}

tasks.register("depsize") {
    description = "Prints dependencies for \"default\" configuration"
    doLast {
        listConfigurationDependencies(configurations["default"])
    }
}

tasks.register("depsize-all-configurations") {
    description = "Prints dependencies for all available configurations"
    doLast {
        configurations
            .filter { it.isCanBeResolved }
            .forEach { listConfigurationDependencies(it) }
    }
}

fun listConfigurationDependencies(configuration: Configuration) {
    val formatStr = "%,10.2f"

    val size = configuration.sumOf { it.length() / (1024.0 * 1024.0) }

    val out = StringBuffer()
    out.append("\nConfiguration name: \"${configuration.name}\"\n")
    if (size > 0) {
        out.append("Total dependencies size:".padEnd(65))
        out.append("${String.format(formatStr, size)} Mb\n\n")

        configuration.sortedBy { -it.length() }
            .forEach {
                out.append(it.name.padEnd(65))
                out.append("${String.format(formatStr, (it.length() / 1024.0))} kb\n")
            }
    } else {
        out.append("No dependencies found")
    }
    println(out)
}

tasks.jar { enabled = false }

tasks.register<ProGuardTask>("proguardJar") {
    outputs.upToDateWhen { false }
    dependsOn("shadowJar")

    configuration("proguard-rules.pro")
    injars(tasks.getByPath("shadowJar"))
    outjars("${buildDir}/libs/${rootProject.name}_proguard.jar")

    val javaHome = System.getProperty("java.home")
    // Automatically handle the Java version of this build.
    if (System.getProperty("java.version").startsWith("1.")) {
        // Before Java 9, the runtime classes were packaged in a single jar file.
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        // As of Java 9, the runtime classes are packaged in modular jmod files.
        libraryjars(
            // filters must be specified first, as a map
            mapOf(
                "jarfilter" to "!**.jar",
                "filter" to "!module-info.class"
            ),
            "$javaHome/jmods/java.base.jmod"
        )
    }

    libraryjars(
        configurations.findByName("onClasspath")!!.files
                + configurations.findByName("proguard")!!.files
                + configurations.findByName("shade")!!.files
    )

    keepkotlinmetadata()

    verbose()

}
