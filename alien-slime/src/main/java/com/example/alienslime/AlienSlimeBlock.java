package com.example.alienslime;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class AlienSlimeBlock extends Block {

    // How many ticks to wait before checking for growth.
    // Higher = slower growth. Tune this to taste.
    private static final int TICK_INTERVAL = 20;
    private static final int GROWTH_CHANCE = 5; // 1 / GROWTH_CHANCE for a slime block to spread

    public AlienSlimeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        // Step 1: If lava or fire is adjacent, destroy this block and stop.
        for (Direction dir : Direction.values()) {
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            if (neighborState.is(Blocks.LAVA) || neighborState.is(Blocks.FIRE)) {
                level.removeBlock(pos, false);
                return;
            }
        }
        if (random.nextInt(GROWTH_CHANCE) == 0) {
            // Step 2: Pick a random primary direction to try spreading into.
            Direction growDir = Direction.values()[random.nextInt(6)];
            BlockPos target = pos.relative(growDir);

            // Step 3: Check if direct spread to `target` is valid.
            if (canSpreadTo(level, target)) {
                level.setBlock(target, this.defaultBlockState(), Block.UPDATE_ALL);

            } else if (level.getBlockState(target).isSolid()
                    && !level.getBlockState(target).is(ModBlocks.ALIEN_SLIME_BLOCK.get())) {

                // reduce the chance of spreading around a scarred alien slime block
                if (level.getBlockState(target).is(ModBlocks.SCARRED_ALIEN_SLIME_BLOCK.get())) {
                    if (!(random.nextInt(GROWTH_CHANCE) == 0)) {
                        return;
                    }
                }

                // The primary direction hit a solid non-slime block — that's a corner.
                Direction cornerDir = Direction.values()[random.nextInt(6)];
                BlockPos cornerTarget = target.relative(cornerDir);

                if (canSpreadTo(level, cornerTarget)) {
                    level.setBlock(cornerTarget, this.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }

        level.scheduleTick(pos, this, TICK_INTERVAL);

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

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            // safe to modify the world here
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(Blocks.LAVA) || neighborState.is(Blocks.FIRE)) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            // safe to modify the world here
            level.scheduleTick(pos, this, TICK_INTERVAL);
        }
    }
}
