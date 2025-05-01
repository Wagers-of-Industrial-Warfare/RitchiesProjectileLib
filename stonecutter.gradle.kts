import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
	`maven-publish`
	id("dev.kikugie.stonecutter")
	id("dev.architectury.loom") version "1.9.+" apply false
	id("architectury-plugin") version "3.4.+" apply false
	id("com.gradleup.shadow") version "8.3.5" apply false
}
stonecutter active "1.21.1" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
	group = "project"
	ofTask("buildAndCollect")
}

stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
	group = "project"
	ofTask("publish")
}

for (it in stonecutter.tree.branches) {
	if (it.id.isEmpty()) continue
	val loader = it.id.upperCaseFirst()

	// Builds loader-specific versions into `build/libs/{mod.version}/{loader}`
	stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
		group = "project"
		versions { branch, _ -> branch == it.id }
		ofTask("buildAndCollect")
	}

	// Publishes loader-specific versions
	stonecutter registerChiseled tasks.register("chiseledPublish$loader", stonecutter.chiseled) {
		group = "project"
		versions { branch, _ -> branch == it.id }
		ofTask("publish")
	}
}

// Runs active versions for each loader
for (it in stonecutter.tree.nodes) {
	if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
	val types = listOf("Client", "Server")
	val loader = it.branch.id.uppercaseFirstChar()
	for (type in types) it.project.tasks.register("runActive$type$loader") {
		group = "project"
		dependsOn("run$type")
	}
    it.project.tasks.register("buildActive") {
        group = "project"
        dependsOn("buildAndCollect")
    }
}

subprojects {
	apply(plugin = "maven-publish")
	repositories {
		mavenCentral()
		// mappings
		strictMaven("https://maven.parchmentmc.org", "org.parchmentmc.data")
		strictMaven("https://maven.quiltmc.org/repository/release", "org.quiltmc")

		strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
		strictMaven("https://cursemaven.com", "curse.maven")
        strictMaven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/", "fuzs.forgeconfigapiport")
	}
	publishing {
		repositories {
			maven {
				name = "GitHubPackages"
				url = uri("https://maven.pkg.github.com/wagers-of-industrial-warfare/ritchiesprojectilelib")
				credentials {
					username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
					password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
				}
			}
			maven {
				name = "realRobotixMaven"
				url = uri("https://maven.realrobotix.me/ritchiesprojectilelib")
				credentials(PasswordCredentials::class)
			}
			mavenLocal()
		}
	}
}
