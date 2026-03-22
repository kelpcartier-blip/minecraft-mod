package com.example.alienslime;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ScarredAlienSlimeBlock extends Block {

    // Same interval as the regular slime block — one check every 20 ticks (1 second).
    private static final int TICK_INTERVAL = 20;
    // Chance for scarring to spread; should be lower than chance for slime to spread
    private static final int SPREAD_CHANCE = 60; // 1 / SPREAD_CHANCE

    public ScarredAlienSlimeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            // Schedule our first tick as soon as this block is placed in the world.
            // Without this, tick() would never be called.
            level.scheduleTick(pos, this, TICK_INTERVAL);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(SPREAD_CHANCE) == 0) { //only spread scarring sometimes
            for (Direction dir : Direction.values()) { //check every direction
                BlockState neighborState = level.getBlockState(pos.relative(dir)); //state of the neighbor block in that direction
                if (neighborState.is(ModBlocks.ALIEN_SLIME_BLOCK.get())) {
                    // if it's a slime, make it scarred
                    level.setBlock(pos.relative(dir), ModBlocks.SCARRED_ALIEN_SLIME_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }

        // Always reschedule so this block keeps checking every tick.
        level.scheduleTick(pos, this, TICK_INTERVAL);
    }
}
