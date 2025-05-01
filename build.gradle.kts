@file:Suppress("UnstableApiUsage")

plugins {
	`maven-publish`
	id("dev.architectury.loom")
	id("architectury-plugin")
    id("com.gradleup.shadow")
}

val minecraft_version = stonecutter.current.version

version = "${mod.version}${if (buildData.release) "" else "-dev"}+mc.${minecraft_version}-common${if (buildData.ci) "-build.${buildData.buildNumber}" else ""}"
group = "${mod.group}.common"
base.archivesName = mod.id

architectury.common(stonecutter.tree.branches.mapNotNull {
	if (stonecutter.current.project !in it) null
	else it.project.prop("loom.platform")
})

stonecutter {
    replacements {
        string {
            direction = eval(minecraft_version, "<1.21")
            phase = "LAST"
            from = "ModConfigSpec"
            to = "ForgeConfigSpec"
            id = "RPLConfigs"
        }
    }
}

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings(loom.layered {
		officialMojangMappings { nameSyntheticMembers = false }
		parchment("org.parchmentmc.data:parchment-${minecraft_version}:${mod.dep("parchment_version")}@zip")
	})
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    modApi("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${mod.dep("config_api_version")}")
    implementation("com.google.code.findbugs:jsr305:3.0.1")
}


java {
	withSourcesJar()
	val java = if (stonecutter.eval(minecraft_version, ">=1.20.5"))
		JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	targetCompatibility = java
	sourceCompatibility = java
}

// todo: for some reason this breaks dependents
/*publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.remapJar)
            artifact(tasks.remapSourcesJar)
            group = mod.group
            artifactId = mod.id
        }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/common"))
    dependsOn("build")
}*/
