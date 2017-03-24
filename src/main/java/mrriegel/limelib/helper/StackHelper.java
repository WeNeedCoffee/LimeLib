package mrriegel.limelib.helper;

import java.util.List;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

public class StackHelper {

	public static boolean equalOreDict(ItemStack a, ItemStack b) {
		if (a.isEmpty() || b.isEmpty())
			return false;
		for (int i : OreDictionary.getOreIDs(a))
			if (Ints.contains(OreDictionary.getOreIDs(b), i))
				return true;
		return false;
	}

	public static boolean equalOreDictExact(ItemStack a, ItemStack b) {
		if (a.isEmpty() || b.isEmpty())
			return false;
		return Sets.newHashSet(OreDictionary.getOreIDs(a)).equals(Sets.newHashSet(OreDictionary.getOreIDs(b)));
	}

	public static boolean isOre(ItemStack stack) {
		if (!stack.isEmpty()) {
			for (int i : OreDictionary.getOreIDs(stack)) {
				String oreName = OreDictionary.getOreName(i);
				if ((oreName.startsWith("denseore") || (oreName.startsWith("ore")) && Character.isUpperCase(oreName.charAt(3))))
					return true;
			}
		}
		return false;
	}

	public static boolean match(ItemStack stack, Object o) {
		if (stack.isEmpty())
			return false;
		if (o instanceof Item || (o instanceof ItemStack && ((ItemStack) o).getItemDamage() == OreDictionary.WILDCARD_VALUE))
			return stack.getItem() == o;
		if (o instanceof Block)
			return stack.getItem() == Item.getItemFromBlock((Block) o);
		if (o instanceof String)
			return Ints.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID((String) o));
		if (o instanceof ItemStack) {
			return stack.isItemEqual((ItemStack) o);
		}
		return false;
	}

	public static ItemStack stringToStack(String string) {
		/** minecraft:dye#32/3 */
		/** ee3:table#32 */
		/** botania:pie */
		Item item = null;
		int amount = 1;
		int meta = 0;
		String[] ar = string.split("[#/]");
		if (ar.length < 1)
			return null;
		item = Item.getByNameOrId(ar[0]);
		if (item == null)
			return null;
		if (ar.length >= 2 && StringUtils.isNumeric(ar[1]))
			amount = Integer.valueOf(ar[1]);
		if (ar.length >= 3 && StringUtils.isNumeric(ar[2]))
			meta = Integer.valueOf(ar[2]);
		return new ItemStack(item, amount, meta);
	}

	public static String stackToString(ItemStack stack, boolean simple) {
		if (stack.isEmpty())
			return null;
		String prefix = stack.getItem().getRegistryName().toString();
		if (!simple)
			return prefix + "#" + stack.getCount() + "/" + stack.getItemDamage();
		if (stack.getCount() == 1 && stack.getItemDamage() == 0)
			return prefix;
		if (stack.getCount() > 1 && stack.getItemDamage() == 0)
			return prefix + "#" + stack.getCount();
		return prefix + "#" + stack.getCount() + "/" + stack.getItemDamage();
	}

	public static NonNullList<ItemStack> split(ItemStack stack) {
		return split(stack, 2);
	}

	public static NonNullList<ItemStack> split(ItemStack stack, int splits) {
		if (stack.isEmpty())
			return null;
		List<Integer> ints = Utils.split(stack.getCount(), splits);
		NonNullList<ItemStack> stacks = NonNullList.create();
		for (int i : ints)
			stacks.add(ItemHandlerHelper.copyStackWithSize(stack, i));
		return stacks;
	}

	public static ItemStack getStackFromBlock(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote) {
			return world.getBlockState(pos).getBlock().getPickBlock(world.getBlockState(pos), new RayTraceResult(Vec3d.ZERO, EnumFacing.UP), world, pos, FakePlayerFactory.getMinecraft((WorldServer) world
			// DimensionManager.getWorld(0)
					));
		} else {
			return world.getBlockState(pos).getBlock().getPickBlock(world.getBlockState(pos), LimeLib.proxy.getClientRayTrace(), world, pos, player);
		}
	}

	public static boolean isWrench(ItemStack stack) {
		if (stack.isEmpty() || stack.getItem() instanceof ItemBlock)
			return false;
		boolean wrench = false;
		for (String s : new String[] { "wrench", "scrench", "screwdriver" }) {
			wrench |= stack.getItem().getClass().getSimpleName().toLowerCase().contains(s);
			for (Class<?> c : stack.getItem().getClass().getInterfaces())
				wrench |= c.getSimpleName().toLowerCase().contains(s);
			wrench |= stack.getUnlocalizedName().toLowerCase().contains(s);
		}
		return wrench;
	}

	public static void addStack(NonNullList<ItemStack> lis, ItemStack stack) {
		if (stack.isEmpty())
			return;
		IItemHandler inv = new ItemStackHandler(lis);
		lis.add(ItemHandlerHelper.insertItemStacked(inv, stack, false));
		Iterables.removeIf(lis, ItemStack::isEmpty);
	}

	public static NonNullList<ItemStack> inv2list(IItemHandler inv) {
		NonNullList<ItemStack> lis = NonNullList.create();
		for (int i = 0; i < inv.getSlots(); i++)
			addStack(lis, inv.getStackInSlot(i));
		return lis;
	}

	public static void list2inv(NonNullList<ItemStack> lis, IItemHandler inv) {
		for (ItemStack stack : lis) {
			ItemStack remain = ItemHandlerHelper.insertItemStacked(inv, stack, false);
			if (!remain.isEmpty())
				LimeLib.log.error(remain + " is lost.");
		}
	}

	public static void toStackList(List<Object> lis) {
		for (int i = 0; i < lis.size(); i++) {
			Object o = lis.get(i);
			if (o instanceof Item)
				lis.set(i, new ItemStack((Item) o, 1, OreDictionary.WILDCARD_VALUE));
			if (o instanceof Block)
				lis.set(i, new ItemStack((Block) o, 1, OreDictionary.WILDCARD_VALUE));
		}
	}

}
