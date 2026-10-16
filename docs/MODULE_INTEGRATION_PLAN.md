# Noxara module integration plan

Noxara is intended to combine Meteor's module system with selected Wurst7 features, without registering two modules that perform the same job. This is a working plan, not a claim that all listed features are already implemented.

## Integration rules

1. **Same core behavior:** extend Meteor's existing module with the extra controls, rather than adding a second toggle with the same job.
2. **Related but different behavior:** keep a separate module when it has its own meaningful workflow (for example, ordinary Nuker versus a more constrained/legit variant).
3. **Wurst-only behavior:** port it as a native Noxara/Meteor module, with the usual Noxara settings, categories, binds, profiles and GUI.
4. **Settings:** keep settings grouped, label them clearly, add visibility conditions for dependent options, and give safe defaults. Avoid settings that do nothing or duplicate another setting.
5. **Compatibility and attribution:** port against Minecraft 26.3 APIs; retain upstream license notices and attribution where applicable. Trouser-Streak is tracked separately because its current main branch targets 26.2 and the repository did not expose a license file when checked. Do not copy its source until permission/licensing is resolved.
6. **Validation:** every batch must compile on the Noxara development branch and then be tested in a disposable local world. Never merge this work into `master` without explicit approval.

## First merged features in code

- **AutoTool:** keeps Meteor's enchantment preference, Ender Chest/Silk Touch rule, Fortune rule, anti-break percentage, switch delay and whitelist/blacklist. Adds Wurst-inspired optional sword selection, a non-damageable/empty-hotbar fallback, and a remaining-durability repair threshold.
- **FastUse + FastPlace:** keeps Meteor's item selection and cooldown controls. Adds an optional Wurst-style right-click-delay reset, with a setting to restrict it to holding blocks. It is one combined module rather than two competing toggles.
- **Noxara GUI foundation:** dark graphite surfaces, violet accents, improved contrast, left-aligned module labels, and category icons are the current baseline. The custom layout, smoother animations and full visual polish are still outstanding.

## Next implementation queue

### High priority: useful unique features
- AutoFarm / crop harvesting
- AutoLibrarian / villager trade helper
- FastLadder
- AutoPotion and AutoDrop
- AutoSwitch and AutoSword behavior, after checking what existing Meteor modules already cover
- Tunneller and the more configurable excavation/building workflows
- Navigator / searchable feature browser improvements
- Missing visual options such as NoFog, NoHurtCam and extra ESP/radar overlays

### Merge and settings audit
- Compare every Wurst feature to Meteor's existing modules by behavior, not only by module name.
- Extend existing modules where a setting meaningfully fits.
- Keep genuinely different variants separate but explain their distinction in the module description.
- Review movement, combat, inventory, rendering, world and utility categories in batches.

### Exclusions from automatic bulk copying
- Do not blindly copy all source files or dependencies from Wurst or third-party addons.
- Do not include server-crash/forced-operator/dupe/exploit modules as part of an indiscriminate port.
- Do not treat a green compile as proof of in-game correctness.

## Source references

- Meteor Client: https://github.com/MeteorDevelopment/meteor-client
- Wurst7: https://github.com/Wurst-Imperium/Wurst7
- Trouser-Streak: https://github.com/etianl/Trouser-Streak
- Noxara Minecraft 26.3 base work: https://github.com/Masterking0416/noxara-client/tree/noxara/mc-26.3-tested-port

A name-only comparison of the Wurst7 `HackList` and Meteor's `Modules` registry is useful for triage, but not authoritative: aliases and different implementations can still cover the same capability, so each feature needs a behavior-level review.
