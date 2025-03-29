@file:Suppress("UnstableApiUsage")


plugins {
	`maven-publish`
	id("dev.architectury.loom")
	id("architectury-plugin")
	id("com.github.johnrengelman.shadow")
}

val loader = property("loom.platform")!!
val minecraft_version: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
	"No common project for $project"
}.project

version = "${mod.version}${if (buildData.release) "" else "-dev"}+mc.${minecraft_version}-fabric${if (buildData.ci) "-build.${buildData.buildNumber}" else ""}"
group = "${mod.group}.$loader"
base.archivesName = mod.id

architectury {
	platformSetupLoomIde()
	fabric()
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
	get("developmentFabric").extendsFrom(commonBundle)
}

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = common.loom.accessWidenerPath
	runConfigs {
		all {
			isIdeConfigGenerated = true
			runDir = "../../../run"
			//vmArgs("-Dmixin.debug.export=true")
		}
	}
}

repositories {
	maven("https://mvn.devos.one/snapshots/") // Porting Lib
	strictMaven("https://jitpack.io/", "com.github.llamalad7.mixinextras") // Mixin Extras, Fabric ASM
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft_version")
	mappings(loom.layered {
		officialMojangMappings { nameSyntheticMembers = false }
		parchment("org.parchmentmc.data:parchment-${minecraft_version}:${common.mod.dep("parchment_version")}@zip")
	})
	modImplementation("net.fabricmc:fabric-loader:${common.mod.dep("fabric_loader")}")
	modApi("net.fabricmc.fabric-api:fabric-api:${common.mod.dep("fabric_api_version")}+${minecraft_version}")

    for (module in mod.dep("porting_lib_modules").split(",")) {
        modApi(include("io.github.fabricators_of_create.Porting-Lib:$module:${common.mod.dep("porting_lib_version")}") { exclude(module = "loot") })
    }

    modApi(include("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${common.mod.dep("config_api_version")}"){})
    modApi(include("com.electronwill.night-config:core:${common.mod.dep("night_config_version")}"){})
    modApi(include("com.electronwill.night-config:toml:${common.mod.dep("night_config_version")}"){})

    implementation("com.google.code.findbugs:jsr305:3.0.1")

	commonBundle(project(common.path, "namedElements")) { isTransitive = false }
	shadowBundle(project(common.path, "transformProductionFabric")) { isTransitive = false }
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

tasks.shadowJar {
	exclude("architectury.common.json")
	configurations = listOf(shadowBundle)
	archiveClassifier = "dev-shadow"
}

tasks.remapJar {
	injectAccessWidener = true
	input = tasks.shadowJar.get().archiveFile
	archiveClassifier = null
	dependsOn(tasks.shadowJar)
}

tasks.jar {
	archiveClassifier = "dev"
}

tasks.processResources {
	properties(listOf("fabric.mod.json"),
		"version" to mod.version,
		"fabric_loader_version" to mod.dep("fabric_loader"),
		"fabric_api_version" to mod.dep("fabric_api_version"),
		"minecraft_version" to minecraft_version,
        "porting_lib_version" to mod.dep("porting_lib_version")
	)
}

sourceSets.main {
	resources { // include generated resources in resources
		srcDir("src/generated/resources")
		exclude("src/generated/resources/.cache")
	}
}

tasks.build {
	group = "versioned"
	description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
	group = "versioned"
	description = "Must run through 'chiseledBuild'"
	from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
	dependsOn("build")
}
