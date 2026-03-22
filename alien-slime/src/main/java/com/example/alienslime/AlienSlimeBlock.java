package com.example.alienslime;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class AlienSlimeBlock extends Block {

    // How many spread attempts to make per random tick.
    // Higher = faster growth. Tune this to taste.
    private static final int SPREAD_ATTEMPTS = 4;

    public AlienSlimeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        // Step 1: If lava or fire is adjacent, destroy this block and stop.
        for (Direction dir : Direction.values()) {
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            if (neighborState.is(Blocks.LAVA) || neighborState.is(Blocks.FIRE)) {
                level.removeBlock(pos, false);
                return;
            }
        }

        for (int i = 0; i < SPREAD_ATTEMPTS; i++) {

            // Step 2: Pick a random primary direction to try spreading into.
            Direction growDir = Direction.values()[random.nextInt(6)];
            BlockPos target = pos.relative(growDir);

            // Step 3: Check if direct spread to `target` is valid.
            if (canSpreadTo(level, target)) {
                level.setBlock(target, this.defaultBlockState(), Block.UPDATE_ALL);

            } else if (level.getBlockState(target).isSolid()
                    && !level.getBlockState(target).is(ModBlocks.ALIEN_SLIME_BLOCK.get())) {

                // The primary direction hit a solid non-slime block — that's a corner.
                Direction cornerDir = Direction.values()[random.nextInt(6)];
                BlockPos cornerTarget = target.relative(cornerDir);

                if (canSpreadTo(level, cornerTarget)) {
                    level.setBlock(cornerTarget, this.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    // Helper method: returns true if the slime can spread directly to the given position.
    // Extracted here so it can be reused for both direct and corner spread checks.
    private boolean canSpreadTo(ServerLevel level, BlockPos target) {
        if (!level.getBlockState(target).canBeReplaced()) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            BlockState neighbor = level.getBlockState(target.relative(dir));
            if (neighbor.isSolid() && !neighbor.is(ModBlocks.ALIEN_SLIME_BLOCK.get())) {
                return true;
            }
        }
        return false;
    }
}
