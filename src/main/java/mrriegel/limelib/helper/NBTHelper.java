package mrriegel.limelib.helper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

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
		BOOLEAN(1, false, (n, s) -> n.getBoolean(s), (n, p) -> n.setBoolean(p.getKey(), (boolean) p.getValue()), Boolean.class, boolean.class), //
		BYTE(1, (byte) 0, (n, s) -> n.getByte(s), (n, p) -> n.setByte(p.getKey(), (byte) p.getValue()), Byte.class, byte.class), //
		SHORT(2, (short) 0, (n, s) -> n.getShort(s), (n, p) -> n.setShort(p.getKey(), (short) p.getValue()), Short.class, short.class), //
		INT(3, 0, (n, s) -> n.getInteger(s), (n, p) -> n.setInteger(p.getKey(), (int) p.getValue()), Integer.class, int.class), //
		LONG(4, 0L, (n, s) -> n.getLong(s), (n, p) -> n.setLong(p.getKey(), (long) p.getValue()), Long.class, long.class), //
		FLOAT(5, 0F, (n, s) -> n.getFloat(s), (n, p) -> n.setFloat(p.getKey(), (float) p.getValue()), Float.class, float.class), //
		DOUBLE(6, 0D, (n, s) -> n.getDouble(s), (n, p) -> n.setDouble(p.getKey(), (double) p.getValue()), Double.class, double.class), //
		STRING(8, null, (n, s) -> n.getString(s), (n, p) -> n.setString(p.getKey(), (String) p.getValue()), String.class), //
		NBT(10, null, (n, s) -> n.getCompoundTag(s), (n, p) -> n.setTag(p.getKey(), (NBTTagCompound) p.getValue()), NBTTagCompound.class), //
		ITEMSTACK(10, ItemStack.EMPTY, (n, s) -> new ItemStack(n.getCompoundTag(s)), (n, p) -> n.setTag(p.getKey(), ((ItemStack) p.getValue()).writeToNBT(new NBTTagCompound())), ItemStack.class), //
		BLOCKPOS(4, null, (n, s) -> BlockPos.fromLong(n.getLong(s)), (n, p) -> n.setLong(p.getKey(), ((BlockPos) p.getValue()).toLong()), BlockPos.class, MutableBlockPos.class);

		int tagID;
		Object defaultValue;
		Class<?>[] clazz;
		BiFunction<NBTTagCompound, String, Object> getter;
		BiConsumer<NBTTagCompound, Pair<String, Object>> setter;

		private NBTType(int tagID, Object defaultValue, BiFunction<NBTTagCompound, String, Object> getter, BiConsumer<NBTTagCompound, Pair<String, Object>> setter, Class<?>... clazz) {
			this.tagID = tagID;
			this.defaultValue = defaultValue;
			this.clazz = clazz;
			this.getter = getter;
			this.setter = setter;
		}

		public static Map<Class<?>, NBTType> m = Maps.newHashMap();

		public static boolean validClass(Class<?> clazz) {
			return Enum.class.isAssignableFrom(clazz) || m.get(clazz) != null;
		}

		static {
			for (NBTType n : NBTType.values())
				for (Class<?> c : n.clazz)
					m.put(c, n);
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

	public static <T> Optional<T> getSafe(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (nbt.hasKey(name))
			return Optional.of(get(nbt, name, clazz));
		return Optional.empty();
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

	public static <T> Optional<List<T>> getListSafe(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (nbt.hasKey(name))
			return Optional.of(getList(nbt, name, clazz));
		return Optional.empty();
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

	public static <K, V> Optional<Map<K, V>> getMapSafe(NBTTagCompound nbt, String name, Class<K> keyClazz, Class<V> valClazz) {
		if (nbt.hasKey(name))
			return Optional.of(getMap(nbt, name, keyClazz, valClazz));
		return Optional.empty();
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
