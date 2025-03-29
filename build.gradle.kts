@file:Suppress("UnstableApiUsage")

plugins {
	`maven-publish`
	id("dev.architectury.loom")
	id("architectury-plugin")
}

val minecraft_version = stonecutter.current.version

version = "${mod.version}${if (buildData.release) "" else "-dev"}+mc.${minecraft_version}-common${if (buildData.ci) "-build.${buildData.buildNumber}" else ""}"
group = "${mod.group}.common"
base.archivesName = mod.id

architectury.common(stonecutter.tree.branches.mapNotNull {
	if (stonecutter.current.project !in it) null
	else it.project.prop("loom.platform")
})

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

tasks.build {
	group = "versioned"
	description = "Must run through 'chiseledBuild'"
}
