package mrriegel.limelib.helper;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

public class BlockHelper {

	public static boolean isBlockBreakable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return !world.isAirBlock(pos) && !state.getBlock().getMaterial(state).isLiquid() && state.getBlock() != Blocks.BEDROCK && state.getBlock().getBlockHardness(state, world, pos) > -1.0F;
	}

	public static List<ItemStack> breakBlockWithFortune(World world, BlockPos pos, int fortune, EntityPlayer player, boolean simulate, boolean particle) {
		IBlockState state = world.getBlockState(pos);
		if (!isBlockBreakable(world, pos))
			return Lists.newArrayList();
		List<ItemStack> lis = Lists.newArrayList(state.getBlock().getDrops(world, pos, state, fortune));
		if (!simulate) {
			if (particle)
				world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockToAir(pos);
		}
		lis.removeAll(Collections.singleton(null));
		return lis;
	}

	public static ItemStack breakBlockWithSilk(World world, BlockPos pos, int fortune, EntityPlayer player, boolean simulate, boolean particle) {
		IBlockState state = world.getBlockState(pos);
		if (!isBlockBreakable(world, pos) || state.getBlock().canSilkHarvest(world, pos, state, player))
			return null;
		ItemStack stack = state.getBlock().getPickBlock(state, new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP), world, pos, player);
		if (!simulate) {
			if (particle)
				world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockToAir(pos);
		}
		return stack;
	}

	public static boolean isOre(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!state.getBlock().isFullCube(state))
			return false;
		try {
			ItemStack stack = null;
			try {
				stack = state.getBlock().getPickBlock(state, new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP), world, pos, null);
			} catch (Exception e) {
				stack = new ItemStack(state.getBlock());
			}
			if (stack != null) {
				for (int i : OreDictionary.getOreIDs(stack)) {
					String oreName = OreDictionary.getOreName(i);
					if (oreName.startsWith("denseore") || (oreName.startsWith("ore") && Character.isUpperCase(oreName.charAt(3))))
						return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static List<BlockPos> getNeighbors(BlockPos p) {
		List<BlockPos> lis = Lists.newArrayList();
		for (EnumFacing e : EnumFacing.VALUES)
			lis.add(p.offset(e));
		return lis;
	}

}
