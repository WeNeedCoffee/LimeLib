package mrriegel.limelib.helper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
		BLOCKPOS(4, null, BlockPos.class); //

		int tagID;
		Object defaultV;
		Class<?> clazz;

		private NBTType(int tagID, Object defaultV, Class<?> clazz) {
			this.tagID = tagID;
			this.defaultV = defaultV;
			this.clazz = clazz;
		}

		public static BiMap<Class<?>, NBTType> m = HashBiMap.create();

		public static boolean validClass(Class<?> clazz) {
			return Enum.class.isAssignableFrom(clazz) || m.get(clazz) != null;
		}

		static {
			for (NBTType n : NBTType.values())
				m.put(n.clazz, n);
		}
	}

	public static <T> T get(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (Enum.class.isAssignableFrom(clazz))
			return clazz.getEnumConstants()[nbt.getInteger(name)];
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
		if (Enum.class.isAssignableFrom(value.getClass()))
			return set(nbt, name, ((Enum<?>) value).ordinal());
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
			break;
		}
		return nbt;
	}

	public static <T> List<T> getList(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (!NBTType.validClass(clazz))
			throw new IllegalArgumentException();
		if (nbt == null || !nbt.hasKey(name, 10))
			return Lists.newArrayList();
		List<T> values = Lists.newArrayList();
		NBTTagCompound lis = nbt.getCompoundTag(name);
		int size = lis.getInteger("size");
		for (int i = 0; i < size; i++)
			values.add(get(lis, "__" + i, clazz));
		return values;
	}

	public static NBTTagCompound setList(NBTTagCompound nbt, String name, List<?> values) {
		if (nbt == null || values.isEmpty())
			return nbt;
		for (Object o : values)
			if (o != null) {
				if (!NBTType.validClass(o.getClass()))
					throw new IllegalArgumentException();
				break;
			}
		NBTTagCompound lis = new NBTTagCompound();
		lis.setInteger("size", values.size());
		for (int i = 0; i < values.size(); i++)
			set(lis, "__" + i, values.get(i));
		nbt.setTag(name, lis);
		return nbt;
	}

	public static <K, V> Map<K, V> getMap(NBTTagCompound nbt, String name, Class<K> keyClazz, Class<V> valClazz) {
		if (!NBTType.validClass(keyClazz) || !NBTType.validClass(valClazz))
			throw new IllegalArgumentException();
		if (nbt == null || !nbt.hasKey(name, 10))
			return Maps.newHashMap();
		Map<K, V> values = Maps.newHashMap();
		NBTTagCompound map = nbt.getCompoundTag(name);
		List<K> keys = getList(map, "key", keyClazz);
		List<V> vals = getList(map, "value", valClazz);
		Validate.isTrue(keys.size() == vals.size());
		for (int i = 0; i < keys.size(); i++)
			values.put(keys.get(i), vals.get(i));
		return values;
	}

	public static NBTTagCompound setMap(NBTTagCompound nbt, String name, Map<?, ?> values) {
		if (nbt == null || values.isEmpty())
			return nbt;
		List<Entry<?, ?>> entries = Lists.newArrayList();
		for (Entry<?, ?> o : values.entrySet()) {
			if (!NBTType.validClass(o.getKey().getClass()) || !NBTType.validClass(o.getValue().getClass()))
				throw new IllegalArgumentException();
			entries.add(o);
		}
		NBTTagCompound map = new NBTTagCompound();
		setList(map, "key", entries.stream().map(e -> e.getKey()).collect(Collectors.toList()));
		setList(map, "value", entries.stream().map(e -> e.getValue()).collect(Collectors.toList()));
		nbt.setTag(name, map);
		return nbt;
	}

}
