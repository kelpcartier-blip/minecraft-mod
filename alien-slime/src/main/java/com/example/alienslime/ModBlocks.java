package com.example.alienslime;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

// This class is where we declare every block (and its matching item) for this mod.
// Keeping registrations here instead of in the main mod class makes things easier to
// find as the mod grows — one place to look for "what blocks does this mod add?"
public class ModBlocks {

    // One DeferredRegister per registry type. Everything in this mod that is a Block
    // goes through BLOCKS; everything that is an Item goes through ITEMS.
    // The mod ID is the "namespace" — all our blocks will be "alienslime:something".
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AlienSlimeMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AlienSlimeMod.MODID);

    // GLM serializers tell NeoForge how to deserialize each modifier type from JSON.
    // One entry here per modifier class — the JSON files reference these by ID.
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
        DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AlienSlimeMod.MODID);

    public static final Supplier<MapCodec<AddIdolToTemplesModifier>> ADD_IDOL_TO_TEMPLES =
        LOOT_MODIFIER_SERIALIZERS.register("add_idol_to_temples", () -> AddIdolToTemplesModifier.CODEC);

    // The alien slime block itself.
    // The lambda receives the registry name (e.g. ResourceLocation "alienslime:alien_slime_block")
    // and returns a new instance of our block. setId() is mandatory in 1.21.1 — leaving it out
    // will throw an exception at startup.
    public static final DeferredBlock<AlienSlimeBlock> ALIEN_SLIME_BLOCK =
        BLOCKS.registerBlock("alien_slime_block", AlienSlimeBlock::new,
            BlockBehaviour.Properties.of()
                .destroyTime(0.3f)              // easy to mine — softer than stone
                .explosionResistance(1.0f)
                .sound(SoundType.SLIME_BLOCK)   // satisfying squelch
                .noLootTable()                  // it's destroyed not dropped
        );

    // Every placeable block also needs an item form — this is what sits in your inventory
    // and what gets placed when you right-click. registerSimpleBlockItem handles wiring
    // the two together automatically.
    public static final DeferredItem<BlockItem> ALIEN_SLIME_BLOCK_ITEM =
        ITEMS.registerSimpleBlockItem("alien_slime_block", ALIEN_SLIME_BLOCK);

    // The scarred variant. It doesn't spread on its own, but it converts adjacent
    // alien_slime_blocks into more scarred blocks on each tick.
    public static final DeferredBlock<ScarredAlienSlimeBlock> SCARRED_ALIEN_SLIME_BLOCK =
        BLOCKS.registerBlock("scarred_alien_slime_block", ScarredAlienSlimeBlock::new,
            BlockBehaviour.Properties.of()
                .destroyTime(0.3f)
                .explosionResistance(1.0f)
                .sound(SoundType.SLIME_BLOCK)
                .noLootTable()
        );

    public static final DeferredItem<BlockItem> SCARRED_ALIEN_SLIME_BLOCK_ITEM =
        ITEMS.registerSimpleBlockItem("scarred_alien_slime_block", SCARRED_ALIEN_SLIME_BLOCK);

    // The Ancient Idol: a glowing decorative block found in temple chests.
    // Light level 9 gives it a subtle visible glow — less than a torch (14) but clearly magical.
    // It does NOT use noLootTable() because players should be able to pick it up and move it.
    public static final DeferredBlock<AncientIdolBlock> ANCIENT_IDOL =
        BLOCKS.registerBlock("ancient_idol", AncientIdolBlock::new,
            BlockBehaviour.Properties.of()
                .destroyTime(3.0f)              // sturdy — feels like an artifact, not a dirt block
                .explosionResistance(12.0f)
                .sound(SoundType.STONE)
                .lightLevel(state -> 9)         // subtle mystical glow; no extra code needed
        );

    public static final DeferredItem<BlockItem> ANCIENT_IDOL_ITEM =
        ITEMS.registerSimpleBlockItem("ancient_idol", ANCIENT_IDOL);
}
