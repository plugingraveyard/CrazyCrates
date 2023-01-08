plugins {
    `java-library`

    `maven-publish`

    id("com.modrinth.minotaur") version "2.6.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    /**
     * Placeholders
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.mvdw-software.com/content/groups/public/")

    /**
     * NBT API
     */
    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.codemc.io/repository/nms")
}

dependencies {
    implementation(project(":api"))

    implementation(project(":v1_8_R3"))
    implementation(project(":v1_12_R1"))
    implementation(project(":v1_16_R3"))
    implementation(project(":v1_17_R1"))

    implementation("de.tr7zw", "nbt-data-api", "2.11.1")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")
    implementation("org.jetbrains", "annotations", "23.0.0")

    compileOnly("org.spigotmc", "spigot-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.8")

    compileOnly("be.maximvdw", "MVdWPlaceholderAPI", "3.1.1-SNAPSHOT") {
        exclude(group = "org.spigotmc")
        exclude(group = "org.bukkit")
    }

    compileOnly("com.sainttx.holograms", "holograms", "2.12")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc")
        exclude(group = "org.bukkit")
    }
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")
val buildVersion = "${rootProject.version}-b$buildNumber"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")

        listOf(
            "de.tr7zw",
            "org.bstats",
            "org.jetbrains"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("crazycrates")

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set("${rootProject.version}")

        versionType.set("alpha")

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(listOf("1.8", "1.8.8", "1.12.2", "1.16.5", "1.17.1"))
        loaders.addAll(listOf("spigot", "paper", "purpur"))

        //<h3>The first release for CrazyCrates on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set("""
                <h2>Notice:</h2>
                 <p>This is only for Legacy ( 1.8 - 1.17.1 ) Support, No new features will be added.</p>
                <h2>Bug Fixes:</h2>
                 <p>N/A</p>
            """.trimIndent())
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to if (buildNumber != null) buildVersion else rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}

publishing {
    repositories {
        maven("https://repo.crazycrew.us/legacy") {
            name = "crazycrew"
            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "${extra["plugin_group"]}"
            artifactId = rootProject.name.toLowerCase()
            version = "${project.version}"
            from(components["java"])
        }
    }
}