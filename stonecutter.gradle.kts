plugins {
	`maven-publish`
	id("dev.kikugie.stonecutter")
	id("dev.architectury.loom") version "1.9.+" apply false
	id("architectury-plugin") version "3.4.+" apply false
	id("com.gradleup.shadow") version "8.3.5" apply false
}
stonecutter active "1.20.1" /* [SC] DO NOT EDIT */

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
