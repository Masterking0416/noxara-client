# Noxara Client

Noxara Client is an independent, open-source Fabric client derived from [Meteor Client](https://github.com/MeteorDevelopment/meteor-client), targeting **Minecraft Java Edition 26.3**.

The 26.3 compatibility base follows the work in [MeteorDevelopment/meteor-client PR #6698](https://github.com/MeteorDevelopment/meteor-client/pull/6698) by dekrom. That pull request is still open upstream; Noxara runs its own build verification and must be tested independently.

> **Development status:** the 26.3 port is under active integration. A passing CI build is necessary, but not sufficient, to call a version playable; runtime launch and mixin checks are still required.

## Development target

- Minecraft Java Edition: 26.3
- Mod loader: Fabric
- Java toolchain: 25
- Build system: Gradle and Fabric Loom
- GUI direction: dark graphite, violet accent, restrained motion, readable type

## Build locally

1. Install a JDK 25 distribution.
2. Clone this repository and check out the `noxara/mc-26.3-tested-port` branch.
3. Run the Gradle build:
   - Windows: `gradlew.bat build`
   - Linux/macOS: `./gradlew build`

The compiled JAR is placed under `build/libs`. Test it in a clean Minecraft 26.3 Fabric instance before sharing it.

## Roadmap

1. Keep the Minecraft 26.3 build compiling and fix API/mixin incompatibilities.
2. Redesign the ClickGUI and HUD while retaining Meteor's module/settings foundation.
3. Port selected Wurst features one by one, with compatibility testing and attribution.
4. Evaluate Trouser-Streak integration separately. Its GitHub repository does not currently declare a license, so its code must not be copied or redistributed until the applicable permission is established.
5. Produce release builds only after clean-launch testing.

## Upstream projects and licensing

This repository contains code derived from Meteor Client, which is licensed under the **GNU General Public License v3.0 (GPL-3.0)**. The upstream license is retained in `LICENSE`. This derivative remains under GPL-3.0; comply with source-disclosure and attribution requirements when redistributing modified builds.

- [Meteor Client](https://github.com/MeteorDevelopment/meteor-client) — primary codebase.
- [Meteor 26.3 port PR #6698](https://github.com/MeteorDevelopment/meteor-client/pull/6698) — current compatibility port reference.
- [Wurst7](https://github.com/Wurst-Imperium/Wurst7) — candidate source for selected feature ports, subject to compatibility and license review.
- [Trouser-Streak](https://github.com/etianl/Trouser-Streak) — planned integration pending a clear license/permission review.

Noxara is an independent community fork and is not an official Meteor Development or Wurst release.
