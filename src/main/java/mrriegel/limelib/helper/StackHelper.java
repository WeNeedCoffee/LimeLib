package mrriegel.limelib.helper;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
		List<Integer> ints = Lists.newArrayList();
		for (int i = 0; i < splits; i++)
			ints.add(stack.stackSize / splits);
		for (int i = 0; i < stack.stackSize % splits; i++)
			ints.set(i, ints.get(i) + 1);
		List<ItemStack> stacks = Lists.newArrayList();
		for (int i : ints)
			stacks.add(ItemHandlerHelper.copyStackWithSize(stack, i));
		return stacks;
	}
}
