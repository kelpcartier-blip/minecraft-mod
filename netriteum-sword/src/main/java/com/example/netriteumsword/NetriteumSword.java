package com.example.netriteumsword;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The string here is your mod's ID.
// It must match mod_id in gradle.properties exactly.
@Mod(NetriteumSword.MODID)
public class NetriteumSword {

    public static final String MODID = "netriteumsword";

    // A logger lets you print messages to the Minecraft console.
    // Use LOGGER.info("message") anywhere in the mod to see output while the game runs.
    private static final Logger LOGGER = LogUtils.getLogger();

    // NeoForge passes these in automatically when the mod loads:
    // - modEventBus: used to register items, blocks, and other game content
    // - modContainer: used to register config files and access mod metadata
    public NetriteumSword(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Netriteum Sword loaded!");

        // When you're ready to add items, you'll register them here like:
        //   ModItems.ITEMS.register(modEventBus);

        // When you're ready to add blocks:
        //   ModBlocks.BLOCKS.register(modEventBus);
    }

    // CLIENT-SIDE CODE NOTE:
    // Anything that touches rendering, particles, screens, or other client-only
    // Minecraft classes must NOT go in this file. Put it in a separate class
    // inside a `client` subpackage (e.g. com.example.netriteumsword.client.ClientSetup).
    //
    // Why: dedicated servers don't have client classes at all — mixing them in
    // here will crash the server.
}
