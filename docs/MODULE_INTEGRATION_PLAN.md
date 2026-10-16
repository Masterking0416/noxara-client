# Noxara module integration plan

Noxara's target is a full feature audit and merge of Meteor and Wurst7, not a small selection. Overlapping behavior should share one Noxara module/settings page; genuinely unique functionality from either client should remain available as native Noxara modules. This is a working plan, not a claim that all features are already implemented.

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
- **AutoSwim:** integrated Wurst's forward swimming sprint behavior as an optional `auto-swim` setting in Meteor's existing Sprint module, avoiding a duplicate module toggle. Build and in-game validation pending.
- **3x3:** added a dedicated World module that packet-attempts a 3×3 plane around the block under the crosshair, respecting breakability and a configurable reach cap. Server permissions, mining rules, and anti-cheat can still reject instant-break attempts; Java 25 build and in-game validation pending.
- **Noxara category navigation:** reordered the sidebar into a more natural Combat → Movement → Player → Render → World → Misc flow; module entries remain alphabetically sorted within each view.
- **Noxara GUI foundation:** dark graphite surfaces, violet accents, improved contrast, left-aligned module labels, and category icons are the current baseline. The custom layout, smoother animations and full visual polish are still outstanding.

## Full inventory and integration sequence

The complete Wurst registered-feature audit is tracked in WURST_METEOR_FEATURE_MATRIX.md. That matrix defines the full scope; the short initial list above is not the whole target.

1. Audit every Wurst entry against Meteor by behavior, settings and dependencies.
2. Merge shared behavior into one module/settings page instead of separate duplicate toggles.
3. Keep unique Wurst features as native Noxara modules where technically compatible, and preserve Meteor-only functionality too.
4. Process modules in tested category-sized batches, compiling and validating in-game after each batch.
5. Keep a clear status for each feature so nothing is silently omitted.

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
