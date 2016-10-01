package mrriegel.testmod;

import java.awt.Color;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		if (!worldIn.isRemote) {
		} else {
//			pos = pos.up();
			for (Vec3d v : ParticleHelper.getVecsForExplosion( 0.9, 15, Axis.Y))
				ParticleHelper.renderParticle(new CommonParticle(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, v.xCoord, v.yCoord, v.zCoord).setTexture(ParticleHelper.squareParticle).setMaxAge2(60).setColor(Color.white.getRGB(), 0).setNoClip(true).setFlouncing(0.015));
			// for (Vec3d v : ParticleHelper.getVecsForCircle(pos, 2, 7,
			// Axis.Y))
			// ParticleHelper.renderParticle(new CommonParticle(v.xCoord,
			// v.yCoord, v.zCoord,0,0.09,0).setMaxAge2(60));
		}
		if (true)
			return true;
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

}
