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

	public static void setList(ItemStack stack, String tag, NBTTagList list) {
		initNBTTagCompound(stack);
		NBTHelper.setList(stack.getTagCompound(), tag, list);
	}

	// String
	public static String getString(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getString(stack.getTagCompound(), keyName);
	}

	public static void setString(ItemStack stack, String keyName, String keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setString(stack.getTagCompound(), keyName, keyValue);
	}

	// boolean
	public static boolean getBoolean(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getBoolean(stack.getTagCompound(), keyName);
	}

	public static void setBoolean(ItemStack stack, String keyName, boolean keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setBoolean(stack.getTagCompound(), keyName, keyValue);
	}

	// byte
	public static byte getByte(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getByte(stack.getTagCompound(), keyName);
	}

	public static void setByte(ItemStack stack, String keyName, byte keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setByte(stack.getTagCompound(), keyName, keyValue);
	}

	// short
	public static short getShort(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getShort(stack.getTagCompound(), keyName);
	}

	public static void setShort(ItemStack stack, String keyName, short keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setShort(stack.getTagCompound(), keyName, keyValue);
	}

	// int
	public static int getInt(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getInt(stack.getTagCompound(), keyName);
	}

	public static void setInt(ItemStack stack, String keyName, int keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setInt(stack.getTagCompound(), keyName, keyValue);
	}

	// long
	public static long getLong(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getLong(stack.getTagCompound(), keyName);
	}

	public static void setLong(ItemStack stack, String keyName, long keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setLong(stack.getTagCompound(), keyName, keyValue);
	}

	// float
	public static float getFloat(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getFloat(stack.getTagCompound(), keyName);
	}

	public static void setFloat(ItemStack stack, String keyName, float keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setFloat(stack.getTagCompound(), keyName, keyValue);
	}

	// double
	public static double getDouble(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getDouble(stack.getTagCompound(), keyName);
	}

	public static void setDouble(ItemStack stack, String keyName, double keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setDouble(stack.getTagCompound(), keyName, keyValue);
	}

	// tag
	public static NBTTagCompound getTag(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getTag(stack.getTagCompound(), keyName);
	}

	public static void setTag(ItemStack stack, String keyName, NBTTagCompound keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setTag(stack.getTagCompound(), keyName, keyValue);
	}

	// itemstack
	public static ItemStack getItemStack(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getItemStack(stack.getTagCompound(), keyName);

	}

	public static void setItemStack(ItemStack stack, String keyName, ItemStack keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setItemStack(stack.getTagCompound(), keyName, keyValue);
	}

	// enum
	public static <E extends Enum<E>> E getEnum(ItemStack stack, String keyName, Class<E> clazz) {
		initNBTTagCompound(stack);
		return NBTHelper.getEnum(stack.getTagCompound(), keyName, clazz);

	}

	public static <E extends Enum<E>> void setEnum(ItemStack stack, String keyName, E keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setEnum(stack.getTagCompound(), keyName, keyValue);
	}

	// Stringlist
	public static List<String> getStringList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getStringList(stack.getTagCompound(), keyName);
	}

	public static void setStringList(ItemStack stack, String keyName, List<String> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setStringList(stack.getTagCompound(), keyName, keyValue);
	}

	// Booleanlist
	public static List<Boolean> getBooleanList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getBooleanList(stack.getTagCompound(), keyName);
	}

	public static void setBooleanList(ItemStack stack, String keyName, List<Boolean> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setBooleanList(stack.getTagCompound(), keyName, keyValue);
	}

	// Bytelist
	public static List<Byte> getByteList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getByteList(stack.getTagCompound(), keyName);
	}

	public static void setByteList(ItemStack stack, String keyName, List<Byte> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setByteList(stack.getTagCompound(), keyName, keyValue);
	}

	// Shortlist
	public static List<Short> getShortList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getShortList(stack.getTagCompound(), keyName);
	}

	public static void setShortList(ItemStack stack, String keyName, List<Short> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setShortList(stack.getTagCompound(), keyName, keyValue);
	}

	// Integerlist
	public static List<Integer> getIntList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getIntList(stack.getTagCompound(), keyName);
	}

	public static void setIntList(ItemStack stack, String keyName, List<Integer> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setIntList(stack.getTagCompound(), keyName, keyValue);
	}

	// Longlist
	public static List<Long> getLongList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getLongList(stack.getTagCompound(), keyName);
	}

	public static void setLongList(ItemStack stack, String keyName, List<Long> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setLongList(stack.getTagCompound(), keyName, keyValue);
	}

	// Floatlist
	public static List<Float> getFloatList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getFloatList(stack.getTagCompound(), keyName);
	}

	public static void setFloatList(ItemStack stack, String keyName, List<Float> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setFloatList(stack.getTagCompound(), keyName, keyValue);
	}

	// Doublelist
	public static List<Double> getDoubleList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getDoubleList(stack.getTagCompound(), keyName);
	}

	public static void setDoubleList(ItemStack stack, String keyName, List<Double> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setDoubleList(stack.getTagCompound(), keyName, keyValue);
	}

	// Taglist
	public static List<NBTTagCompound> getTagList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getTagList(stack.getTagCompound(), keyName);
	}

	public static void setTagList(ItemStack stack, String keyName, List<NBTTagCompound> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setTagList(stack.getTagCompound(), keyName, keyValue);
	}

	// Stacklist
	public static List<ItemStack> getItemStackList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		return NBTHelper.getItemStackList(stack.getTagCompound(), keyName);
	}

	public static void setItemStackList(ItemStack stack, String keyName, List<ItemStack> keyValue) {
		initNBTTagCompound(stack);
		NBTHelper.setItemStackList(stack.getTagCompound(), keyName, keyValue);
	}

}
