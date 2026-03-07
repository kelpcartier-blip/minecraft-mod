# Minecraft Mod Development

A shared workspace for learning NeoForge mod development together.
Each mod lives in its own subfolder. The `example/` folder contains an annotated reference mod showing what a clean, minimal mod looks like after stripping out the MDK boilerplate.

---

## Prerequisites

Install these before anything else:

### 1. JDK 21
NeoForge for Minecraft 1.20.5 and newer requires Java 21. Earlier versions (1.20.4 and below) use Java 17.
When in doubt, install JDK 21 — it covers all modern versions.

- Download from: https://adoptium.net/ (choose **JDK 21**, Temurin distribution)
- Install it, then verify by opening a terminal and running: `java -version`
- You should see something like `openjdk version "21.x.x"`

### 2. IntelliJ IDEA
The best free IDE for Java and mod development.

- Download from: https://www.jetbrains.com/idea/download/

### 3. Git
For sharing code between collaborators.

- Download from: https://git-scm.com/
- On **Windows**, the Git installer includes **Git Bash** — a terminal that understands Linux-style commands. Use Git Bash for all terminal commands in this guide.
- After installing, open a terminal (Git Bash on Windows, any terminal on Linux) and set your name and email:
  ```
  git config --global user.name "Your Name"
  git config --global user.email "your@email.com"
  ```

---

## Starting a New Mod

Each new mod starts from the official NeoForge MDK, which is available as a GitHub repository.
You'll clone it into a new subfolder of this repo.

### Step 1 — Choose your Minecraft version

A mod targets a specific version of Minecraft. You'll need to pick the version you want to play your mod on before starting.

**How to find out what version a Minecraft instance is running:**
- In the vanilla launcher: the version is listed below the profile name on the launch screen
- In Prism Launcher or ATLauncher: shown in the instance details
- In CurseForge: shown on the modpack page or in the instance settings
- In-game: shown in the bottom-left corner of the title screen, or run `/version` in chat

**Then pick the matching MDK.** All available versions are listed at:
https://github.com/NeoForgeMDKs

Look for the repo named `MDK-<version>-ModDevGradle` — for example, `MDK-1.21.1-ModDevGradle` for Minecraft 1.21.1.

> **Note:** A mod built for one Minecraft version will not load on a different version. If you want your mod to work on multiple versions, you'd need a separate build for each — that's an advanced topic for later.

### Step 2 — Clone the MDK into a new folder

Open a terminal in this repository's root folder. On Windows, right-click the folder in File Explorer and choose **Open Git Bash here**.

**Linux:**
```bash
git clone https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle my-mod-name
rm -rf my-mod-name/.git
```

**Windows (Git Bash):**
```bash
git clone https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle my-mod-name
rm -rf my-mod-name/.git
```

> Git Bash on Windows uses the same commands as Linux, so the clone and `rm -rf` commands are identical in Git Bash. If you're using Command Prompt or PowerShell instead, use `rmdir /s /q my-mod-name\.git` in place of `rm -rf`.

Replace `MDK-1.21.1-ModDevGradle` with the version you chose, and `my-mod-name` with a short name for your mod (lowercase, hyphens ok — e.g. `cool-sword`).

The `rm -rf .git` step removes the MDK's own git history so your new mod folder becomes part of this repo instead of a separate repo.

### Step 3 — Open the mod folder in IntelliJ

- Open IntelliJ IDEA
- Choose **File → Open** and select your new mod's folder (e.g. `cool-sword/`) — not the root of this repo
- IntelliJ will detect it as a Gradle project and ask to import it — say yes
- It will now download Minecraft, NeoForge, and all dependencies. **This takes a while the first time** (10–30 minutes). Let it finish before doing anything else.

### Step 4 — Configure your mod

All the basic settings for your mod are in one file: `gradle.properties`.

Open it and update these lines:

```properties
mod_id=coolsword
mod_name=Cool Sword
mod_license=All Rights Reserved
mod_version=1.0.0
mod_group_id=com.yourname.coolsword
```

Rules for `mod_id`:
- Lowercase letters, numbers, and underscores only
- Must start with a letter
- No spaces or hyphens

> The `mod_id` you set here will automatically be filled into the mod metadata and should also match the string in your `@Mod(...)` annotation in the Java source.

### Step 5 — Rename the example source files

The MDK comes with example code under `src/main/java/com/example/examplemod/`. You'll want to replace this with your own package and class names.

The `example/` folder in this repo has a simplified version to use as a starting point. See `example/README.md` for a full checklist.

### Step 6 — Run it

In IntelliJ, on the right side, open the **Gradle** panel.
Navigate to: `Tasks → mod development → runClient` and double-click it.

Minecraft will launch with your mod loaded. Check the console at the bottom of IntelliJ for log output from your mod.

> **Note:** ModDevGradle (used by MDK 1.21.1 and newer) does not have a `genIntellijRuns` task. Run configs are available directly — just use `runClient` from the Gradle panel.

---

## Building and Installing Into Minecraft

Once your mod works in the development environment, build it into a `.jar` to install in a real Minecraft instance.

### Step 1 — Build the jar

Open a terminal inside your mod's folder and run:

**Linux:**
```bash
./gradlew build
```

**Windows (Git Bash):**
```bash
./gradlew build
```

**Windows (Command Prompt or PowerShell):**
```
gradlew.bat build
```

The output appears at:
```
build/libs/your-mod-name-1.0.0.jar
```

If two jars appear, use the one **without** `-sources` in the name.

### Step 2 — Find your Minecraft mods folder

| OS | Path |
|---|---|
| Windows | `%AppData%\.minecraft\mods\` |
| Linux | `~/.minecraft/mods/` |

If you use a launcher like CurseForge or Prism Launcher, each profile has its own folder — check the launcher's settings to find it.

### Step 3 — Install NeoForge (if not already)

Your mod requires NeoForge to be installed in the Minecraft instance.
Download the NeoForge installer from https://neoforged.net/ for the matching Minecraft version, run it, and select **Install client**.

### Step 4 — Copy your jar

Copy the built `.jar` into the `mods/` folder and launch Minecraft using the NeoForge profile.

---

## Working with IntelliJ and an External Editor

When files are changed outside of IntelliJ (by Claude Code, a text editor, or git), IntelliJ may or may not pick up the changes automatically depending on what was changed.

### What refreshes automatically
Edits to existing `.java` files and JSON/TOML resource files are generally detected when you switch back to the IntelliJ window. No action needed.

### What needs a manual refresh

**New files or folders** — IntelliJ may not notice them until you force a sync:
- Press `Ctrl+Alt+Y` (Windows/Linux) or `Cmd+Alt+Y` (macOS), or
- Go to **File → Synchronize**

**Changes to `gradle.properties` or `build.gradle`** — IntelliJ will show a popup at the top of the editor asking to reload the Gradle project. Always click **Load Gradle Changes** when you see this. If you miss it, you can reload manually from the Gradle panel on the right side (click the refresh icon).

Skipping the Gradle reload after a `gradle.properties` change is a common source of confusing errors — the code looks right but the build is using stale settings.

---

## Collaborating with Git

Both collaborators should clone this repository. On Windows, open Git Bash in the folder where you want to put the project (right-click → **Open Git Bash here**).

```bash
git clone <repo-url>
cd minecraft_mod
```

### Basic workflow

The following commands work the same on Linux and in Git Bash on Windows:

```bash
# Before starting work, pull the latest changes
git pull

# After making changes, check what changed
git status
git diff

# Stage and commit your changes
git add cool-sword/src/
git commit -m "Add custom sword item"

# Push to share
git push
```

### What not to commit

The `.gitignore` in this repo already excludes:

- `build/` — compiled output
- `.gradle/` — Gradle cache
- `run/` — the Minecraft instance that launches during development (saves, logs, etc.)
- `.idea/` — IntelliJ project settings

---

## Repository Layout

```
minecraft_mod/
  README.md             ← you are here
  .gitignore
  example/              ← reference code for starting a new mod
  cool-sword/           ← example mod folder (one per mod)
    gradle.properties   ← mod settings (name, id, version)
    build.gradle        ← build configuration (rarely need to touch)
    src/
      main/
        java/           ← your Java code
        resources/      ← textures, sounds, data files
        templates/      ← mod metadata (neoforge.mods.toml)
```

---

## Helpful Resources

### Getting started
- **NeoForge: Getting Started** — covers prerequisites, workspace setup, and first build: https://docs.neoforged.net/docs/gettingstarted/
- **NeoForge: Mod Files** — explains `gradle.properties`, `neoforge.mods.toml`, and how they fit together: https://docs.neoforged.net/docs/gettingstarted/modfiles
- **NeoForge: Structuring Your Mod** — package naming conventions, how to organize code as it grows, and importantly why client-only code must be kept separate from common code: https://docs.neoforged.net/docs/gettingstarted/structuring

### Core concepts (read these when you're ready to add real content)
- **NeoForge: Registries** — how items, blocks, and everything else get registered with the game: https://docs.neoforged.net/docs/concepts/registries
- **NeoForge: Blocks** — https://docs.neoforged.net/docs/blocks/
- **NeoForge: Items** — https://docs.neoforged.net/docs/items/

### Reference
- **NeoForge full docs:** https://docs.neoforged.net/
- **MDK versions on GitHub:** https://github.com/NeoForgeMDKs
- **NeoForge Discord:** Active community for questions
- **Minecraft Wiki** — useful for understanding game concepts before modding them: https://minecraft.wiki/
