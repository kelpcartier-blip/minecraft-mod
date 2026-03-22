package com.example.alienslime;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

// This annotation tells NeoForge "this class is the entry point for the mod with this ID".
// The ID here must exactly match mod_id in gradle.properties and the entry in neoforge.mods.toml.
@Mod(AlienSlimeMod.MODID)
public class AlienSlimeMod {

    // The mod ID is used as a "namespace" — every block, item, and resource in this
    // mod will be prefixed with "alienslime:", like "alienslime:alien_slime_block".
    public static final String MODID = "alienslime";

    // A logger lets us print messages to the console. Useful for debugging.
    // You'll see these messages in the IntelliJ console when the game runs.
    public static final Logger LOGGER = LogUtils.getLogger();

    // DeferredRegisters are how we tell Minecraft about our blocks and items.
    // Think of these as lists that we add entries to before the game starts,
    // and then NeoForge registers everything at the right time during startup.
    // We're declaring them here but they'll be populated in dedicated classes later.
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // NeoForge calls this constructor automatically when your mod is loaded.
    // It injects the IEventBus (used to listen for game events) and ModContainer
    // (metadata about your mod) for you — you don't need to create them yourself.
    public AlienSlimeMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Alien Slime mod loading...");

        // Wire up our registers to the event bus so NeoForge knows to process them.
        // Without these lines, any blocks or items we add won't appear in the game.
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        // TODO: When we add the AlienSlimeBlock, we'll register it here.
        // TODO: We'll also add a creative tab so the block shows up in the creative inventory.
    }
}
