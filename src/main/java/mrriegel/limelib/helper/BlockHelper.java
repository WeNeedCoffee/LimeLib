package mrriegel.limelib.helper;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import com.google.common.collect.Lists;

public class BlockHelper {

	public static boolean isBlockBreakable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return !world.isAirBlock(pos) && !state.getBlock().getMaterial(state).isLiquid() && state.getBlock().getBlockHardness(state, world, pos) > -1F;
	}

	public static NonNullList<ItemStack> breakBlockWithFortune(World world, BlockPos pos, int fortune, EntityPlayer player, boolean simulate, boolean particle) {
		IBlockState state = world.getBlockState(pos);
		if (!isBlockBreakable(world, pos) /*|| !state.getBlock().canHarvestBlock(world, pos, player)*/)
			return NonNullList.create();
		NonNullList<ItemStack> lis = NonNullList.create();
		lis.addAll(Lists.newArrayList(state.getBlock().getDrops(world, pos, state, fortune)));
		ForgeEventFactory.fireBlockHarvesting(lis, world, pos, state, fortune, 1.0f, false, player);
		if (!simulate) {
			if (particle)
				world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockToAir(pos);
		}
		lis.removeAll(Collections.singleton(ItemStack.EMPTY));
		return lis;
	}

	public static NonNullList<ItemStack> breakBlockWithSilk(World world, BlockPos pos, EntityPlayer player, boolean simulate, boolean particle, boolean breakAnyway) {
		IBlockState state = world.getBlockState(pos);
		if (!isBlockBreakable(world, pos) || !state.getBlock().canHarvestBlock(world, pos, player))
			return null;
		if (state.getBlock().canSilkHarvest(world, pos, state, player)) {
			ItemStack stack = /**
			 * state.getBlock().getPickBlock(state, new
			 * RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP), world, pos,
			 * player);
			 */
			StackHelper.getStackFromBlock(world, pos, player);
			if (!stack.isEmpty()) {
				if (breakAnyway)
					return breakBlockWithFortune(world, pos, 0, player, simulate, particle);
				else
					return NonNullList.create();
			}
			if (!simulate) {
				if (particle)
					world.playEvent(2001, pos, Block.getStateId(state));
				world.setBlockToAir(pos);
			}
			NonNullList<ItemStack> nnl = NonNullList.create();
			nnl.add(stack);
			return nnl;
		} else if (breakAnyway)
			return breakBlockWithFortune(world, pos, 0, player, simulate, particle);
		return NonNullList.create();
	}

	public static boolean isOre(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!state.getBlock().isFullCube(state))
			return false;
		try {
			ItemStack stack = null;
			try {
				stack = state.getBlock().getPickBlock(state, new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP), (World) world, pos, null);
				return StackHelper.isOre(stack);
			} catch (Exception e) {
				stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
				return StackHelper.isOre(stack);
			}
		} catch (Exception e) {
		}
		return false;
	}

}
