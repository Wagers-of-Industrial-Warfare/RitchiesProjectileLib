# Ritchie's Projectile Library

Code reused in multiple projects by rbasamoyai.

Adds the following features:
- `#ritchiesprojectilelib:precise_motion`: an entity type tag that sends more
  detailed movement and position data to clients.
- A configurable chunkloading system oriented towards modded long-range and
  fast projectiles.
  - Unloads forceloaded chunks that are no longer in use
  - Loads a portion of forceloaded chunks at a time to reduce performance
    impact while still allowing for many long distance projectiles
- Screen shake effect for mods, particularly those focused on firearms and
  artillery

---

## Depending on RPL

First, add the following maven to your `repositories` block:
```gradle
repositories {
    //...
    maven { // Ritchie's Projectile Library
        url = "https://maven.realrobotix.me/master/"
        content {
            includeGroup("com.rbasamoyai") // THIS IS IMPORTANT
        }
    }
}
```

RPL artefacts are formatted as follows:
```
ritchiesprojectilelib-<rpl_version>+mc.<minecraft_version>-<rpl_platform>-build.<rpl_build_number>
```
where `rpl_version` is the version of Ritchie's Projectile Library,
`minecraft_version` is the Minecraft version, `platform` is the modloader
platform, of which `forge`, `fabric`, and `common` JARs are provided, and
`build_number` is the build number.

The latest build can be found in the GitHub actions history:
https://github.com/Wagers-of-Industrial-Warfare/RitchiesProjectileLib/actions

Depending on your `build.gradle` setup, add one of the following to your
`dependencies` block:

__ForgeGradle (1.18.2-1.20.1)__
```gradle
implementation fg.deobf("com.rbasamoyai:ritchiesprojectilelib:<rpl_version>+mc.<minecraft_version>-forge-build.<rpl_build_number>") { transitive = false }
```

__Loom (Fabric/Architectury)__
```gradle
modImplementation("com.rbasamoyai:ritchiesprojectilelib:<rpl_version>+mc.<minecraft_version>-<rpl_platform>-build.<rpl_build_number>") { transitive = false }
```
`rpl_platform` should either be `common`, `forge`, or `fabric` depending on
the Architectury subproject you add the mod to. For Fabric, only use `fabric`.

---

## Including RPL in your mod jar

Currently, RPL is not downloadable from platforms such as CurseForge and
Modrinth. This may change in the future, but for now you must include RPL
in your mod JAR if you use it in your mod code.

To include RPL in your mod JAR, add the appropriate code to the `dependencies`
block:

__ForgeGradle (1.18.2-1.20.1)__
```gradle
jarJar("com.rbasamoyai:ritchiesprojectilelib:${rpl_version}+mc.${minecraft_version}-forge-build.${rpl_build_number}") {
    transitive = false
    jarJar.ranged(it, '[2.0.0,2.0.1)') // Change this based on rpl_version. jarJar.pin seems to not work.
}
// Same as the previous section
implementation fg.deobf("com.rbasamoyai:ritchiesprojectilelib:${rpl_version}+${minecraft_version}-forge") { transitive = false }
```
For more information on how to setup Jar-in-Jar in ForgeGradle, go to
https://docs.minecraftforge.net/en/fg-5.x/dependencies/jarinjar/.

__Loom (Fabric/Architectury)__
```gradle
// Change the previous section
modImplementation(include("com.rbasamoyai:ritchiesprojectilelib:${rpl_version}+mc.${minecraft_version}-${rpl_platform}-build.${rpl_build_number}")) { transitive = false }
```
