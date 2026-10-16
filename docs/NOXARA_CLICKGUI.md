# Noxara ClickGUI architecture

The ClickGUI is implemented as native Minecraft/Meteor widgets. It uses the existing `WidgetScreen` event routing, renderer, settings factory, configuration model, module registry, and keybind screen rather than creating a second incompatible settings system.

## Main screen

- `NoxaraClickScreen` is the screen wrapper. It owns the dashboard and participates in the existing tab system. Mouse/keyboard events are transformed to the GUI's scaled coordinate system by `WidgetScreen` before widgets receive them.
- `NoxaraDashboard` is the full-screen layout and page model. It paints the tinted background, computes panel placement, filters the live module registry, and supports the All, Active, Favorites, and category views.
- `NoxaraSidebar` provides workspace shortcuts and category navigation with an animated violet selection marker.
- `NoxaraMainPanel` owns the search text box, dynamic module count, content heading, and scroll view. Search matches module names, titles, descriptions, and individual setting titles.
- `NoxaraPanel` and `NoxaraStyle` supply the shared rounded geometry, translucent slate surfaces, violet palette, outlines, soft shadows, and allocation-conscious color interpolation.
- `NoxaraModuleCard` owns expandable per-module settings. `NoxaraModuleButton` is a separate renderer/input component for the module title, description, animated enable switch, and expand affordance.

## Settings and keybinds

Expanded cards render the settings produced by the existing `GuiTheme.settings(module.settings)` factory. This keeps Meteor's actual setting objects as the source of truth and preserves type-specific behavior, hidden/conditional settings, config serialization, and addon settings.

The existing native widgets are styled to match Noxara:

- `WMeteorCheckbox` — animated pill switch with a violet track and glow.
- `WMeteorSlider` — rounded track, violet fill, animated thumb, and a value bubble while dragging.
- `WMeteorDropdown` — rounded selector, popup surface, and rounded highlighted options.
- `WMeteorTextBox` — rounded search/input surface, native cursor/selection, keyboard editing, and completion popup.
- `WMeteorButton` — rounded animated interactive surfaces.
- `WMeteorWindow` — rounded window chrome for the full module-settings screen.

Clicking a card body expands or collapses its settings; clicking the switch zone toggles the module; right-clicking the card opens the full settings/keybind screen. That full screen uses the native `WKeybind` flow, including its waiting-for-input state and config reset/copy/paste controls. Settings receive their native `tick` updates while an inline card is expanded.

## Input, layout, and performance

The `WidgetScreen` wrapper handles scaled mouse coordinates, click/release routing, key events, character input, and close transitions. The native `WView` handles clipped content and smooth scrolling. Text entry remains in `WTextBox` so cursor movement, selection, control-key shortcuts, and text input are not reimplemented.

Render-time colors are reused rather than created per frame. Widget text is clipped during layout, not during rendering. Surface geometry is batched through `GuiRenderer` quads and its antialiased circle texture for rounded corners. Animations use clamped frame-delta interpolation to avoid overshooting with frame-rate changes.

## Blur and visual effects

`NoxaraClickScreen` is recognized by the existing downsample/upsample blur pipeline, so the screen gets a short fade-in/fade-out background blur even when the optional Blur module switch is off. Other screens keep the Blur module's prior enable/setting behavior. The GUI overlays a translucent dark veil and panels on top of that blurred scene; the violet glow is a subtle multi-stop quad gradient.

## Build verification

The GUI source is covered by the repository's Noxara Java 25 Gradle workflow. A successful compile verifies API/type compatibility; in-game testing is still required for visual checks across GUI scales, short resolutions, search interaction, dragging sliders, opening dropdowns, and keybind capture.
