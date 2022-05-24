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

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.bristermitten.me/repository/maven-releases/")
    maven("https://repo.bristermitten.me/repository/maven-snapshots/")
    maven("https://repo.triumphteam.dev/releases/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(files("lib/Vouchersv1.8.20_2.jar"))
    compileOnly("net.luckperms:api:5.4")

    val mittenLibVersion = "1.0.4-SNAPSHOT"
    implementation("me.bristermitten:mittenlib-core:$mittenLibVersion")
    annotationProcessor("me.bristermitten:mittenlib-annotation-processor:$mittenLibVersion")

    implementation("dev.triumphteam:triumph-gui:3.1.2")
    @Suppress("GradlePackageUpdate") // Keeping this lower to support Java 8
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("mysql:mysql-connector-java:8.0.29")


    implementation("net.kyori:adventure-api:4.10.1")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
    implementation("net.kyori:adventure-text-minimessage:4.10.1")

    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")

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
            "com.google.common",
            "com.google.inject",
            "com.zaxxer",
            "com.mysql.cj",
            "dev.triumphteam",
            "co.aikar.locales",
            "co.aikar.commands",
            "net.kyori"
        ).forEach {
            relocate(it, "me.bristermitten.claimboxes.$it")
        }
        minimize()
    }
}

tasks.register<Copy>("copyJarToServerPlugins") {
    from(tasks.getByPath("shadowJar"))
    into(layout.projectDirectory.dir("server/plugins"))
}
