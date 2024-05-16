# Ritchie's Projectile Library

Code reused in multiple projects by rbasamoyai.

Adds the following features:
- `#ritchiesprojectilelib:precise_motion`: an entity type tag that sends more detailed movement and position data to clients.
- A configurable chunkloading system oriented towards modded long-range and fast projectiles.
  - Unloads forceloaded chunks that are no longer in use
  - Loads a portion of forceloaded chunks at a time to reduce performance impact while still allowing for many long distance travels.  

---

## Depending on RPL

First, add the following to your `repositories` block:
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

RPL artefacts, represented with `<rpl_version>` in the following examples, are
formatted as the RPL version number and the commit hash, like this: `1.0.0-3a68f88`.
(NOTE: this will likely change to build number instead of commit hash.)

The commit hash for the latest build can be found in the Github branch history.

Depending on your `build.gradle` setup, add one of the following to your `dependencies`
block:

__ForgeGradle (1.18.2-1.20.1)__
```gradle
implementation fg.deobf("com.rbasamoyai:ritchiesprojectilelib:<rpl_version>+<minecraft_version>-forge") { transitive = false }
```

__Loom (Fabric/Architectury)__
```gradle
modImplementation("com.rbasamoyai:ritchiesprojectilelib:<rpl_version>+<minecraft_version>-<branch>") { transitive = false }
```
`branch` should either be `common`, `forge`, or `fabric` depending on which
Architectury subproject you add the mod to. For Fabric, only use `fabric`.

---

## Including RPL in your mod jar

Currently, RPL is not downloadable from platforms such as CurseForge and Modrinth.
This may change in the future, but for now you must include RPL in your mod JAR
if you use it in your mod code.

To include RPL in your mod JAR, add the appropriate code to the `dependencies`
block:

__ForgeGradle (1.18.2-1.20.1)__
```gradle
jarJar("com.rbasamoyai:ritchiesprojectilelib:${rpl_version}+${minecraft_version}-forge") {
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
modImplementation(include("com.rbasamoyai:ritchiesprojectilelib:<rpl_version>+<minecraft_version>-<branch>")) { transitive = false }
```