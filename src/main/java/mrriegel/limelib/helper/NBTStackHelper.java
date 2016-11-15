package mrriegel.limelib.helper;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTStackHelper {

	public static void initNBTTagCompound(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
	}

	public static boolean hasTag(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.hasTag(stack.getTagCompound(), keyName);
	}

	public static void removeTag(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		NBTHelper.removeTag(stack.getTagCompound(), keyName);
	}

	// list
	public static NBTTagList getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
		initNBTTagCompound(stack);
		return NBTHelper.getList(stack.getTagCompound(), tag, objtype, nullifyOnFail);
	}

	public static ItemStack setList(ItemStack stack, String tag, NBTTagList list) {
		initNBTTagCompound(stack);
		NBTHelper.setList(stack.getTagCompound(), tag, list);
		return stack;
	}

	// String
	public static String getString(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getString(stack.getTagCompound(), keyName);
	}

	public static ItemStack setString(ItemStack stack, String keyName, String keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setString(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// boolean
	public static boolean getBoolean(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getBoolean(stack.getTagCompound(), keyName);
	}

	public static ItemStack setBoolean(ItemStack stack, String keyName, boolean keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setBoolean(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// byte
	public static byte getByte(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getByte(stack.getTagCompound(), keyName);
	}

	public static ItemStack setByte(ItemStack stack, String keyName, byte keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setByte(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// short
	public static short getShort(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getShort(stack.getTagCompound(), keyName);
	}

	public static ItemStack setShort(ItemStack stack, String keyName, short keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setShort(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// int
	public static int getInt(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getInt(stack.getTagCompound(), keyName);
	}

	public static ItemStack setInt(ItemStack stack, String keyName, int keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setInt(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// long
	public static long getLong(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getLong(stack.getTagCompound(), keyName);
	}

	public static ItemStack setLong(ItemStack stack, String keyName, long keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setLong(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// float
	public static float getFloat(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getFloat(stack.getTagCompound(), keyName);
	}

	public static ItemStack setFloat(ItemStack stack, String keyName, float keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setFloat(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// double
	public static double getDouble(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getDouble(stack.getTagCompound(), keyName);
	}

	public static ItemStack setDouble(ItemStack stack, String keyName, double keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setDouble(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// tag
	public static NBTTagCompound getTag(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getTag(stack.getTagCompound(), keyName);
	}

	public static ItemStack setTag(ItemStack stack, String keyName, NBTTagCompound keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setTag(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// itemstack
	public static ItemStack getItemStack(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getItemStack(stack.getTagCompound(), keyName);

	}

	public static ItemStack setItemStack(ItemStack stack, String keyName, ItemStack keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setItemStack(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// enum
	public static <E extends Enum<E>> E getEnum(ItemStack stack, String keyName, Class<E> clazz) {
		initNBTTagCompound(stack);
		return NBTHelper.getEnum(stack.getTagCompound(), keyName, clazz);

	}

	public static <E extends Enum<E>> ItemStack setEnum(ItemStack stack, String keyName, E keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setEnum(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Stringlist
	public static List<String> getStringList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getStringList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setStringList(ItemStack stack, String keyName, List<String> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setStringList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Booleanlist
	public static List<Boolean> getBooleanList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getBooleanList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setBooleanList(ItemStack stack, String keyName, List<Boolean> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setBooleanList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Bytelist
	public static List<Byte> getByteList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getByteList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setByteList(ItemStack stack, String keyName, List<Byte> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setByteList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Shortlist
	public static List<Short> getShortList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getShortList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setShortList(ItemStack stack, String keyName, List<Short> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setShortList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Integerlist
	public static List<Integer> getIntList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getIntList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setIntList(ItemStack stack, String keyName, List<Integer> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setIntList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Longlist
	public static List<Long> getLongList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getLongList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setLongList(ItemStack stack, String keyName, List<Long> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setLongList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Floatlist
	public static List<Float> getFloatList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getFloatList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setFloatList(ItemStack stack, String keyName, List<Float> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setFloatList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Doublelist
	public static List<Double> getDoubleList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getDoubleList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setDoubleList(ItemStack stack, String keyName, List<Double> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setDoubleList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Taglist
	public static List<NBTTagCompound> getTagList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getTagList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setTagList(ItemStack stack, String keyName, List<NBTTagCompound> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setTagList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

	// Stacklist
	public static List<ItemStack> getItemStackList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getItemStackList(stack.getTagCompound(), keyName);
	}

	public static ItemStack setItemStackList(ItemStack stack, String keyName, List<ItemStack> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setItemStackList(stack.getTagCompound(), keyName, keyValue);
		return stack;
	}

}
