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

}
