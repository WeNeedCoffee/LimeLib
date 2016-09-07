package mrriegel.limelib.block;

import java.util.List;

import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.tile.IDataKeeper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class CommonBlockContainer<T extends CommonTile> extends CommonBlock implements ITileEntityProvider {

	public CommonBlockContainer(Material materialIn, String name) {
		super(materialIn, name);
		isBlockContainer = true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.getTileEntity(pos) instanceof CommonTile)
			for (ItemStack stack : ((CommonTile) worldIn.getTileEntity(pos)).getDroppingItems())
				if (stack != null)
					spawnAsEntity(worldIn, pos, stack.copy());
		worldIn.removeTileEntity(pos);
	}

	@Override
	public void registerBlock() {
		super.registerBlock();
		GameRegistry.registerTileEntity(getTile(), getUnlocalizedName());
	}

	protected abstract Class<? extends T> getTile();

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof CommonTile) {
				return ((CommonTile) tileentity).openGUI((EntityPlayerMP) playerIn);
			}
			return false;
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (worldIn.getTileEntity(pos) instanceof IDataKeeper && NBTStackHelper.getBoolean(stack, "idatakeeper")) {
			IDataKeeper tile = (IDataKeeper) worldIn.getTileEntity(pos);
			tile.readFromStack(stack);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		List<ItemStack> lis = getDrops(worldIn, pos, state, 0);
		if (worldIn.getTileEntity(pos) instanceof IDataKeeper && lis.size() == 1 && lis.get(0).getItem() == Item.getItemFromBlock(state.getBlock())) {
			IDataKeeper tile = (IDataKeeper) worldIn.getTileEntity(pos);
			ItemStack stack = lis.get(0);
			NBTStackHelper.setBoolean(stack, "idatakeeper", true);
			tile.writeToStack(stack);
			worldIn.setBlockToAir(pos);
			spawnAsEntity(worldIn, pos, stack.copy());
		}
	}

}
