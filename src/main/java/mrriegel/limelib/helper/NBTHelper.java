package mrriegel.limelib.helper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

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
		BOOLEAN(1, false, Boolean.class, (n, s) -> n.getBoolean(s), (n, p) -> n.setBoolean(p.getKey(), (boolean) p.getValue())), //
		BYTE(1, (byte) 0, Byte.class, (n, s) -> n.getByte(s), (n, p) -> n.setByte(p.getKey(), (byte) p.getValue())), //
		SHORT(2, (short) 0, Short.class, (n, s) -> n.getShort(s), (n, p) -> n.setShort(p.getKey(), (short) p.getValue())), //
		INT(3, 0, Integer.class, (n, s) -> n.getInteger(s), (n, p) -> n.setInteger(p.getKey(), (int) p.getValue())), //
		LONG(4, 0L, Long.class, (n, s) -> n.getLong(s), (n, p) -> n.setLong(p.getKey(), (long) p.getValue())), //
		FLOAT(5, 0F, Float.class, (n, s) -> n.getFloat(s), (n, p) -> n.setFloat(p.getKey(), (float) p.getValue())), //
		DOUBLE(6, 0D, Double.class, (n, s) -> n.getDouble(s), (n, p) -> n.setDouble(p.getKey(), (double) p.getValue())), //
		STRING(8, null, String.class, (n, s) -> n.getString(s), (n, p) -> n.setString(p.getKey(), (String) p.getValue())), //
		NBT(10, null, NBTTagCompound.class, (n, s) -> n.getCompoundTag(s), (n, p) -> n.setTag(p.getKey(), (NBTTagCompound) p.getValue())), //
		ITEMSTACK(10, ItemStack.EMPTY, ItemStack.class, (n, s) -> new ItemStack(n.getCompoundTag(s)), (n, p) -> n.setTag(p.getKey(), ((ItemStack) p.getValue()).writeToNBT(new NBTTagCompound()))), //
		BLOCKPOS(4, null, BlockPos.class, (n, s) -> BlockPos.fromLong(n.getLong(s)), (n, p) -> n.setLong(p.getKey(), ((BlockPos) p.getValue()).toLong()));

		int tagID;
		Object defaultValue;
		Class<?> clazz;
		BiFunction<NBTTagCompound, String, Object> getter;
		BiConsumer<NBTTagCompound, Pair<String, Object>> setter;

		private NBTType(int tagID, Object defaultValue, Class<?> clazz, BiFunction<NBTTagCompound, String, Object> getter, BiConsumer<NBTTagCompound, Pair<String, Object>> setter) {
			this.tagID = tagID;
			this.defaultValue = defaultValue;
			this.clazz = clazz;
			this.getter = getter;
			this.setter = setter;
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
			return (T) type.defaultValue;
		return (T) type.getter.apply(nbt, name);
	}

	public static NBTTagCompound set(NBTTagCompound nbt, String name, Object value) {
		if (nbt == null || value == null)
			return nbt;
		if (Enum.class.isAssignableFrom(value.getClass()))
			return set(nbt, name, ((Enum<?>) value).ordinal());
		NBTType type = NBTType.m.get(value.getClass());
		if (type == null)
			throw new IllegalArgumentException();
		type.setter.accept(nbt, Pair.of(name, value));
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
