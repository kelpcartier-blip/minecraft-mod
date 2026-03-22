package com.example.alienslime;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// This class is where we declare every block (and its matching item) for this mod.
// Keeping registrations here instead of in the main mod class makes things easier to
// find as the mod grows — one place to look for "what blocks does this mod add?"
public class ModBlocks {

    // One DeferredRegister per registry type. Everything in this mod that is a Block
    // goes through BLOCKS; everything that is an Item goes through ITEMS.
    // The mod ID is the "namespace" — all our blocks will be "alienslime:something".
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AlienSlimeMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AlienSlimeMod.MODID);

    // The alien slime block itself.
    // The lambda receives the registry name (e.g. ResourceLocation "alienslime:alien_slime_block")
    // and returns a new instance of our block. setId() is mandatory in 1.21.1 — leaving it out
    // will throw an exception at startup.
    public static final DeferredBlock<AlienSlimeBlock> ALIEN_SLIME_BLOCK =
        BLOCKS.register("alien_slime_block", registryName -> new AlienSlimeBlock(
            BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, registryName))
                .destroyTime(0.3f)              // easy to mine — softer than stone
                .explosionResistance(1.0f)
                .sound(SoundType.SLIME_BLOCK)   // satisfying squelch
                .randomTicks()                  // tells Minecraft to call randomTick() on this block
        ));

    // Every placeable block also needs an item form — this is what sits in your inventory
    // and what gets placed when you right-click. registerSimpleBlockItem handles wiring
    // the two together automatically.
    public static final DeferredItem<BlockItem> ALIEN_SLIME_BLOCK_ITEM =
        ITEMS.registerSimpleBlockItem("alien_slime_block", ALIEN_SLIME_BLOCK);
}
