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

version = "${mod.version}+mc.${minecraft_version}-forge${if (buildData.nightly) "-build.${buildData.buildNumber}" else ""}${if (buildData.ci) "" else "-dev"}"
group = "${mod.group}.$loader"
base.archivesName = mod.id

architectury {
	platformSetupLoomIde()
	forge()
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
	get("developmentForge").extendsFrom(commonBundle)
}

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = common.loom.accessWidenerPath
	forge.convertAccessWideners = true
	forge.mixinConfigs(
		"${mod.id}-forge.mixins.json",
		"${mod.id}.mixins.json",
	)

	runConfigs.all {
		isIdeConfigGenerated = true
		runDir = "../../../run"
		vmArgs("-Dmixin.debug.export=true")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft_version")
	mappings(loom.layered {
		officialMojangMappings { nameSyntheticMembers = false }
		parchment("org.parchmentmc.data:parchment-${minecraft_version}:${common.mod.dep("parchment_version")}@zip")
	})
    "forge"("net.minecraftforge:forge:$minecraft_version-${common.mod.dep("forge_loader")}")

    "io.github.llamalad7:mixinextras-forge:${common.mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }

	commonBundle(project(common.path, "namedElements")) { isTransitive = false }
	shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }
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
	properties(listOf("META-INF/mods.toml"),
		"id" to mod.id,
		"name" to mod.id,
		"version" to mod.version,
		"forge_version" to common.mod.dep("forge_loader").substringBefore("."), // only specify major version of forge
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
