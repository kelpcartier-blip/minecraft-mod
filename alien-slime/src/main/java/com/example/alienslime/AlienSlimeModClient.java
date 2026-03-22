package com.example.alienslime;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;

// The "dist = Dist.CLIENT" means NeoForge will ONLY load this class on the client
// (the game you play). It will never be loaded on a dedicated server.
// This is the safe place to put any code that touches rendering, sounds, or GUIs.
@Mod(value = AlienSlimeMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AlienSlimeMod.MODID, value = Dist.CLIENT)
public class AlienSlimeModClient {

    public AlienSlimeModClient(ModContainer container) {
        AlienSlimeMod.LOGGER.info("Alien Slime client setup...");
        // TODO: Client-only setup goes here — things like custom renderers or particle effects.
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // This runs once when the client finishes starting up.
        // TODO: Register any client-side event handlers or renderers here.
    }
}
