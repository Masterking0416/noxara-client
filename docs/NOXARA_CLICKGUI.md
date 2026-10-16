# Noxara ClickGUI architecture

The ClickGUI is implemented as native Minecraft/Meteor widgets. It uses the existing `WidgetScreen` event routing, renderer, settings factory, configuration model, module registry, and keybind screen rather than creating a second incompatible settings system.

## Main screen

- `NoxaraClickScreen` is the screen wrapper. It owns the dashboard and participates in the existing tab system.
- `NoxaraDashboard` coordinates page selection, live module filtering, the floating window rectangle, mouse capture for dragging/resizing, and the animated snow/nebula backdrop.
- `NoxaraSidebar` provides All, Active, Favorites, and category navigation. It becomes a clipped smooth scroller when the resized window is too short.
- `NoxaraMainPanel` owns search, active/module counts, and the scrollable module list. Search matches module names, titles, descriptions, and setting labels.
- `NoxaraWindowBar` is the draggable header. `NoxaraResizeGrip` provides the bottom-right resize affordance.
- `NoxaraPanel` and `NoxaraStyle` provide rounded geometry, translucent slate surfaces, borders, soft shadows, animated violet states, and reusable color interpolation.
- `NoxaraModuleCard` manages expandable settings; `NoxaraModuleButton` owns a module row's title, description, switch, and expander.

## Resizing and window memory

The window starts at a compact percentage of the available screen area with minimum dimensions that adapt to short displays. Drag the top title bar to move it. Drag the dotted bottom-right grip to resize it. Resizing is bounded by the visible screen area; the last geometry is remembered while the client process stays open and restored on the next open.

## Settings and keybinds

Expanded cards render the settings produced by the existing `GuiTheme.settings(module.settings)` factory. This keeps the actual settings objects as source of truth and preserves type-specific behavior, config serialization, conditional settings, and add-on settings.

The existing Meteor widgets are styled to match Noxara:

- `WMeteorCheckbox` — animated pill switch with violet track and glow.
- `WMeteorSlider` — rounded track, violet fill, animated thumb, and value bubble while dragging.
- `WMeteorDropdown` — rounded selector and popup choices.
- `WMeteorTextBox` — rounded input surface with native cursor/selection and completion behavior.
- `WMeteorButton` and `WMeteorWindow` — rounded interactive surfaces for secondary settings.
- `WKeybind` continues to handle waiting-for-input state, modifiers, resets, and conflict rules.

Clicking a card body expands/collapses settings; clicking the pill toggles the module; right-clicking opens the full settings/keybind screen.

## Input and performance

The native `WidgetScreen` wrapper transforms mouse coordinates and routes click, release, scroll, key, and character events. Drag operations are captured by the dashboard until mouse release. The native `WView` clips content and handles smooth scrolling.

The background snowfall uses seeded arrays and preallocated color objects; it does not create particle objects or colors during rendering. UI components use clamped delta interpolation. Rounded surfaces are batched from existing renderer quads and the antialiased circle texture.

## Blur and visual effects

The existing multi-pass blur pipeline recognizes `NoxaraClickScreen` and fades a blurred scene behind the ClickGUI. The screen draws a dark tinted overlay, a low-alpha violet nebula gradient, and softly twinkling snowfall/sparkles behind the floating charcoal window.
