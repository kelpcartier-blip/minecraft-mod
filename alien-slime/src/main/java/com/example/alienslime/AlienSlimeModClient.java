package com.example.alienslime;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

// The "dist = Dist.CLIENT" means NeoForge will ONLY load this class on the client
// (the game you play). It will never be loaded on a dedicated server.
// This is the safe place to put any code that touches rendering, sounds, or GUIs.
@Mod(value = AlienSlimeMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AlienSlimeMod.MODID, value = Dist.CLIENT)
public class AlienSlimeModClient {

    // How often (in ticks) we scan for idols and spawn particles.
    // 20 ticks = 1 second. This keeps the effect subtle rather than constant.
    private static final int PARTICLE_TICK_INTERVAL = 20;

    // Per eligible crop block, the chance of spawning a particle on each scan.
    // Keeps the sparkles sparse and magical rather than overwhelming.
    private static final double PARTICLE_CHANCE = 0.1;

    // How far down the renderer should give affected crops particle effects
    private static final int IDOL_SCAN_DEPTH = 8;

    // Counts up each tick so we know when to run the next scan.
    private static int tickCounter = 0;


    public AlienSlimeModClient(ModContainer container) {
        AlienSlimeMod.LOGGER.info("Alien Slime client setup...");

        // Particle ticking is a gameplay event (not a lifecycle event), so it
        // goes on the game event bus rather than the mod event bus.
        NeoForge.EVENT_BUS.addListener(AlienSlimeModClient::onClientTick);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // This runs once when the client finishes starting up.
    }

    // Runs every tick on the client. We throttle the actual work to once per
    // PARTICLE_TICK_INTERVAL ticks so we're not scanning the world constantly.
    private static void onClientTick(ClientTickEvent.Post event) {
        if (++tickCounter % PARTICLE_TICK_INTERVAL != 0) return;

        // Minecraft.getInstance() gives us the running game. The level and player
        // can be null if we're on the main menu, so we check before using them.
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null || mc.player == null) return;

        BlockPos playerPos = mc.player.blockPosition();
        int hr = AncientIdolBlock.IDOL_RADIUS * 2;

        // Scan for idol blocks near the player. When one is found, hand off to
        // spawnParticlesAroundIdol to handle the crop scan and particle spawning.
        for (int x = -hr; x <= hr; x++) {
            for (int y = -IDOL_SCAN_DEPTH; y <= 4; y++) {
                for (int z = -hr; z <= hr; z++) {
                    BlockPos candidate = playerPos.offset(x, y, z);
                    if (level.getBlockState(candidate).getBlock() instanceof AncientIdolBlock) {
                        spawnParticlesAroundIdol(level, candidate);
                    }
                }
            }
        }
    }

    // For a given idol position, scan nearby crop blocks and randomly spawn
    // END_ROD particles above them. The small upward velocity makes them
    // drift like magical dust rather than sitting static.
    private static void spawnParticlesAroundIdol(Level level, BlockPos idolPos) {
        int r = AncientIdolBlock.IDOL_RADIUS;
        for (int x = -r; x <= r; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos cropPos = idolPos.offset(x, y, z);
                    if (level.getBlockState(cropPos).getBlock() instanceof CropBlock) {
                        if (level.random.nextDouble() < PARTICLE_CHANCE) {
                            level.addParticle(ParticleTypes.END_ROD,
                                    cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5,
                                    0, 0.05, 0);
                        }
                    }
                }
            }
        }
    }
}
