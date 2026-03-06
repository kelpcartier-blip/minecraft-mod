# Example Mod Reference

This folder shows what a minimal NeoForge mod looks like **after** stripping out the MDK's example code.
It is a reference for what you're aiming for — not a starting point to copy.
Always start by cloning the MDK (see the main README), then use this as a guide for cleaning it up.

## Files

| File | Purpose |
|---|---|
| `src/main/java/.../HelloMod.java` | Minimal mod entry point, with comments explaining each part |
| `src/main/templates/META-INF/neoforge.mods.toml` | Mod metadata template — variables filled from `gradle.properties` |

## Checklist when setting up a freshly cloned MDK

- [ ] Edit `gradle.properties` — set `mod_id`, `mod_name`, `mod_version`, `mod_group_id`
- [ ] Rename the package folder from `com/example/examplemod` to match your `mod_group_id`
  - Right-click the package in IntelliJ → Refactor → Rename, so references update automatically
- [ ] Rename `ExampleMod.java` to something meaningful (e.g. `CoolSwordMod.java`)
- [ ] Update the `MODID` constant and `@Mod(...)` annotation to match your `mod_id`
- [ ] Replace the constructor body — remove example items/blocks or keep them to learn from
- [ ] Run `genIntellijRuns` in the Gradle panel, then `runClient` to verify it loads
- [ ] Check the console for your log message to confirm the mod initialized

## Client vs. common code

The MDK includes an `ExampleModClient.java` alongside the main mod class. This is not just for
organization — it is a hard requirement.

Minecraft code is split into two sides:
- **Common** code runs on both the client (the game you play) and the server
- **Client** code only exists on the client — things like rendering, visual effects, and UI

If client-only code ends up in the wrong place, it will crash a dedicated server because those
classes simply don't exist there. NeoForge enforces this by requiring you to keep client code in
a separate subpackage (conventionally named `client`) and register it differently.

The practical rule: **if it touches rendering, particles, or screen/GUI classes, it belongs in
the `client` subpackage.**

See the NeoForge docs for the full explanation: https://docs.neoforged.net/docs/gettingstarted/structuring

## About the MDK's example code

The MDK comes with more than just a Hello World — it includes:

- An example block (`example_block`)
- An example item (`example_item`)
- A custom creative tab
- A config file (`Config.java`)
- A client-side setup class (`ExampleModClient.java`)

This is a lot to take in at once, but it's also a great reference. Each piece demonstrates a real
pattern you'll use when building your own mod. Reading through `ExampleMod.java` with the
NeoForge docs open is a solid way to learn how registries and events work.
