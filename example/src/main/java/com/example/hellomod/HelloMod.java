package com.example.hellomod;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The string here is your mod's ID.
// It must match the mod_id set in gradle.properties.
// Use only lowercase letters, numbers, and underscores.
@Mod(HelloMod.MODID)
public class HelloMod {

    public static final String MODID = "hellomod";

    // A logger lets you print messages to the console while the game runs.
    private static final Logger LOGGER = LogUtils.getLogger();

    // NeoForge passes modEventBus and modContainer in automatically.
    // - modEventBus: used to register items, blocks, and other game content
    // - modContainer: used to register config files and access mod metadata
    public HelloMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Hello from {}! The mod loaded successfully.", MODID);

        // When you're ready to add items or blocks, you'll register them here like:
        //   MyItems.ITEMS.register(modEventBus);
        //   MyBlocks.BLOCKS.register(modEventBus);
    }

    // CLIENT-SIDE CODE NOTE:
    // Anything that touches rendering, particles, screens, or other client-only
    // Minecraft classes must NOT go in this file. Put it in a separate class
    // inside a `client` subpackage (e.g. com.example.hellomod.client.ClientSetup)
    // and register it using DistExecutor or @EventBusSubscriber(Dist.CLIENT).
    //
    // Why: dedicated servers don't have client classes at all. Mixing them in here
    // will crash the server.
    //
    // See: https://docs.neoforged.net/docs/gettingstarted/structuring
}
