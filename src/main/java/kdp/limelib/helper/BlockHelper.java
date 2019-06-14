package kdp.limelib.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockHelper {

    public static boolean isBlockBreakable(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getMaterial() != Material.AIR && !state.getMaterial().isLiquid() && state
                .getBlockHardness(world, pos) > -1F;
    }

}
