# Agent Reference — Minecraft Mod Learning Workspace

## Purpose of This Repository

This repo exists for two goals that are equally important:

1. **Make modding accessible** — reduce the friction of setup, tooling, and boilerplate so more time is spent on actual mod work.
2. **Facilitate learning** — this is a parent-and-child project. The goal is not just a working mod but understanding how it works. Prefer explaining and guiding over doing. When a user wants to add something new (a block, item, recipe), generate a stub with structure and comments rather than a complete implementation. Leave functional sections as TODOs with commented-out generic examples. Fill in the gaps yourself only when explicitly asked.

**Tone guidance:**
- Proactively help with Java syntax and formatting errors — don't make them struggle with punctuation.
- When there's a teachable moment about how something works (why the event bus, why DeferredRegister, why client/server separation), take it.
- Keep explanations concrete and game-world-grounded where possible. "This runs once when Minecraft loads your mod" is better than "this is the mod entry point constructor."

**Keeping docs current:**
- If something in AGENTS.md or README.md turns out to be wrong, outdated, or misleading — whether discovered through a runtime error, a failed setup step, or noticing the game/tooling has changed — update the relevant file as part of fixing the issue. Don't wait to be asked.
- The user's son is unlikely to think to flag documentation as needing updates. Treat doc maintenance as part of the job whenever new information surfaces.

**IntelliJ refresh after changes:**
After making any change to the codebase, always tell the user what (if any) refresh action is needed in IntelliJ:
- Edits to existing `.java` or JSON/TOML files: no action needed — IntelliJ picks these up automatically on focus.
- New files or folders added: tell the user to press `Ctrl+Alt+Y` / `Cmd+Alt+Y` (File → Synchronize) to force IntelliJ to notice them.
- Changes to `gradle.properties` or `build.gradle`: tell the user to click **Load Gradle Changes** in the IntelliJ popup, or use the refresh icon in the Gradle panel. This is the most important one — missing it causes confusing stale-build errors.

**Version control:**
- Commit changes to the local repository regularly — after completing a feature, fixing a bug, or making meaningful doc updates. Don't let work pile up uncommitted.
- The repo is on GitHub at https://github.com/kelpcartier-blip/minecraft-mod. After major changes (new feature working end-to-end, significant bug fixed, big doc update) suggest that the user push so the work is backed up and shared.
- Use GitHub issues to track planned features and bugs. Close issues via commit message with `Closes #N` when the fix lands on main.

---

## NeoForge Documentation Map

Quick links for looking things up:
- Getting started: https://docs.neoforged.net/docs/gettingstarted/
- Mod files (gradle.properties / neoforge.mods.toml): https://docs.neoforged.net/docs/gettingstarted/modfiles
- Structuring a mod: https://docs.neoforged.net/docs/gettingstarted/structuring
- Concepts — Registries: https://docs.neoforged.net/docs/concepts/registries
- Concepts — Sides (client/server): https://docs.neoforged.net/docs/concepts/sides
- Concepts — Events: https://docs.neoforged.net/docs/concepts/events
- Blocks: https://docs.neoforged.net/docs/blocks/
- Block Entities: https://docs.neoforged.net/docs/blockentities/
- Items: https://docs.neoforged.net/docs/items/
- Tools: https://docs.neoforged.net/docs/items/tools
- Resources overview (assets vs data): https://docs.neoforged.net/docs/resources/
- Models: https://docs.neoforged.net/docs/resources/client/models/
- Translations (lang files): https://docs.neoforged.net/docs/resources/client/i18n
- Recipes: https://docs.neoforged.net/docs/resources/server/recipes/
- Loot tables: https://docs.neoforged.net/docs/resources/server/loottables/
- Tags: https://docs.neoforged.net/docs/resources/server/tags
- Versioning: https://docs.neoforged.net/docs/gettingstarted/versioning

---

## Project Setup

### MDK source
Each mod starts from a cloned NeoForge MDK: https://github.com/NeoForgeMDKs
Pick the repo matching the target Minecraft version and the `ModDevGradle` build system.
e.g. `MDK-1.21.1-ModDevGradle`

### Key files in every mod project
- `gradle.properties` — the single place to set mod ID, name, version, group ID, and MC/NeoForge versions
- `build.gradle` — build config; rarely needs touching
- `src/main/java/...` — all Java source code
- `src/main/resources/assets/<modid>/` — client-side resources (textures, models, lang files, sounds)
- `src/main/resources/data/<modid>/` — server-side data (recipes, loot tables, tags)
- `src/main/templates/META-INF/neoforge.mods.toml` — mod metadata; `${variables}` filled from gradle.properties

### Mod ID rules
- 2–64 characters, lowercase letters/digits/underscores only, must start with a letter
- Must match exactly between `gradle.properties`, `@Mod(...)` annotation, and `neoforge.mods.toml`

### Java version
- Minecraft 1.20.5+ and NeoForge require **Java 21**
- 1.20.4 and earlier use Java 17

### Running in dev
After importing in IntelliJ: run `genIntellijRuns` Gradle task, then use the `runClient` run config.
Test on a dedicated server too — many client/server bugs only appear there.

---

## Core Concepts

### The @Mod Entry Point

```java
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register DeferredRegisters here
        // Subscribe to mod bus events here
    }
}
```

NeoForge injects `IEventBus` and `ModContainer` automatically. Nothing else is needed in the constructor beyond wiring up registrations and event subscriptions.

### DeferredRegister — How Everything Gets Added to the Game

Everything in Minecraft (blocks, items, sounds, etc.) must be registered. NeoForge's recommended pattern is `DeferredRegister`. Registration happens at a fixed point during startup — you can't register things later.

```java
// Define the register (usually as a static field in a dedicated class)
public static final DeferredRegister.Blocks BLOCKS =
    DeferredRegister.createBlocks(ExampleMod.MODID);

// Declare entries
public static final DeferredBlock<Block> MY_BLOCK =
    BLOCKS.register("my_block", registryName -> new Block(...));

// Wire it up in the mod constructor
BLOCKS.register(modEventBus);
```

The `DeferredHolder` / `DeferredBlock` / `DeferredItem` returned are lazy references — call `.get()` when you need the actual object, and only after registration is complete. **Do not query registries while registration is still happening.**

Specialized variants: `DeferredRegister.Blocks`, `DeferredRegister.Items`, `DeferredRegister.DataComponents`, `DeferredRegister.Entities`.

### The Event System

NeoForge uses two event buses:
- **Mod event bus** (`modEventBus` / `IEventBus`) — lifecycle events: registration, setup, config. Runs during startup, often in parallel. This is the one your mod constructor receives.
- **Game event bus** (`NeoForge.EVENT_BUS`) — runtime gameplay events: player actions, server tick, block breaks, etc.

Subscribing to events:
```java
// Option 1: in the constructor, add a listener
modEventBus.addListener(this::onCommonSetup);

// Option 2: annotation on a static method in a class registered to the bus
@SubscribeEvent
public static void onCommonSetup(FMLCommonSetupEvent event) { ... }

// Option 3: automatic discovery (class must have all static handlers)
@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MyModEvents {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) { ... }
}
```

**Never subscribe to abstract event classes** — always use concrete subtypes. Events can be cancellable (`ICancellableEvent`), have priorities (`EventPriority`), and can be side-specific.

### Mod Lifecycle Order

Constructor → `@EventBusSubscriber` discovery → `FMLConstructModEvent` → registry events → `FMLCommonSetupEvent` → `FMLClientSetupEvent` / `FMLDedicatedServerSetupEvent` → `InterModComms` → `FMLLoadCompleteEvent`

---

## Client vs. Server (Sides)

This is the most common source of crashes for new modders.

**Physical sides:**
- **Physical client** — the game the player runs; has rendering, GUI, sound classes
- **Physical server** — dedicated server JAR; does NOT have client-only classes

**Logical sides:**
- **Logical server** — runs game mechanics (entity ticking, inventory, weather). Exists inside both dedicated servers AND singleplayer (singleplayer spins up an internal server).
- **Logical client** — handles display only

**The rule:** Anything touching rendering, particles, screens, or GUI classes is client-only. Put it in a `client` subpackage and load it only on the client side. Mixing it into common code will throw `NoClassDefFoundError` on a dedicated server.

```java
// Check logical side (use this most often)
if (!level.isClientSide()) {
    // server logic
}

// Check physical side (use when accessing client-only classes)
if (FMLEnvironment.getDist() == Dist.CLIENT) {
    // safe to reference client classes here
}

// Load a whole class only on the client
@Mod(value = "examplemod", dist = Dist.CLIENT)
public class ExampleModClient { ... }
```

Docs: https://docs.neoforged.net/docs/concepts/sides

---

## Blocks

### Key facts
- There is **one block instance** in the entire game. The world stores references to it. Never instantiate blocks outside of registration.
- Block properties (hardness, sound, light) are set via `BlockBehaviour.Properties.of()`.
- Blocks in inventories are actually `BlockItem` instances — a separate registration.
- For a "few hundred" variant states: use **blockstates**. For infinite/complex data (inventories): use a **BlockEntity**.

### Registration pattern

For a plain `Block` with no custom class:
```java
public static final DeferredRegister.Blocks BLOCKS =
    DeferredRegister.createBlocks(ExampleMod.MODID);

public static final DeferredBlock<Block> MY_BLOCK =
    BLOCKS.registerSimpleBlock("my_block",
        BlockBehaviour.Properties.of()
            .destroyTime(2.0f)
            .explosionResistance(10.0f)
            .sound(SoundType.STONE)
    );
```

For a **custom block class** (e.g. `MyBlock extends Block`), use `registerBlock` instead — it passes the properties into your constructor and handles the ID internally:
```java
public static final DeferredBlock<MyBlock> MY_BLOCK =
    BLOCKS.registerBlock("my_block", MyBlock::new,
        BlockBehaviour.Properties.of()
            .destroyTime(2.0f)
            .explosionResistance(10.0f)
            .sound(SoundType.STONE)
    );
```

> **Note:** `setId()` on `BlockBehaviour.Properties` was not present in NeoForge 21.1.x. The `registerSimpleBlock` / `registerBlock` helpers handle ID assignment automatically and are the correct approach for this version.

### Common BlockBehaviour.Properties
| Property | What it does | Example values |
|---|---|---|
| `destroyTime(f)` | How long to mine | Stone: 1.5, Obsidian: 50, Bedrock: -1 |
| `explosionResistance(f)` | Blast resistance | Stone: 6, Obsidian: 1200 |
| `sound(SoundType)` | Break/step/place sounds | `SoundType.STONE`, `SoundType.WOOD` |
| `lightLevel(state -> n)` | Light emitted (0–15) | `state -> 15` for full brightness |
| `friction(f)` | Slipperiness | Ice: 0.98, default: 0.6 |
| `randomTicks()` | Enable random ticking | (no args) |
| `noLootTable()` | Block drops nothing | (no args) |

### Required resources for a new block
Every new block needs these files or it will be invisible/broken:
1. `assets/<modid>/blockstates/<block_name>.json` — maps blockstate properties to models
2. `assets/<modid>/models/block/<block_name>.json` — visual model
3. `assets/<modid>/models/item/<block_name>.json` — item model (for inventory)
4. `assets/<modid>/textures/block/<block_name>.png` — texture
5. `assets/<modid>/lang/en_us.json` — display name translation
6. `data/<modid>/loot_table/blocks/<block_name>.json` — what the block drops when broken

### Block placement/breaking lifecycle
Placement: prerequisites → `canBeReplaced` → `getStateForPlacement` → `canSurvive` → `onPlace` → `setPlacedBy`
Breaking: `attack` → per-tick `getDestroyProgress` → `playerWillDestroy` → `onDestroyedByPlayer` → `playerDestroy` → drops → xp

Docs: https://docs.neoforged.net/docs/blocks/

---

## Block Entities

Use when a block needs to store data that blockstates can't handle (inventories, energy, progress).

### Setup requires three things
1. A class extending `BlockEntity`
2. A registered `BlockEntityType`
3. The associated block implementing `EntityBlock` and overriding `newBlockEntity()`

### Data persistence
Override `loadAdditional()` and `saveAdditional()` to read/write NBT data.
Call `setChanged()` after any modification — otherwise the chunk might not save.

Reserved NBT tag names you cannot use: `id`, `x`, `y`, `z`, `NeoForgeData`, `neoforge:attachments`

### Ticking
Implement `getTicker()` in the block to run code every game tick. Avoid heavy work in tick — batch operations every N ticks instead.

### Syncing to client
Three approaches:
- Chunk load: override `getUpdateTag()` / `handleUpdateTag()`
- Block update: use `getUpdatePacket()` + `Level#sendBlockUpdated()`
- Custom packets (most flexible)

Always null-check the BlockEntity before using it — it may have been removed by the time a packet arrives.

Docs: https://docs.neoforged.net/docs/blockentities/

---

## Items

### Key facts
- Items define defaults; `ItemStack` holds the actual instance with per-stack data (components).
- `ItemStack` is **mutable** — use `.copy()` before modifying if you need to preserve the original.
- `setId()` on `Item.Properties` is **not required in NeoForge 21.1.x** — the registration helpers handle it.

### Registration pattern
```java
public static final DeferredRegister.Items ITEMS =
    DeferredRegister.createItems(ExampleMod.MODID);

public static final DeferredItem<Item> MY_ITEM =
    ITEMS.registerSimpleItem("my_item", props -> props
        .stacksTo(16)
        .rarity(Rarity.UNCOMMON)
    );

// Block item (item form of a block)
public static final DeferredItem<BlockItem> MY_BLOCK_ITEM =
    ITEMS.registerSimpleBlockItem("my_block", MyBlocks.MY_BLOCK);
```

### Common Item.Properties
| Property | What it does |
|---|---|
| `stacksTo(n)` | Max stack size (default 64) |
| `durability(n)` | Adds durability; forces stack size to 1 |
| `fireResistant()` | Item entity survives fire/lava |
| `rarity(Rarity.X)` | Name color: COMMON, UNCOMMON, RARE, EPIC |
| `food(FoodProperties)` | Makes item edible |
| `enchantable(n)` | Max enchantment level |
| `equippable(slot)` | Which equipment slot |

### Adding to creative tabs

**Option 1 — instance method registered in the constructor** (simpler, works well in 21.1.x):
```java
// In your mod constructor:
modEventBus.addListener(this::addCreative);

// Elsewhere in the class:
private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        event.accept(ModBlocks.MY_BLOCK_ITEM);
    }
}
```

**Option 2 — static method with `@SubscribeEvent`** (useful if your event handlers are in a separate class):
```java
@EventBusSubscriber(modid = MyMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MyModEvents {
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(MyItems.MY_ITEM.get());
        }
    }
}
```

### Required resources for a new item
1. `assets/<modid>/models/item/<item_name>.json` — model (usually inherits `minecraft:item/generated`)
2. `assets/<modid>/textures/item/<item_name>.png` — texture (16×16 PNG)
3. `assets/<modid>/lang/en_us.json` — display name translation

Docs: https://docs.neoforged.net/docs/items/

---

## Tools

Tools use a `ToolMaterial` to define a tier, then register item types with delegates.

### ToolMaterial fields
- Block tag: blocks this tier **cannot** mine (e.g. `needs_iron_tool`)
- Durability (stone: 131, iron: 250)
- Mining speed multiplier (stone: 4f, iron: 6f)
- Attack damage bonus
- Enchantability
- Repair item tag

### Registration
```java
ITEMS.register("my_pickaxe", () ->
    new PickaxeItem(MY_MATERIAL, new Item.Properties())
);
```

### Tags for mining levels
- `needs_stone_tool`, `needs_iron_tool`, `needs_diamond_tool` — what tier is required to get drops
- Custom tool tags go in `data/<modid>/tags/block/`

Docs: https://docs.neoforged.net/docs/items/tools

---

## Resources: Assets vs. Data

**Assets** = client-side, loaded from resource packs. Lives under `assets/<namespace>/`.
Includes: textures, models, lang files, sounds, particles.

**Data** = server-side, loaded from data packs. Lives under `data/<namespace>/`.
Includes: recipes, loot tables, tags, advancements, worldgen.

Both use **resource locations** in the format `namespace:path`.
Your mod's namespace is its mod ID.

NeoForge auto-generates `pack.mcmeta` — you don't need to create it.

Docs: https://docs.neoforged.net/docs/resources/

---

## Models

Models are JSON files at `assets/<namespace>/models/`.
Resource location `examplemod:item/my_item` → file at `assets/examplemod/models/item/my_item.json`.

### Common parent models
| Parent | Used for |
|---|---|
| `minecraft:item/generated` | Flat 2D items |
| `minecraft:block/cube_all` | Block with same texture on all sides |
| `minecraft:block/cube_column` | Logs, pillars (top/side textures) |
| `minecraft:block/cross` | Plants, flowers |

### Minimal item model
```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "examplemod:item/my_item"
  }
}
```

### Minimal block model (same texture all sides)
```json
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "examplemod:block/my_block"
  }
}
```

Blockstate file is also required — it maps block properties to which model to use.

Docs: https://docs.neoforged.net/docs/resources/client/models/

---

## Translations (Lang Files)

File location: `assets/<modid>/lang/en_us.json`

Translation key format:
- Blocks: `block.<modid>.<block_name>`
- Items: `item.<modid>.<item_name>`
- Creative tabs: `itemGroup.<modid>.<tab_name>`

```json
{
  "block.examplemod.my_block": "My Cool Block",
  "item.examplemod.my_item": "My Cool Item"
}
```

Blocks and items auto-generate their translation key via `#getDescriptionId()` — you just need the matching entry in the lang file.

Docs: https://docs.neoforged.net/docs/resources/client/i18n

---

## Recipes

Recipe files: `data/<modid>/recipe/<name>.json`

### Shaped crafting example
```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "###",
    " | ",
    " | "
  ],
  "key": {
    "#": { "item": "examplemod:my_item" },
    "|": { "item": "minecraft:stick" }
  },
  "result": {
    "id": "examplemod:my_tool",
    "count": 1
  }
}
```

### Shapeless crafting example
```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    { "item": "minecraft:diamond" },
    { "item": "examplemod:my_item" }
  ],
  "result": {
    "id": "examplemod:my_result",
    "count": 1
  }
}
```

Use `"tag": "minecraft:planks"` instead of `"item": "..."` to accept any item in a tag.

Recipe priorities can be set in `data/<modid>/recipe_priorities.json` when overlapping recipes conflict.

Docs: https://docs.neoforged.net/docs/resources/server/recipes/

---

## Loot Tables

File location: `data/<modid>/loot_table/blocks/<block_name>.json`
Every block automatically looks for a loot table at this path when broken.

### Minimal loot table (always drops itself)
```json
{
  "type": "minecraft:block",
  "pools": [{
    "rolls": 1,
    "entries": [{
      "type": "minecraft:item",
      "name": "examplemod:my_block"
    }]
  }]
}
```

Loot tables support conditions (silk touch, fortune), functions (count modifiers), and weighted random entries.
Use `noLootTable()` on block properties for blocks that should never drop anything.

Docs: https://docs.neoforged.net/docs/resources/server/loottables/

---

## Tags

Tags group registry objects so recipes/tools can accept any member without listing each one.
Files: `data/<namespace>/tags/<registry_path>/<tag_name>.json`

```json
{
  "values": [
    "examplemod:my_block",
    "#minecraft:stone"
  ]
}
```

The `#` prefix means "include all members of this other tag."

In code:
```java
TagKey<Block> MY_TAG = TagKey.create(Registries.BLOCK,
    Identifier.fromNamespaceAndPath("examplemod", "my_blocks"));

// Check membership
blockState.is(MY_TAG);
itemStack.is(MY_ITEM_TAG);
```

NeoForge convention: shared cross-mod tags use the `c` namespace (e.g. `c:ingots/copper`).
Use plural names (`planks`, `ingots`). Nest categories in folders (`c:ingots/iron`, `c:ingots/gold`).

For tool mining tiers, add your block to the appropriate `needs_X_tool` tag.

Docs: https://docs.neoforged.net/docs/resources/server/tags

---

## Package Naming and Code Structure

Top-level package should be something you own reversed: a domain, GitHub username, etc.
- `example.com` → `com.example`
- `github.com/username` → `io.github.username`

Second level is the mod ID: `com.example.mymod`

Sub-package organization options:
- **By type**: `blocks/`, `items/`, `entities/` — mirrors Minecraft's own structure
- **By feature**: `feature/furnace/` containing the block, menu, and item together

**Always isolate client-only code** into a `client` subpackage.
Data generation code goes in a `data` subpackage.

Class naming conventions: suffix with type — `PowerRingItem`, `NotDirtBlock`, `OvenMenu`.
Entities traditionally use bare names (`Pig`, `Zombie`).

Docs: https://docs.neoforged.net/docs/gettingstarted/structuring

---

## Common Crash Causes (Quick Reference)

| Symptom | Likely cause |
|---|---|
| `NoClassDefFoundError` / `ClassNotFoundException` | Client-only class loaded on server — check side separation |
| Block replaced with air | Block instantiated outside of registration |
| Exception mentioning `setId` | Only applies to newer NeoForge versions — use `registerBlock`/`registerSimpleBlock` helpers in 21.1.x |
| Registry query returns null | Queried registry while registration was still in progress |
| Cyclic ordering crash | Dependency A declared BEFORE B and B declared BEFORE A |
| Missing translation (shows raw key) | `en_us.json` entry missing or key doesn't match |
| Block drops nothing | Missing loot table file at `data/<modid>/loot_table/blocks/<name>.json` |
| Item has no texture (black/purple) | Missing model or texture file, or wrong resource location |
