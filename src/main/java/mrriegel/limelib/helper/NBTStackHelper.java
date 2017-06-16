package mrriegel.limelib.helper;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTStackHelper {

	public static void initNBTTagCompound(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
	}

	public static boolean hasTag(ItemStack stack, String keyName) {
		return NBTHelper.hasTag(stack.getTagCompound(), keyName);
	}

	public static void removeTag(ItemStack stack, String keyName) {
		NBTHelper.removeTag(stack.getTagCompound(), keyName);
	}

	public static <T> T get(ItemStack stack, String name, Class<T> clazz) {
		return NBTHelper.get(stack.getTagCompound(), name, clazz);
	}

	public static ItemStack set(ItemStack stack, String name, Object value) {
		initNBTTagCompound(stack);
		NBTHelper.set(stack.getTagCompound(), name, value);
		return stack;
	}

	public static <T> List<T> getList(ItemStack stack, String name, Class<T> clazz) {
		return NBTHelper.getList(stack.getTagCompound(), name, clazz);
	}

	public static ItemStack setList(ItemStack stack, String name, List<?> values) {
		initNBTTagCompound(stack);
		NBTHelper.setList(stack.getTagCompound(), name, values);
		return stack;
	}

}
