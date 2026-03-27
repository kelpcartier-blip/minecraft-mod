package com.example.alienslime;

import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.ArrayList;

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

    // NeoForge calls this constructor automatically when your mod is loaded.
    // It injects the IEventBus (used to listen for game events) and ModContainer
    // (metadata about your mod) for you — you don't need to create them yourself.
    public AlienSlimeMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Alien Slime mod loading...");

        modEventBus.addListener(this::addCreative);

        // BlockDropsEvent is a gameplay event (not a lifecycle event), so it goes on
        // the game event bus (NeoForge.EVENT_BUS) rather than the mod event bus.
        NeoForge.EVENT_BUS.addListener(this::onBlockDrops);

        // Wire up our registers to the event bus so NeoForge knows to process them.
        // Without these lines, any blocks or items we add won't appear in the game.
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlocks.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.ALIEN_SLIME_BLOCK_ITEM);
            event.accept(ModBlocks.SCARRED_ALIEN_SLIME_BLOCK_ITEM);
            event.accept(ModBlocks.ANCIENT_IDOL_ITEM);
        }
    }

    // -------------------------------------------------------------------------
    // Fired whenever a block is broken and its drops are being calculated.
    // We use this to (a) add bonus drops when a crop is near an idol, and
    // (b) occasionally curse the land by placing an alien slime block.
    //
    // This runs on the SERVER side — safe to modify the world here.
    // -------------------------------------------------------------------------
    private void onBlockDrops(BlockDropsEvent event) {
        Level level = (Level) event.getLevel();

        // Server-side only — the level check guards against running this on the client.
        if (level.isClientSide()) return;

        BlockPos brokenPos = event.getPos();

        // Only act on standard farmland crops (wheat, carrots, potatoes, beetroot, etc.).
        // Anything else — stone, wood, dirt — exits here immediately, keeping this cheap.
        if (event.getState().getBlock() instanceof net.minecraft.world.level.block.CropBlock) {

            // Check whether an idol is sitting within range of this crop.
            // Returns null if none found — in that case there's nothing to do.
            if (AncientIdolBlock.findNearbyIdol(level, brokenPos) == null) {
                return;
            }

            // --- Bonus drops ---
            // event.getDrops() is the list of ItemStacks this block will drop.
            // We copy every stack and add the copies back, doubling the yield.
            // We collect into a separate list first because you can't add to a list
            // while you're iterating over it — that would cause a crash.
            List<ItemStack> drops = event.getDrops();
            List<ItemStack> copies = new ArrayList<>();
            for (ItemStack stack : drops) {
                copies.add(stack.copy());
            }
            drops.addAll(copies);

            // --- Curse roll ---
            // Roll a random number between 0.0 and 1.0. If it falls below CURSE_CHANCE,
            // the curse triggers. Across a full field harvest this happens ~22% of the time.
            // The slime replaces the crop's position — right above the dirt — and will
            // begin spreading on its own via AlienSlimeBlock's existing tick logic.
            if (level.random.nextDouble() < AncientIdolBlock.CURSE_CHANCE) {
                level.setBlock(brokenPos, ModBlocks.ALIEN_SLIME_BLOCK.get().defaultBlockState(), 3);
            }
        }
    }
}
