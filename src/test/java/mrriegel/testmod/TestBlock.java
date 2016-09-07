package mrriegel.testmod;

import mrriegel.limelib.block.CommonBlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TestBlock extends CommonBlockContainer {

	public TestBlock() {
		super(Material.ROCK, "blocko");
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TestTile();
	}

	@Override
	protected Class<? extends TileEntity> getTile() {
		return TestTile.class;
	}

	// @Override
	// public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess
	// blockAccess, BlockPos pos, EnumFacing side) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean isBlockNormalCube(IBlockState state) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	// @Override
	// public boolean isOpaqueCube(IBlockState state) {
	// // TODO Auto-generated method stub
	// return false;
	// }

}
