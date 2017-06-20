package mrriegel.testmod;

import mrriegel.limelib.block.CommonBlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		if (!worldIn.isRemote) {
			//			EntityFallingBlock e=new EntityFallingBlock(worldIn,pos .getX()+.5,pos . getY()+1.5,pos . getZ()+.5, Blocks.COBBLESTONE.getDefaultState());
			//			e.motionY=.3;
			//			e.motionX=(worldIn.rand.nextDouble()-.5)/3;
			//			e.motionZ=(worldIn.rand.nextDouble()-.5)/3;
			//			worldIn.spawnEntityInWorld(e);
		} else {
//			for (Vec3d v : ParticleHelper.getVecsForExplosion(0.4, 10, Axis.Y))
//				LimeLib.proxy.renderParticle(new CommonParticle(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, v.xCoord, v.yCoord, v.zCoord).setTexture(ParticleHelper.sparkleParticle).setMaxAge2(60).setColor(Color.lightGray.getRGB(), 100).setScale(3f).setNoClip(true).setFlouncing(0.02));
		}
		//		if (true)
		//			return true;
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}

}
