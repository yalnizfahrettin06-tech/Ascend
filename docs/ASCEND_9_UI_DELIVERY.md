# Ascend 9.0 — reference implementation

Scope: the three approved home, sharing and collection references supplied on 10 September 2026.

- Warm limestone background, graphite typography, hairline boundaries and small wine accents.
- Home: custom rising bar identity, local time, moment chooser, Roman arch/bust artwork, responsive quote field, page rules, compact favorite/share/more actions. Existing reading, copy, saved-state and plan interactions are retained.
- Explore: open editorial heading and underlined search; neutral relief cards, selected-topic states, collection drilldown, search and access filters retained. Decorative architecture has no accessibility nodes.
- Share: full-screen editor, real exported-bitmap preview, background/photo/video source choices, favorites for background themes, curated strip plus complete searchable library, type/format/layout/motion controls, device save and Android share output.
- Three primary free themes now start with marble. Legacy summit remains free after upgrade. Photo import, motion and advanced controls continue to require the explicitly labeled Pro demo. No payment or real-ad claims are added.
- Marble PNG and animated-video export use the same renderer and preserve source text and selected font. The heart over the preview is an editor control and is not burned into the export.

## Artwork
Generated with the built-in imagegen tool from the supplied home reference. Asset: `app/src/main/res/drawable-nodpi/art_roman_home_v9.png`. Prompt direction: remove all text and UI, retain a right-aligned Roman marble bust inside an architectural arch, ivory left reading space, neutral light rays and column shadows. No external image provider, API key or model override was used.

## Delivery
Version 9.0.0 (22). GitHub Actions runs source checks, JVM tests, lint, instrumentation-source compilation and APK assembly. Emulator checks are optional on manual dispatch and still available for PRs. As requested, delivery stops after the APK link is available; no post-build APK/device inspection is claimed.
