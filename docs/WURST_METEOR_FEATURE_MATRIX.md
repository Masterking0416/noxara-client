# Wurst7 ↔ Meteor exhaustive module inventory (draft)

This file tracks every registered Wurst7 hack against the Noxara branch's Meteor-derived module registry. It is an audit matrix, not a claim that all features are already ported.

**Scope checked:** Wurst upstream HackList.java parsed on 2026-10-16 (156 registered Wurst features). The current Noxara Meteor-derived registry has 169 module classes in Modules.java. Name matches are only candidates; final decisions require comparing real behavior and settings.

## Rules for the finished client

- If two modules do the same job, the finished Noxara module should have one toggle and one settings page containing the useful settings from both.
- If a feature is a distinct mode of existing behavior, make it a setting where it fits cleanly.
- If a feature is truly unique, add it as a native Noxara module under the matching category.
- Do not duplicate the same functionality under two separately enabled toggles.
- Preserve original license notices and attribution. Confirm licensing before importing code from repositories without an explicit license.
- Rows requiring special review should not be blindly imported; verify 26.3 compatibility and their effects on other players/servers.

## Wurst registered-feature audit

| Wurst feature | Upstream class | Initial disposition |
|---|---|---|
| Aim Assist | AimAssistHack | Unique-feature candidate — compare behavior and port if compatible |
| Air Place | AirPlaceHack | Potential overlap with AirPlace — verify behavior and combine settings |
| Anchor Aura | AnchorAuraHack | Potential overlap with AnchorAura — verify behavior and combine settings |
| Anti Afk | AntiAfkHack | Potential overlap with AntiAFK — verify behavior and combine settings |
| Anti Blind | AntiBlindHack | Unique-feature candidate — compare behavior and port if compatible |
| Anti Cactus | AntiCactusHack | Unique-feature candidate — compare behavior and port if compatible |
| Anti Entity Push | AntiEntityPushHack | Unique-feature candidate — compare behavior and port if compatible |
| Anti Hunger | AntiHungerHack | Potential overlap with AntiHunger — verify behavior and combine settings |
| Anti Knockback | AntiKnockbackHack | Candidate merge with Velocity — verify behavior first |
| Anti Spam | AntiSpamHack | Unique-feature candidate — compare behavior and port if compatible |
| Anti Water Push | AntiWaterPushHack | Unique-feature candidate — compare behavior and port if compatible |
| Anti Wobble | AntiWobbleHack | Unique-feature candidate — compare behavior and port if compatible |
| Arrow Dmg | ArrowDmgHack | Unique-feature candidate — compare behavior and port if compatible |
| Attribute Swap | AttributeSwapHack | Potential overlap with AttributeSwap — verify behavior and combine settings |
| Auto Armor | AutoArmorHack | Potential overlap with AutoArmor — verify behavior and combine settings |
| Auto Build | AutoBuildHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Complete | AutoCompleteHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Drop | AutoDropHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Leave | AutoLeaveHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Librarian | AutoLibrarianHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Eat | AutoEatHack | Potential overlap with AutoEat — verify behavior and combine settings |
| Auto Farm | AutoFarmHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Fish | AutoFishHack | Potential overlap with AutoFish — verify behavior and combine settings |
| Auto Mine | AutoMineHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Potion | AutoPotionHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Reconnect | AutoReconnectHack | Potential overlap with AutoReconnect — verify behavior and combine settings |
| Auto Respawn | AutoRespawnHack | Potential overlap with AutoRespawn — verify behavior and combine settings |
| Auto Sign | AutoSignHack | Potential overlap with AutoSign — verify behavior and combine settings |
| Auto Soup | AutoSoupHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Sprint | AutoSprintHack | Candidate merge with Sprint — verify behavior first |
| Auto Steal | AutoStealHack | Candidate merge with InventoryTweaks — verify behavior first |
| Auto Swim | AutoSwimHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Switch | AutoSwitchHack | Unique-feature candidate — compare behavior and port if compatible |
| Auto Sword | AutoSwordHack | Candidate merge with AutoWeapon — verify behavior first |
| Auto Tool | AutoToolHack | Combine into AutoTool — initial settings committed; validation pending |
| Auto Totem | AutoTotemHack | Potential overlap with AutoTotem — verify behavior and combine settings |
| Auto Walk | AutoWalkHack | Potential overlap with AutoWalk — verify behavior and combine settings |
| Barrier Esp | BarrierEspHack | Candidate merge with BlockESP — verify behavior first |
| Base Finder | BaseFinderHack | Unique-feature candidate — compare behavior and port if compatible |
| Blink | BlinkHack | Potential overlap with Blink — verify behavior and combine settings |
| Boat Fly | BoatFlyHack | Unique-feature candidate — compare behavior and port if compatible |
| Bonemeal Aura | BonemealAuraHack | Unique-feature candidate — compare behavior and port if compatible |
| Bow Aimbot | BowAimbotHack | Potential overlap with BowAimbot — verify behavior and combine settings |
| Build Random | BuildRandomHack | Unique-feature candidate — compare behavior and port if compatible |
| Bunny Hop | BunnyHopHack | Unique-feature candidate — compare behavior and port if compatible |
| Camera Distance | CameraDistanceHack | Candidate merge with CameraTweaks — verify behavior first |
| Camera No Clip | CameraNoClipHack | Candidate merge with Freecam — verify behavior first |
| Cave Finder | CaveFinderHack | Unique-feature candidate — compare behavior and port if compatible |
| Chat Translator | ChatTranslatorHack | Unique-feature candidate — compare behavior and port if compatible |
| Chest Esp | ChestEspHack | Candidate merge with StorageESP — verify behavior first |
| Click Aura | ClickAuraHack | Unique-feature candidate — compare behavior and port if compatible |
| Click Gui | ClickGuiHack | Unique-feature candidate — compare behavior and port if compatible |
| Crash Chest | CrashChestHack | Special review required; do not blindly port destructive/server-abuse behavior |
| Creative Flight | CreativeFlightHack | Unique-feature candidate — compare behavior and port if compatible |
| Criticals | CriticalsHack | Potential overlap with Criticals — verify behavior and combine settings |
| Crystal Aura | CrystalAuraHack | Potential overlap with CrystalAura — verify behavior and combine settings |
| Derp | DerpHack | Unique-feature candidate — compare behavior and port if compatible |
| Dolphin | DolphinHack | Unique-feature candidate — compare behavior and port if compatible |
| Excavator | ExcavatorHack | Potential overlap with Excavator — verify behavior and combine settings |
| Extra Elytra | ExtraElytraHack | Candidate merge with ElytraFly — verify behavior first |
| Fancy Chat | FancyChatHack | Unique-feature candidate — compare behavior and port if compatible |
| Fast Break | FastBreakHack | Unique-feature candidate — compare behavior and port if compatible |
| Fast Ladder | FastLadderHack | Candidate merge with FastClimb — verify behavior first |
| Fast Place | FastPlaceHack | Combine into FastUse — initial settings committed; validation pending |
| Feed Aura | FeedAuraHack | Unique-feature candidate — compare behavior and port if compatible |
| Fight Bot | FightBotHack | Unique-feature candidate — compare behavior and port if compatible |
| Fish | FishHack | Unique-feature candidate — compare behavior and port if compatible |
| Flight | FlightHack | Potential overlap with Flight — verify behavior and combine settings |
| Follow | FollowHack | Unique-feature candidate — compare behavior and port if compatible |
| Force Op | ForceOpHack | Special review required; do not blindly port destructive/server-abuse behavior |
| Freecam | FreecamHack | Potential overlap with Freecam — verify behavior and combine settings |
| Fullbright | FullbrightHack | Potential overlap with Fullbright — verify behavior and combine settings |
| Glide | GlideHack | Unique-feature candidate — compare behavior and port if compatible |
| Hand No Clip | HandNoClipHack | Unique-feature candidate — compare behavior and port if compatible |
| Head Roll | HeadRollHack | Unique-feature candidate — compare behavior and port if compatible |
| Health Tags | HealthTagsHack | Unique-feature candidate — compare behavior and port if compatible |
| High Jump | HighJumpHack | Potential overlap with HighJump — verify behavior and combine settings |
| Infini Chat | InfiniChatHack | Unique-feature candidate — compare behavior and port if compatible |
| Insta Build | InstaBuildHack | Unique-feature candidate — compare behavior and port if compatible |
| Instant Bunker | InstantBunkerHack | Unique-feature candidate — compare behavior and port if compatible |
| Inv Walk | InvWalkHack | Candidate merge with GUIMove — verify behavior first |
| Item Esp | ItemEspHack | Unique-feature candidate — compare behavior and port if compatible |
| Item Generator | ItemGeneratorHack | Unique-feature candidate — compare behavior and port if compatible |
| Jesus | JesusHack | Potential overlap with Jesus — verify behavior and combine settings |
| Jetpack | JetpackHack | Unique-feature candidate — compare behavior and port if compatible |
| Kaboom | KaboomHack | Special review required; do not blindly port destructive/server-abuse behavior |
| Killaura Legit | KillauraLegitHack | Candidate merge with KillAura — verify behavior first |
| Killaura | KillauraHack | Potential overlap with KillAura — verify behavior and combine settings |
| Kill Potion | KillPotionHack | Unique-feature candidate — compare behavior and port if compatible |
| Liquids | LiquidsHack | Unique-feature candidate — compare behavior and port if compatible |
| Lsd | LsdHack | Unique-feature candidate — compare behavior and port if compatible |
| Mace Dmg | MaceDmgHack | Unique-feature candidate — compare behavior and port if compatible |
| Mass Tpa | MassTpaHack | Unique-feature candidate — compare behavior and port if compatible |
| Miley Cyrus | MileyCyrusHack | Unique-feature candidate — compare behavior and port if compatible |
| Mob Esp | MobEspHack | Candidate merge with ESP — verify behavior first |
| Mob Spawn Esp | MobSpawnEspHack | Unique-feature candidate — compare behavior and port if compatible |
| Multi Aura | MultiAuraHack | Candidate merge with KillAura — verify behavior first |
| Name Protect | NameProtectHack | Potential overlap with NameProtect — verify behavior and combine settings |
| Name Tags | NameTagsHack | Potential overlap with Nametags — verify behavior and combine settings |
| Navigator | NavigatorHack | Unique-feature candidate — compare behavior and port if compatible |
| New Chunks | NewChunksHack | Unique-feature candidate — compare behavior and port if compatible |
| No Background | NoBackgroundHack | Candidate merge with BetterChat — verify behavior first |
| No Clip | NoClipHack | Unique-feature candidate — compare behavior and port if compatible |
| No Fall | NoFallHack | Potential overlap with NoFall — verify behavior and combine settings |
| No Fire Overlay | NoFireOverlayHack | Candidate merge with NoRender — verify behavior first |
| No Fog | NoFogHack | Candidate merge with NoRender — verify behavior first |
| No Hurtcam | NoHurtcamHack | Candidate merge with NoRender — verify behavior first |
| No Levitation | NoLevitationHack | Unique-feature candidate — compare behavior and port if compatible |
| No Overlay | NoOverlayHack | Candidate merge with NoRender — verify behavior first |
| No Pumpkin | NoPumpkinHack | Candidate merge with NoRender — verify behavior first |
| No Shield Overlay | NoShieldOverlayHack | Candidate merge with NoRender — verify behavior first |
| No Slowdown | NoSlowdownHack | Candidate merge with NoSlow — verify behavior first |
| No Vignette | NoVignetteHack | Candidate merge with NoRender — verify behavior first |
| No Weather | NoWeatherHack | Candidate merge with WeatherChanger — verify behavior first |
| No Web | NoWebHack | Unique-feature candidate — compare behavior and port if compatible |
| Nuker | NukerHack | Potential overlap with Nuker — verify behavior and combine settings |
| Nuker Legit | NukerLegitHack | Candidate merge with Nuker — verify behavior first |
| Open Water Esp | OpenWaterEspHack | Unique-feature candidate — compare behavior and port if compatible |
| Overlay | OverlayHack | Unique-feature candidate — compare behavior and port if compatible |
| Panic | PanicHack | Unique-feature candidate — compare behavior and port if compatible |
| Parkour | ParkourHack | Potential overlap with Parkour — verify behavior and combine settings |
| Player Esp | PlayerEspHack | Candidate merge with ESP — verify behavior first |
| Portal Esp | PortalEspHack | Unique-feature candidate — compare behavior and port if compatible |
| Portal Gui | PortalGuiHack | Unique-feature candidate — compare behavior and port if compatible |
| Prophunt Esp | ProphuntEspHack | Unique-feature candidate — compare behavior and port if compatible |
| Protect | ProtectHack | Unique-feature candidate — compare behavior and port if compatible |
| Radar | RadarHack | Unique-feature candidate — compare behavior and port if compatible |
| Rainbow Ui | RainbowUiHack | Unique-feature candidate — compare behavior and port if compatible |
| Reach | ReachHack | Potential overlap with Reach — verify behavior and combine settings |
| Remote View | RemoteViewHack | Unique-feature candidate — compare behavior and port if compatible |
| Restock | RestockHack | Unique-feature candidate — compare behavior and port if compatible |
| Safe Walk | SafeWalkHack | Potential overlap with SafeWalk — verify behavior and combine settings |
| Scaffold Walk | ScaffoldWalkHack | Candidate merge with Scaffold — verify behavior first |
| Search | SearchHack | Unique-feature candidate — compare behavior and port if compatible |
| Skin Derp | SkinDerpHack | Unique-feature candidate — compare behavior and port if compatible |
| Sneak | SneakHack | Potential overlap with Sneak — verify behavior and combine settings |
| Snow Shoe | SnowShoeHack | Unique-feature candidate — compare behavior and port if compatible |
| Speed Hack | SpeedHackHack | Unique-feature candidate — compare behavior and port if compatible |
| Speed Nuker | SpeedNukerHack | Candidate merge with Nuker — verify behavior first |
| Spider | SpiderHack | Potential overlap with Spider — verify behavior and combine settings |
| Step | StepHack | Potential overlap with Step — verify behavior and combine settings |
| Template Tool | TemplateToolHack | Unique-feature candidate — compare behavior and port if compatible |
| Throw | ThrowHack | Unique-feature candidate — compare behavior and port if compatible |
| Tillaura | TillauraHack | Candidate merge with KillAura — verify behavior first |
| Timer | TimerHack | Potential overlap with Timer — verify behavior and combine settings |
| Tired | TiredHack | Unique-feature candidate — compare behavior and port if compatible |
| Too Many Hax | TooManyHaxHack | Unique-feature candidate — compare behavior and port if compatible |
| Tp Aura | TpAuraHack | Unique-feature candidate — compare behavior and port if compatible |
| Trajectories | TrajectoriesHack | Potential overlap with Trajectories — verify behavior and combine settings |
| Tree Bot | TreeBotHack | Unique-feature candidate — compare behavior and port if compatible |
| Trigger Bot | TriggerBotHack | Unique-feature candidate — compare behavior and port if compatible |
| Troll Potion | TrollPotionHack | Unique-feature candidate — compare behavior and port if compatible |
| True Sight | TrueSightHack | Unique-feature candidate — compare behavior and port if compatible |
| Tunneller | TunnellerHack | Unique-feature candidate — compare behavior and port if compatible |
| Vein Miner | VeinMinerHack | Potential overlap with VeinMiner — verify behavior and combine settings |
| XRay | XRayHack | Potential overlap with Xray — verify behavior and combine settings |

## Meteor registry

The starting Meteor module registry is the Modules.java file in the source tree. During the audit, mark each Meteor module as Keep + enrich, Merged, Unique Meteor feature, or Needs review.

## First integration commits

- AutoTool additions: https://github.com/Masterking0416/noxara-client/commit/b59b1a737196aad5041f317bc57eec5736b10d6f — latest build/runtime validation pending.
- FastUse/FastPlace options: https://github.com/Masterking0416/noxara-client/commit/1c4d0ae1ce67cd755587bd590442ab8a405a4f1d
- Minecraft tick integration: https://github.com/Masterking0416/noxara-client/commit/c69e3518c2df4df5af788381d65d64e57d32273a — latest build/runtime validation pending.
- GUI changes remain a foundation; they are not the finished custom ClickGUI.

## Next integration sequence

1. Audit all entries above against Meteor by actual behavior, settings and dependencies.
2. Merge overlapping behavior into one module/settings page.
3. Keep every genuinely unique Wurst feature as a native Noxara module where technically compatible, and preserve Meteor-only features.
4. Work in tested category-sized batches; compile after each batch, then validate the affected modules in a disposable 26.3 world.
5. Keep statuses current so features are not silently omitted.

This matrix is a draft: similar names do not prove functional equivalence.
