package mrriegel.limelib.helper;

import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelper {
	public static boolean hasTag(NBTTagCompound nbt, String keyName) {
		return nbt != null && nbt.hasKey(keyName);
	}

	public static void removeTag(NBTTagCompound nbt, String keyName) {
		if (nbt != null) {
			nbt.removeTag(keyName);
		}
	}

	public static NBTTagList createList(NBTBase... list) {
		return createList(Lists.newArrayList(list));
	}

	public static NBTTagList createList(List<NBTBase> list) {
		NBTTagList tagList = new NBTTagList();
		for (NBTBase b : list)
			tagList.appendTag(b);
		return tagList;
	}

	// list
	public static NBTTagList getList(NBTTagCompound nbt, String tag, int objtype, boolean nullifyOnFail) {
		return hasTag(nbt, tag) ? nbt.getTagList(tag, objtype) : nullifyOnFail ? null : new NBTTagList();
	}

	public static void setList(NBTTagCompound nbt, String tag, NBTTagList list) {
		nbt.setTag(tag, list);
	}

	// String
	public static String getString(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 8)) {
			return null;
		}
		return nbt.getString(keyName);
	}

	public static void setString(NBTTagCompound nbt, String keyName, String keyValue) {
		if (keyValue != null)
			nbt.setString(keyName, keyValue);
	}

	// boolean
	public static boolean getBoolean(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName)) {
			setBoolean(nbt, keyName, false);
		}
		return nbt.getBoolean(keyName);
	}

	public static void setBoolean(NBTTagCompound nbt, String keyName, boolean keyValue) {
		nbt.setBoolean(keyName, keyValue);
	}

	// byte
	public static byte getByte(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 99)) {
			setByte(nbt, keyName, (byte) 0);
		}
		return nbt.getByte(keyName);
	}

	public static void setByte(NBTTagCompound nbt, String keyName, byte keyValue) {
		nbt.setByte(keyName, keyValue);
	}

	// short
	public static short getShort(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 99)) {
			setShort(nbt, keyName, (short) 0);
		}
		return nbt.getShort(keyName);
	}

	public static void setShort(NBTTagCompound nbt, String keyName, short keyValue) {
		nbt.setShort(keyName, keyValue);
	}

	// int
	public static int getInteger(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 99)) {
			setInteger(nbt, keyName, 0);
		}
		return nbt.getInteger(keyName);
	}

	public static void setInteger(NBTTagCompound nbt, String keyName, int keyValue) {
		nbt.setInteger(keyName, keyValue);
	}

	// long
	public static long getLong(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 99)) {
			setLong(nbt, keyName, 0L);
		}
		return nbt.getLong(keyName);
	}

	public static void setLong(NBTTagCompound nbt, String keyName, long keyValue) {
		nbt.setLong(keyName, keyValue);
	}

	// float
	public static float getFloat(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 99)) {
			setFloat(nbt, keyName, 0F);
		}
		return nbt.getFloat(keyName);
	}

	public static void setFloat(NBTTagCompound nbt, String keyName, float keyValue) {
		nbt.setFloat(keyName, keyValue);
	}

	// double
	public static double getDouble(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName, 99)) {
			setDouble(nbt, keyName, 0D);
		}
		return nbt.getDouble(keyName);
	}

	public static void setDouble(NBTTagCompound nbt, String keyName, double keyValue) {
		nbt.setDouble(keyName, keyValue);
	}

	// itemnbt
	public static ItemStack getItemStack(NBTTagCompound nbt, String keyName) {
		if (!nbt.hasKey(keyName)) {
			setItemStack(nbt, keyName, null);
		}
		NBTTagCompound res = (NBTTagCompound) nbt.getTag(keyName);
		return ItemStack.loadItemStackFromNBT(res);

	}

	public static void setItemStack(NBTTagCompound nbt, String keyName, ItemStack keyValue) {
		NBTTagCompound res = new NBTTagCompound();
		if (keyValue != null) {
			keyValue.writeToNBT(res);
		}
		nbt.setTag(keyName, res);
	}

	// enum
	public static <E extends Enum<E>> E getEnum(NBTTagCompound nbt, String keyName, Class<E> clazz) {
		if (!nbt.hasKey(keyName)) {
			return null;
		}
		String s = getString(nbt, keyName);
		for (E e : EnumSet.allOf(clazz)) {
			if (e.name().equals(s))
				return e;
		}
		return null;

	}

	public static <E extends Enum<E>> void setEnum(NBTTagCompound nbt, String keyName, E keyValue) {
		if (keyValue != null)
			setString(nbt, keyName, keyValue.name());
	}

	private static final String SIZE = "§Size";

	// Stringlist
	public static List<String> getStringList(NBTTagCompound nbt, String keyName) {
		List<String> lis = Lists.newArrayList();
		int size = getInteger(nbt, keyName + SIZE);
		for (int i = 0; i < size; i++)
			lis.add(getString(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setStringList(NBTTagCompound nbt, String keyName, List<String> keyValue) {
		if (keyValue != null) {
			setInteger(nbt, keyName + SIZE, keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				String s = keyValue.get(i);
				if (s != null)
					setString(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Booleanlist
	public static List<Boolean> getBooleanList(NBTTagCompound nbt, String keyName) {
		List<Boolean> lis = Lists.newArrayList();
		int size = getInteger(nbt, keyName + SIZE);
		for (int i = 0; i < size; i++)
			lis.add(getBoolean(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setBooleanList(NBTTagCompound nbt, String keyName, List<Boolean> keyValue) {
		if (keyValue != null) {
			setInteger(nbt, keyName + SIZE, keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Boolean s = keyValue.get(i);
				if (s != null)
					setBoolean(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Integerlist
	public static List<Integer> getIntegerList(NBTTagCompound nbt, String keyName) {
		List<Integer> lis = Lists.newArrayList();
		int size = getInteger(nbt, keyName + SIZE);
		for (int i = 0; i < size; i++)
			lis.add(getInteger(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setIntegerList(NBTTagCompound nbt, String keyName, List<Integer> keyValue) {
		if (keyValue != null) {
			setInteger(nbt, keyName + SIZE, keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Integer s = keyValue.get(i);
				if (s != null)
					setInteger(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Doublelist
	public static List<Double> getDoubleList(NBTTagCompound nbt, String keyName) {
		List<Double> lis = Lists.newArrayList();
		int size = getInteger(nbt, keyName + SIZE);
		for (int i = 0; i < size; i++)
			lis.add(getDouble(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setDoubleList(NBTTagCompound nbt, String keyName, List<Double> keyValue) {
		if (keyValue != null) {
			setInteger(nbt, keyName + SIZE, keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				Double s = keyValue.get(i);
				if (s != null)
					setDouble(nbt, keyName + ":" + i, s);
			}
		}
	}

	// Stacklist
	public static List<ItemStack> getItemStackList(NBTTagCompound nbt, String keyName) {
		List<ItemStack> lis = Lists.newArrayList();
		int size = getInteger(nbt, keyName + SIZE);
		for (int i = 0; i < size; i++)
			lis.add(getItemStack(nbt, keyName + ":" + i));
		return lis;
	}

	public static void setItemStackList(NBTTagCompound nbt, String keyName, List<ItemStack> keyValue) {
		if (keyValue != null) {
			setInteger(nbt, keyName + SIZE, keyValue.size());
			for (int i = 0; i < keyValue.size(); i++) {
				ItemStack s = keyValue.get(i);
				if (s != null)
					setItemStack(nbt, keyName + ":" + i, s);
			}
		}
	}

}
