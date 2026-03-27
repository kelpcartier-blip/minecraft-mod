package com.example.alienslime;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

// The Ancient Idol is a decorative block players find in jungle/desert temple chests
// and place in their farm. While it sits there it buffs nearby crop yields — but
// each harvest has a small chance to curse the land by planting alien slime.
public class AncientIdolBlock extends Block {

    // How far from the idol (in blocks) a crop harvest is affected.
    // Water hydrates farmland within 4 blocks, so radius 8 is twice as powerful.
    public static final int IDOL_RADIUS = 8;

    // The per-harvest curse chance, chosen so that harvesting the entire field
    // (all ~289 blocks in the radius) produces roughly a 22% chance of at least
    // one alien slime spawn. Math: E[spawns] = N * p = 0.25, so P(≥1) ≈ 1-e^(-0.25) ≈ 22%.
    // The (2R+1)^2 denominator = 289 for IDOL_RADIUS=8.
    public static final double CURSE_CHANCE = 0.25 / ((2 * IDOL_RADIUS + 1.0) * (2 * IDOL_RADIUS + 1.0));

    public AncientIdolBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // appendHoverText adds lines to the item tooltip shown when the player hovers over it
    // in their inventory. This is how we hint at both the buff and the curse.
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("block.alienslime.ancient_idol.tooltip1"));
        tooltipComponents.add(Component.translatable("block.alienslime.ancient_idol.tooltip2"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    // -------------------------------------------------------------------------
    // Helper used by the event listener in AlienSlimeMod.
    //
    // Scans a flat horizontal square of radius IDOL_RADIUS centered on cropPos
    // and returns the position of the first idol block found, or null if none.
    //
    // We check the same Y level and the one level below, since players may
    // place the idol on the ground rather than at crop height.
    // -------------------------------------------------------------------------
    public static BlockPos findNearbyIdol(Level level, BlockPos cropPos) {
        // Walk every position in a square grid centered on the crop.
        // The moment we find an idol we return its position immediately —
        // there's no need to keep searching once we know one is nearby.
        for (int x = -IDOL_RADIUS; x <= IDOL_RADIUS; x++) {
            for (int z = -IDOL_RADIUS; z <= IDOL_RADIUS; z++) {
                // Check both the crop's Y level and one below, because the idol
                // sits on the ground while crops grow one block above the dirt.
                for (int y = -1; y <= 0; y++) {
                    BlockPos candidate = cropPos.offset(x, y, z);
                    if (level.getBlockState(candidate).getBlock() instanceof AncientIdolBlock) {
                        return candidate;
                    }
                }
            }
        }
        // No idol found within range — return null to signal "no effect".
        return null;
    }
}
