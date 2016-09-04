package mrriegel.limelib.helper;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Lists;

public class NBTStackHelper {

	private static void initNBTTagCompound(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
	}

	// list
	public static NBTTagList getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
		initNBTTagCompound(stack);
		return NBTHelper.hasTag(stack.getTagCompound(), tag) ? stack.getTagCompound().getTagList(tag, objtype) : nullifyOnFail ? null : new NBTTagList();
	}

	public static void setList(ItemStack stack, String tag, NBTTagList list) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setTag(tag, list);
	}

	// String
	public static String getString(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			return null;
		}
		return stack.getTagCompound().getString(keyName);
	}

	public static void setString(ItemStack stack, String keyName, String keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null)
			stack.getTagCompound().setString(keyName, keyValue);
	}

	// boolean
	public static boolean getBoolean(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setBoolean(stack, keyName, false);
		}
		return stack.getTagCompound().getBoolean(keyName);
	}

	public static void setBoolean(ItemStack stack, String keyName, boolean keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setBoolean(keyName, keyValue);
	}

	// byte
	public static byte getByte(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setByte(stack, keyName, (byte) 0);
		}
		return stack.getTagCompound().getByte(keyName);
	}

	public static void setByte(ItemStack stack, String keyName, byte keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setByte(keyName, keyValue);
	}

	// short
	public static short getShort(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setShort(stack, keyName, (short) 0);
		}
		return stack.getTagCompound().getShort(keyName);
	}

	public static void setShort(ItemStack stack, String keyName, short keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setShort(keyName, keyValue);
	}

	// int
	public static int getInteger(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setInteger(stack, keyName, 0);
		}
		return stack.getTagCompound().getInteger(keyName);
	}

	public static void setInteger(ItemStack stack, String keyName, int keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setInteger(keyName, keyValue);
	}

	// long
	public static long getLong(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setLong(stack, keyName, 0L);
		}
		return stack.getTagCompound().getLong(keyName);
	}

	public static void setLong(ItemStack stack, String keyName, long keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setLong(keyName, keyValue);
	}

	// float
	public static float getFloat(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setFloat(stack, keyName, 0F);
		}
		return stack.getTagCompound().getFloat(keyName);
	}

	public static void setFloat(ItemStack stack, String keyName, float keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setFloat(keyName, keyValue);
	}

	// double
	public static double getDouble(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setDouble(stack, keyName, 0D);
		}
		return stack.getTagCompound().getDouble(keyName);
	}

	public static void setDouble(ItemStack stack, String keyName, double keyValue) {
		initNBTTagCompound(stack);
		stack.getTagCompound().setDouble(keyName, keyValue);
	}

	// itemstack
	public static ItemStack getItemStack(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			setItemStack(stack, keyName, null);
		}
		NBTTagCompound res = (NBTTagCompound) stack.getTagCompound().getTag(keyName);
		return ItemStack.loadItemStackFromNBT(res);

	}

	public static void setItemStack(ItemStack stack, String keyName, ItemStack keyValue) {
		initNBTTagCompound(stack);
		NBTTagCompound res = new NBTTagCompound();
		if (keyValue != null) {
			keyValue.writeToNBT(res);
		}
		stack.getTagCompound().setTag(keyName, res);
	}

	// enum
	public static <E extends Enum<E>> E getEnum(ItemStack stack, String keyName, Class<E> clazz) {
		initNBTTagCompound(stack);
		if (!stack.getTagCompound().hasKey(keyName)) {
			return null;
		}
		String s = stack.getTagCompound().getString(keyName);
		for (E e : EnumSet.allOf(clazz)) {
			if (e.name().equals(s))
				return e;
		}
		return null;

	}

	public static <E extends Enum<E>> void setEnum(ItemStack stack, String keyName, E keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null)
			stack.getTagCompound().setString(keyName, keyValue.name());
	}

	// Stringlist
	public static List<String> getStringList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		List<String> lis = Lists.newArrayList();
		int size = getInteger(stack, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getString(stack, keyName + ":" + i));
		return lis;
	}

	public static void setStringList(ItemStack stack, String keyName, List<String> keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null) {
			setInteger(stack, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				String s = keyValue.get(i);
				if (s != null)
					setString(stack, keyName + ":" + i, s);
			}
		}
	}

	// Booleanlist
	public static List<Boolean> getBooleanList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		List<Boolean> lis = Lists.newArrayList();
		int size = getInteger(stack, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getBoolean(stack, keyName + ":" + i));
		return lis;
	}

	public static void setBooleanList(ItemStack stack, String keyName, List<Boolean> keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null) {
			setInteger(stack, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Boolean s = keyValue.get(i);
				if (s != null)
					setBoolean(stack, keyName + ":" + i, s);
			}
		}
	}

	// Integerlist
	public static List<Integer> getIntegerList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		List<Integer> lis = Lists.newArrayList();
		int size = getInteger(stack, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getInteger(stack, keyName + ":" + i));
		return lis;
	}

	public static void setIntegerList(ItemStack stack, String keyName, List<Integer> keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null) {
			setInteger(stack, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Integer s = keyValue.get(i);
				if (s != null)
					setInteger(stack, keyName + ":" + i, s);
			}
		}
	}

	// Doublelist
	public static List<Double> getDoubleList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		List<Double> lis = Lists.newArrayList();
		int size = getInteger(stack, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getDouble(stack, keyName + ":" + i));
		return lis;
	}

	public static void setDoubleList(ItemStack stack, String keyName, List<Double> keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null) {
			setInteger(stack, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Double s = keyValue.get(i);
				if (s != null)
					setDouble(stack, keyName + ":" + i, s);
			}
		}
	}

	// Stacklist
	public static List<ItemStack> getItemStackList(ItemStack stack, String keyName) {
		initNBTTagCompound(stack);
		List<ItemStack> lis = Lists.newArrayList();
		int size = getInteger(stack, keyName + "Size");
		for (int i = 0; i < size; i++)
			lis.add(getItemStack(stack, keyName + ":" + i));
		return lis;
	}

	public static void setItemStackList(ItemStack stack, String keyName, List<ItemStack> keyValue) {
		initNBTTagCompound(stack);
		if (keyValue != null) {
			setInteger(stack, keyName + "Size", keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				ItemStack s = keyValue.get(i);
				if (s != null)
					setItemStack(stack, keyName + ":" + i, s);
			}
		}
	}

}
