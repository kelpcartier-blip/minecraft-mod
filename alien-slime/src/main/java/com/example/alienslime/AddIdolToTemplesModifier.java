package com.example.alienslime;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

// A Global Loot Modifier (GLM) injects items into existing loot tables without
// replacing them. This one adds an Ancient Idol to jungle and desert temple chests.
//
// Which chests it targets is determined by conditions in the JSON files — the Java
// class just defines what to DO when those conditions are met (add the idol item).
public class AddIdolToTemplesModifier extends LootModifier {

    // The codec tells NeoForge how to read this modifier from its JSON definition.
    // LootModifier.codecStart handles the "conditions" field that every modifier needs.
    // Since we have no extra fields beyond conditions, the codec is very simple.
    public static final MapCodec<AddIdolToTemplesModifier> CODEC =
        RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).apply(inst, AddIdolToTemplesModifier::new)
        );

    public AddIdolToTemplesModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    // Called when a loot table generates drops AND all conditions passed.
    // generatedLoot is the list of items the chest would normally contain —
    // we just add our idol to it and return the modified list.
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(ModBlocks.ANCIENT_IDOL_ITEM.get()));
        return generatedLoot;
    }
}
