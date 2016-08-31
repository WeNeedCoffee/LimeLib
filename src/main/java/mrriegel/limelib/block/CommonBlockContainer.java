package mrriegel.limelib.block;

import mrriegel.limelib.tile.CommonTile;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class CommonBlockContainer extends CommonBlock implements ITileEntityProvider {

	public CommonBlockContainer(Material materialIn, String name) {
		super(materialIn, name);
		isBlockContainer = true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.getTileEntity(pos) instanceof CommonTile)
			for (ItemStack stack : ((CommonTile) worldIn.getTileEntity(pos)).getDroppingItems())
				if (stack != null)
					spawnAsEntity(worldIn, pos, stack);
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
	}

}
