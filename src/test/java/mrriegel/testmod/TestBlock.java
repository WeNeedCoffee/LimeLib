package mrriegel.testmod;

import java.awt.Color;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TestBlock extends CommonBlockContainer<TestTile> {

	public TestBlock() {
		super(Material.ROCK, "blocko");
		setCreativeTab(CreativeTabs.REDSTONE);
		setHardness(3f);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TestTile();
	}

	@Override
	protected Class<? extends TestTile> getTile() {
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
