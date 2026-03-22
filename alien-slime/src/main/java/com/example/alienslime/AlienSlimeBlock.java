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

    public AlienSlimeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // Minecraft calls this method on a random selection of ticking blocks every few seconds.
    // This is the same mechanism that makes grass spread, crops grow, and ice melt.
    // It only ever runs on the server — never on the client — so it's safe to modify the world here.
    //
    // Our plan:
    //   1. If lava or fire is adjacent, destroy this block (slime can't survive the heat)
    //   2. Pick a random neighboring position
    //   3. Check whether the slime can spread there (air + has a surface to cling to)
    //   4. If yes, place a new slime block there
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        // TODO 1: Check all 6 neighbors (use Direction.values() to loop over them).
        //         For each neighbor, get its BlockState with level.getBlockState(pos.relative(dir)).
        //         If the neighbor is lava or fire, remove this slime block and return early.
        //         Hint: compare with Blocks.LAVA and Blocks.FIRE.
        //         Hint: to remove this block, use level.removeBlock(pos, false).
        for (Direction dir : Direction.values()) {
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            if (neighborState.is(Blocks.LAVA) || neighborState.is(Blocks.FIRE)) {
                level.removeBlock(pos, false);
                return;
            }
        }

        // TODO 2: Pick one random direction to try spreading into.
        //         Hint: Direction.values() gives you an array. Use random.nextInt(...) to pick one.
        //         Store the target position as: BlockPos target = pos.relative(chosenDirection);
        Direction growDir = Direction.values()[random.nextInt(6)];
        BlockPos target = pos.relative(growDir);

        // TODO 3: Check whether spreading to `target` is valid.
        //         Valid means two things must both be true:
        //           a) The block at `target` can be replaced (it's air or something similar).
        //              Hint: level.getBlockState(target).canBeReplaced()
        //           b) At least one of `target`'s 6 neighbors is solid AND is not alien slime.
        //              Solid-but-slime doesn't count — otherwise the slime could float itself
        //              outward into open air using its own colony as a "surface".
        //              Hint: loop Direction.values() again, check both:
        //                level.getBlockState(target.relative(dir)).isSolid()
        //                !level.getBlockState(target.relative(dir)).is(ModBlocks.ALIEN_SLIME_BLOCK.get())
        boolean validSurface = false;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(target.relative(dir)).isSolid() && !level.getBlockState(target.relative(dir)).is(ModBlocks.ALIEN_SLIME_BLOCK.get())) {
                validSurface = true;
                break;
            }
        }

        if (!validSurface || !level.getBlockState(target).canBeReplaced()) {
            return;
        }

        // TODO 4: If the spread is valid, place a new slime block at `target`.
        //         Hint: level.setBlock(target, this.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(target, this.defaultBlockState(), Block.UPDATE_ALL);
    }
}
