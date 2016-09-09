package mrriegel.limelib.helper;

import java.util.List;
import java.util.Random;

import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

public class StackHelper {

	public static boolean equalOreDict(ItemStack a, ItemStack b) {
		if (a == null || b == null)
			return false;
		for (int i : OreDictionary.getOreIDs(a))
			if (Ints.contains(OreDictionary.getOreIDs(b), i))
				return true;
		return false;
	}

	public static boolean isOre(ItemStack stack) {
		if (stack != null) {
			for (int i : OreDictionary.getOreIDs(stack)) {
				String oreName = OreDictionary.getOreName(i);
				if (oreName.startsWith("denseore") || (oreName.startsWith("ore") && Character.isUpperCase(oreName.charAt(3))))
					return true;
			}
		}
		return false;
	}

	public static boolean match(ItemStack stack, Object o) {
		if (stack == null)
			return false;
		if (o instanceof Item || (o instanceof ItemStack) && ((ItemStack) o).getItemDamage() == OreDictionary.WILDCARD_VALUE)
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
		if (stack == null)
			return null;
		String prefix = stack.getItem().getRegistryName().toString();
		if (!simple)
			return prefix + "#" + stack.stackSize + "/" + stack.getItemDamage();
		String ret = prefix;
		if (stack.stackSize == 1 && stack.getItemDamage() == 0)
			return prefix;
		if (stack.stackSize > 1 && stack.getItemDamage() == 0)
			return prefix + "#" + stack.stackSize;
		return prefix + "#" + stack.stackSize + "/" + stack.getItemDamage();
	}

	public static List<ItemStack> split(ItemStack stack) {
		return split(stack, 2);
	}

	public static List<ItemStack> split(ItemStack stack, int splits) {
		if (stack == null)
			return null;
		List<Integer> ints = Utils.split(stack.stackSize, splits);
		List<ItemStack> stacks = Lists.newArrayList();
		for (int i : ints)
			stacks.add(ItemHandlerHelper.copyStackWithSize(stack, i));
		return stacks;
	}

	public static void spawnItemStack(World worldIn, BlockPos pos, ItemStack stack) {
		spawnItemStack(worldIn, pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5, stack);
	}

	public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
		// if (stack != null)
		// return;
		Random RANDOM = worldIn.rand;
		float f = RANDOM.nextFloat() * 0.8F + 0.1F;
		float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
		float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;
		if (stack != null)
			stack = stack.copy();
		while (stack != null && stack.stackSize > 0) {
			int i = RANDOM.nextInt(21) + 10;

			if (i > stack.stackSize) {
				i = stack.stackSize;
			}
			stack.stackSize -= i;
			EntityItem entityitem = new EntityItem(worldIn, x + f, y + f1, z + f2, ItemHandlerHelper.copyStackWithSize(stack, i));
			if (stack.hasTagCompound()) {
				entityitem.getEntityItem().setTagCompound(stack.getTagCompound().copy());
			}
			float f3 = 0.05F;
			entityitem.motionX = RANDOM.nextGaussian() * 0.05000000074505806D;
			entityitem.motionY = RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
			entityitem.motionZ = RANDOM.nextGaussian() * 0.05000000074505806D;
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

}
