package mrriegel.limelib.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BlockHelper {

	public static boolean isBlockBreakable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return !world.isAirBlock(pos) && !state.getBlock().getMaterial(state).isLiquid() && state.getBlock().getBlockHardness(state, world, pos) > -1F;
	}

	public static List<ItemStack> breakBlock(World world, BlockPos pos, IBlockState state, @Nullable EntityPlayer player, boolean silk, int fortune, boolean dropXP, boolean particle) {
		if (!isBlockBreakable(world, pos))
			return Lists.newArrayList();
		int exp = state.getBlock().getExpDrop(state, world, pos, fortune);
		if (player != null) {
			BreakEvent event = new BreakEvent(world, pos, state, player);
			event.setExpToDrop(exp);
			if (MinecraftForge.EVENT_BUS.post(event))
				return Lists.newArrayList();
			exp = event.getExpToDrop();
		}
		if (particle)
			world.playEvent(2001, pos, Block.getStateId(state));
		List<ItemStack> lis = null;
		if (silk && state.getBlock().canSilkHarvest(world, pos, state, player)) {
			lis = Lists.newArrayList();
			ItemStack drop = getSilkDrop(world, pos, player);
			if (drop!=null)
				lis.add(drop);
		} else
			lis = getFortuneDrops(world, pos, player, fortune);
		if (player != null && !ForgeHooks.canHarvestBlock(state.getBlock(), player, world, pos))
			lis.clear();
		world.setBlockToAir(pos);
		if (dropXP && !silk && exp > 0)
			state.getBlock().dropXpOnBlockBreak(world, pos, exp);
		return lis;
	}

	public static List<ItemStack> getFortuneDrops(World world, BlockPos pos, EntityPlayer player, int fortune) {
		IBlockState state = world.getBlockState(pos);
		List<ItemStack> tmp = Lists.newArrayList();
		tmp.addAll(Lists.newArrayList(state.getBlock().getDrops(world, pos, state, fortune)));
		float chance = ForgeEventFactory.fireBlockHarvesting(tmp, world, pos, state, fortune, 1.0f, false, player);
		List<ItemStack> lis = Lists.newArrayList();
		for (ItemStack item : tmp) {
			if (world.rand.nextFloat() <= chance) {
				lis.add(item);
			}
		}
		Iterator<ItemStack> it = lis.iterator();
		while (it.hasNext()) {
			ItemStack s = it.next();
			if (s==null)
				it.remove();
		}
		return lis;
	}

	private static Map<Class<? extends Block>, Method> methodMap = Maps.newHashMap();

	public static ItemStack getSilkDrop(World world, BlockPos pos, EntityPlayer player) {
		IBlockState state = world.getBlockState(pos);
		List<ItemStack> tmp = Lists.newArrayList();
		if (state.getBlock().canSilkHarvest(world, pos, state, player)) {
			Method m = null;
			Class<? extends Block> clazz = state.getBlock().getClass();
			Set<Class<? extends Block>> clazzes = Sets.newHashSet(clazz);
			while (m == null) {
				if (methodMap.containsKey(clazz))
					m = methodMap.get(clazz);
				else
					try {
						m = ReflectionHelper.findMethod((Class<? super Block>) clazz, state.getBlock(), new String[] { "func_180643_i", "getSilkTouchDrop" }, IBlockState.class);
						//						m = findMethod(clazz, new String[] { "func_180643_i", "getSilkTouchDrop" }, IBlockState.class);
					} catch (Exception e) {
						clazz = (Class<? extends Block>) clazz.getSuperclass();
						if (clazz != null)
							clazzes.add(clazz);
						else
							break;
					}
			}
			//			m.setAccessible(true);
			for (Class<? extends Block> c : clazzes)
				methodMap.put(c, m);
			ItemStack silked = null;
			try {
				silked = (ItemStack) m.invoke(state.getBlock(), state);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
			if (silked!=null)
				tmp.add(silked);
		}
		ForgeEventFactory.fireBlockHarvesting(tmp, world, pos, state, 0, 1.0f, true, player);
		return tmp.isEmpty() ? null : tmp.get(0);
	}

	//	private static <E> Method findMethod(Class<? extends E> clazz, String[] methodNames, Class<?>... methodTypes) {
	//		Exception failed = null;
	//		for (String methodName : methodNames) {
	//			try {
	//				Method m = clazz.getDeclaredMethod(methodName, methodTypes);
	//				m.setAccessible(true);
	//				return m;
	//			} catch (Exception e) {
	//				failed = e;
	//			}
	//		}
	//		throw new UnableToFindMethodException(methodNames, failed);
	//	}

	public static boolean isOre(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!state.getBlock().isFullCube(state))
			return false;
		try {
			ItemStack stack = null;
			try {
				stack = getSilkDrop(world, pos, world.isRemote ? LimeLib.proxy.getClientPlayer() : Utils.getFakePlayer((WorldServer) world));
				if (stack==null)
					stack = state.getBlock().getPickBlock(state, new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP), world, pos, null);
				return StackHelper.isOre(stack);
			} catch (Exception e) {
				stack = new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state));
				return StackHelper.isOre(stack);
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isToolEffective(ItemStack tool, World world, BlockPos pos, boolean reallyEffective) {
		if (ForgeHooks.isToolEffective(world, pos, tool)) {
			return true;
		}
		IBlockState state = world.getBlockState(pos);
		state = state.getBlock().getActualState(state, world, pos);
		if (state.getBlockHardness(world, pos) < 0)
			return false;
		return (!reallyEffective && state.getBlock().getHarvestTool(state) == null) || tool.getItem().getToolClasses(tool).contains(state.getBlock().getHarvestTool(state));
	}

	public static boolean canToolHarvestBlock(IBlockAccess world, BlockPos pos, @Nonnull ItemStack stack) {
		IBlockState state = world.getBlockState(pos);
		state = state.getBlock().getActualState(state, world, pos);
		if (world instanceof World && state.getBlockHardness((World) world, pos) < 0)
			return false;
		String tool = state.getBlock().getHarvestTool(state);
		if (state.getBlock().getMaterial(state).isToolNotRequired())
			return true;
		if (stack==null)
			return false;
		return stack.getItem().getHarvestLevel(stack, tool, null, null) >= state.getBlock().getHarvestLevel(state);
	}

}
