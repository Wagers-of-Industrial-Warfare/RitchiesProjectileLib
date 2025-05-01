@file:Suppress("UnstableApiUsage")


plugins {
	`maven-publish`
	id("dev.architectury.loom")
	id("architectury-plugin")
	id("com.gradleup.shadow")
}

val loader = prop("loom.platform")!!
val minecraft_version: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
	"No common project for $project"
}.project

version = "${mod.version}${if (buildData.release) "" else "-dev"}+mc.${minecraft_version}-neoforge${if (buildData.ci) "-build.${buildData.buildNumber}" else ""}"
group = "${mod.group}.$loader"
base.archivesName = mod.id

architectury {
	platformSetupLoomIde()
	neoForge()
}

val commonBundle: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

configurations {
	compileClasspath.get().extendsFrom(commonBundle)
	runtimeClasspath.get().extendsFrom(commonBundle)
	get("developmentNeoForge").extendsFrom(commonBundle)
}

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = common.loom.accessWidenerPath
	runConfigs.all {
		isIdeConfigGenerated = true
		runDir = "../../../run"
		vmArgs("-Dmixin.debug.export=true")
	}
}

repositories {
    maven("https://maven.neoforged.net/releases/") //todo: maybe strictMaven
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft_version")
	mappings(loom.layered {
		officialMojangMappings { nameSyntheticMembers = false }
		parchment("org.parchmentmc.data:parchment-${minecraft_version}:${common.mod.dep("parchment_version")}@zip")
	})
    "neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")

	commonBundle(project(common.path, "namedElements")) { isTransitive = false }
	shadowBundle(project(common.path, "transformProductionNeoForge")) { isTransitive = false }
}

java {
	withSourcesJar()
	val java = if (stonecutter.eval(minecraft_version, ">=1.20.5"))
		JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	targetCompatibility = java
	sourceCompatibility = java
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifact(tasks.remapJar)
			artifact(tasks.remapSourcesJar)
			group = mod.group
			artifactId = mod.id
		}
	}
}

tasks.jar {
	archiveClassifier = "dev"
}

tasks.remapJar {
	injectAccessWidener = true
	input = tasks.shadowJar.get().archiveFile
	archiveClassifier = null
	dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
	configurations = listOf(shadowBundle)
	archiveClassifier = "dev-shadow"
	exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
	properties(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
		"id" to mod.id,
		"name" to mod.id,
		"version" to mod.version,
		"neoforge_version" to common.mod.dep("neoforge_loader"),//.substringBefore("."), // only specify major version of forge
		"minecraft_version" to minecraft_version,
	)
}

sourceSets.main {
	resources { // include generated resources in resources
		srcDir("src/generated/resources")
		exclude("src/generated/resources/.cache")
	}
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
	from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
	dependsOn("build")
}
