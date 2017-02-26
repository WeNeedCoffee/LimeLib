package mrriegel.limelib.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BlockHelper {

	public static boolean isBlockBreakable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return !world.isAirBlock(pos) && !state.getBlock().getMaterial(state).isLiquid() && state.getBlock().getBlockHardness(state, world, pos) > -1F;
	}

	public static NonNullList<ItemStack> breakBlockWithFortune(World world, BlockPos pos, int fortune, EntityPlayer player, boolean simulate, boolean particle) {
		IBlockState state = world.getBlockState(pos);
		if (!isBlockBreakable(world, pos) /*|| !state.getBlock().canHarvestBlock(world, pos, player)*/)
			return NonNullList.create();
		NonNullList<ItemStack> lis = getFortuneDrops(world, pos, player, fortune);
		if (!simulate) {
			if (particle)
				world.playEvent(2001, pos, Block.getStateId(state));
			world.setBlockToAir(pos);
		}
		return lis;
	}

	public static NonNullList<ItemStack> breakBlockWithSilk(World world, BlockPos pos, EntityPlayer player, boolean simulate, boolean particle, boolean breakanyway) {
		IBlockState state = world.getBlockState(pos);
		if (!isBlockBreakable(world, pos) /*|| !state.getBlock().canHarvestBlock(world, pos, player)*/)
			return NonNullList.create();
		if (state.getBlock().canSilkHarvest(world, pos, state, player)) {
			ItemStack drop = getSilkDrop(world, pos, player);
			if (!simulate) {
				if (particle)
					world.playEvent(2001, pos, Block.getStateId(state));
				world.setBlockToAir(pos);
			}
			NonNullList<ItemStack> lis = NonNullList.create();
			lis.add(drop);
			return lis;
		} else if (breakanyway) {
			return breakBlockWithFortune(world, pos, 0, player, simulate, particle);
		}
		return NonNullList.create();
	}

	public static NonNullList<ItemStack> getFortuneDrops(World world, BlockPos pos, EntityPlayer player, int fortune) {
		IBlockState state = world.getBlockState(pos);
		NonNullList<ItemStack> tmp = NonNullList.create();
		tmp.addAll(Lists.newArrayList(state.getBlock().getDrops(world, pos, state, fortune)));
		float chance = ForgeEventFactory.fireBlockHarvesting(tmp, world, pos, state, fortune, 1.0f, false, player);
		NonNullList<ItemStack> lis = NonNullList.create();
		for (ItemStack item : tmp) {
			if (world.rand.nextFloat() <= chance) {
				lis.add(item);
			}
		}
		lis.removeAll(Collections.singleton(ItemStack.EMPTY));
		return lis;
	}

	private static Map<Class<? extends Block>, Method> methodMap = Maps.newHashMap();

	public static ItemStack getSilkDrop(World world, BlockPos pos, EntityPlayer player) {
		IBlockState state = world.getBlockState(pos);
		NonNullList<ItemStack> tmp = NonNullList.create();
		if (state.getBlock().canSilkHarvest(world, pos, state, player)) {
			Method m = null;
			Class<? extends Block> clazz = state.getBlock().getClass();
			Set<Class<? extends Block>> clazzes = Sets.newHashSet(clazz);
			while (m == null) {
				try {
					m = methodMap.get(clazz);
					if (m == null)
						m = clazz.getDeclaredMethod("func_180643_i", IBlockState.class);
					if (m == null)
						m = clazz.getDeclaredMethod("getSilkTouchDrop", IBlockState.class);
				} catch (NoSuchMethodException | SecurityException e) {
					clazz = (Class<? extends Block>) clazz.getSuperclass();
					if (clazz != null)
						clazzes.add(clazz);
					else
						break;
				}
			}
			m.setAccessible(true);
			for (Class<? extends Block> c : clazzes)
				methodMap.put(c, m);
			ItemStack silked = ItemStack.EMPTY;
			try {
				silked = (ItemStack) m.invoke(state.getBlock(), state);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
			if (!silked.isEmpty())
				tmp.add(silked);
		}
		ForgeEventFactory.fireBlockHarvesting(tmp, world, pos, state, 0, 1.0f, true, player);
		return tmp.isEmpty() ? ItemStack.EMPTY : tmp.get(0);
	}

	public static boolean isOre(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!state.getBlock().isFullCube(state))
			return false;
		try {
			ItemStack stack = null;
			try {
				stack = getSilkDrop(world, pos, world.isRemote ? LimeLib.proxy.getClientPlayer() : Utils.getFakePlayer((WorldServer) world));
				if (stack != null)
					return StackHelper.isOre(stack);
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

	public static boolean isToolEffective(ItemStack tool, World world, BlockPos pos) {
		if (ForgeHooks.isToolEffective(world, pos, tool)) {
			return true;
		}
		IBlockState state = world.getBlockState(pos);
		state = state.getBlock().getActualState(state, world, pos);
		return state.getBlock().getHarvestTool(state) == null || tool.getItem().getToolClasses(tool).contains(state.getBlock().getHarvestTool(state));
	}

}
