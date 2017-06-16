package mrriegel.limelib.helper;

import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class NBTHelper {

	public static boolean hasTag(NBTTagCompound nbt, String keyName) {
		return nbt != null && nbt.hasKey(keyName);
	}

	public static void removeTag(NBTTagCompound nbt, String keyName) {
		if (nbt == null)
			return;
		nbt.removeTag(keyName);
	}

	public enum NBTType {
		BOOLEAN(1, false, Boolean.class), //
		BYTE(1, (byte) 0, Byte.class), //
		SHORT(2, (short) 0, Short.class), //
		INT(3, 0, Integer.class), //
		LONG(4, 0L, Long.class), //
		FLOAT(5, 0F, Float.class), //
		DOUBLE(6, 0D, Double.class), //
		STRING(8, null, String.class), //
		NBT(10, null, NBTTagCompound.class), //
		ITEMSTACK(10, ItemStack.EMPTY, ItemStack.class), //
		BLOCKPOS(4, null, BlockPos.class);

		int tagID;
		Object defaultV;
		Class<?> clazz;

		private NBTType(int tagID, Object defaultV, Class<?> clazz) {
			this.tagID = tagID;
			this.defaultV = defaultV;
			this.clazz = clazz;
		}

		public static BiMap<Class<?>, NBTType> m = HashBiMap.create();
		static {
			for (NBTType n : NBTType.values())
				m.put(n.clazz, n);
			// m.put(Boolean.class, NBTType.BOOLEAN);
			// m.put(Byte.class, NBTType.BYTE);
			// m.put(Short.class, NBTType.SHORT);
			// m.put(Integer.class, NBTType.INT);
			// m.put(Long.class, NBTType.LONG);
			// m.put(Float.class, NBTType.FLOAT);
			// m.put(Double.class, NBTType.DOUBLE);
			// m.put(String.class, NBTType.STRING);
			// m.put(NBTTagCompound.class, NBTType.NBT);
			// m.put(ItemStack.class, NBTType.ITEMSTACK);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(NBTTagCompound nbt, String name, Class<T> clazz) {
		NBTType type = NBTType.m.get(clazz);
		if (type == null)
			throw new IllegalArgumentException();
		if (nbt == null || !nbt.hasKey(name, type.tagID))
			return (T) type.defaultV;
		switch (type) {
		case BOOLEAN:
			return (T) new Boolean(nbt.getBoolean(name));
		case BYTE:
			return (T) new Byte(nbt.getByte(name));
		case SHORT:
			return (T) new Short(nbt.getShort(name));
		case INT:
			return (T) new Integer(nbt.getInteger(name));
		case LONG:
			return (T) new Long(nbt.getLong(name));
		case FLOAT:
			return (T) new Float(nbt.getFloat(name));
		case DOUBLE:
			return (T) new Double(nbt.getDouble(name));
		case STRING:
			return (T) nbt.getString(name);
		case NBT:
			return (T) nbt.getCompoundTag(name);
		case ITEMSTACK:
			return (T) new ItemStack(nbt.getCompoundTag(name));
		case BLOCKPOS:
			return (T) BlockPos.fromLong(nbt.getLong(name));
		}
		return null;
	}

	public static NBTTagCompound set(NBTTagCompound nbt, String name, Object value) {
		if (nbt == null || value == null)
			return nbt;
		NBTType type = NBTType.m.get(value.getClass());
		if (type == null)
			throw new IllegalArgumentException();
		switch (type) {
		case BOOLEAN:
			nbt.setBoolean(name, (boolean) value);
			break;
		case BYTE:
			nbt.setByte(name, (byte) value);
			break;
		case SHORT:
			nbt.setShort(name, (short) value);
			break;
		case INT:
			nbt.setInteger(name, (int) value);
			break;
		case LONG:
			nbt.setLong(name, (long) value);
			break;
		case FLOAT:
			nbt.setFloat(name, (float) value);
			break;
		case DOUBLE:
			nbt.setDouble(name, (double) value);
			break;
		case STRING:
			nbt.setString(name, (String) value);
			break;
		case NBT:
			nbt.setTag(name, (NBTTagCompound) value);
			break;
		case ITEMSTACK:
			nbt.setTag(name, ((ItemStack) value).writeToNBT(new NBTTagCompound()));
			break;
		case BLOCKPOS:
			nbt.setLong(name, ((BlockPos) value).toLong());
		}
		return nbt;
	}

	public static <T> List<T> getList(NBTTagCompound nbt, String name, Class<T> clazz) {
		NBTType type = NBTType.m.get(clazz);
		if (type == null)
			throw new IllegalArgumentException();
		if (nbt == null || !nbt.hasKey(name, 10))
			return Lists.newArrayList();
		List<T> values = Lists.newArrayList();
		NBTTagCompound lis = nbt.getCompoundTag(name);
		int size = lis.getInteger("size");
		for (int i = 0; i < size; i++)
			values.add(get(lis, name + "__" + i, clazz));
		return values;
	}

	public static NBTTagCompound setList(NBTTagCompound nbt, String name, List<?> values) {
		if (nbt == null || values.isEmpty())
			return nbt;
		Object one = null;
		for (Object o : values)
			if (o != null) {
				one = o;
				break;
			}
		NBTTagCompound lis = new NBTTagCompound();
		lis.setInteger("size", values.size());
		if (one != null) {
			NBTType type = NBTType.m.get(one.getClass());
			if (type == null)
				throw new IllegalArgumentException();
		}
		for (int i = 0; i < values.size(); i++)
			set(lis, name + "__" + i, values.get(i));
		nbt.setTag(name, lis);
		return nbt;
	}

}
