package mrriegel.testmod;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import mrriegel.limelib.block.CommonBlockContainer;

public class TestBlock extends CommonBlockContainer{

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
