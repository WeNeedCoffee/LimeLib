package mrriegel.limelib.helper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
}
