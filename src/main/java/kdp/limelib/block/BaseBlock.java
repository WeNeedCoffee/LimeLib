package kdp.limelib.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BaseBlock extends Block {

	protected boolean hasTile = false;

	public BaseBlock(Properties properties, String name) {
		super(properties);
		setRegistryName(name);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return hasTile;
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
		return super.createTileEntity(state, world);
	}

}
