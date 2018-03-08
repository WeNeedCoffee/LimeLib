package mrriegel.limelib.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class NBTHelper {

	private static Set<INBTable> iNBTs = new ObjectOpenCustomHashSet<>(new Strategy<INBTable>() {

		@Override
		public int hashCode(INBTable o) {
			return System.identityHashCode(o);
		}

		@Override
		public boolean equals(INBTable a, INBTable b) {
			return a == b;
		}

	});

	public static boolean hasTag(NBTTagCompound nbt, String keyName) {
		return nbt != null && nbt.hasKey(keyName);
	}

	public static void removeTag(NBTTagCompound nbt, String keyName) {
		if (nbt == null)
			return;
		nbt.removeTag(keyName);
	}

	public static int getSize(NBTTagCompound nbt) {
		ByteBuf buf = Unpooled.buffer();
		ByteBufUtils.writeTag(buf, nbt);
		return buf.readableBytes();
	}

	@Deprecated
	public static void register(INBTable n) {
		iNBTs.add(n);
	}

	public static interface INBTable {
		void set(NBTTagCompound nbt, String name, Object value);

		Object get(NBTTagCompound nbt, String name, Class<?> clazz);

		boolean classValid(Class<?> clazz);

		default Object defaultValue() {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static INBTable getINBT(Class<?> clazz) {
		for (INBTable n : iNBTs)
			if (n.classValid(clazz))
				return n;
		return null;
	}

	static {
		//enum
		register(new INBTable() {

			@Override
			public void set(NBTTagCompound nbt, String name, Object value) {
				nbt.setInteger(name, ((Enum<?>) value).ordinal());
			}

			@Override
			public Object get(NBTTagCompound nbt, String name, Class<?> clazz) {
				return clazz.getEnumConstants()[nbt.getInteger(name)];
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return Enum.class.isAssignableFrom(clazz);
			}
		});
		for (NBTType t : NBTType.values())
			register(of(t.defaultValue, t.getter, t.setter, t.classes));
	}

	public static INBTable of(Object defaultValue, BiFunction<NBTTagCompound, String, Object> getter, BiConsumer<NBTTagCompound, Pair<String, Object>> setter, Class<?>... classes) {
		return new INBTable() {

			@Override
			public void set(NBTTagCompound nbt, String name, Object value) {
				setter.accept(nbt, Pair.of(name, value));
			}

			@Override
			public Object get(NBTTagCompound nbt, String name, Class<?> clazz) {
				return getter.apply(nbt, name);
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return Arrays.stream(classes).anyMatch(c -> clazz == c);
			}

			@Override
			public Object defaultValue() {
				return defaultValue;
			}

		};
	}

	private static enum NBTType implements INBTable {
		BOOLEAN(false, (n, s) -> n.getBoolean(s), (n, p) -> n.setBoolean(p.getKey(), (boolean) p.getValue()), Boolean.class, boolean.class), //
		BYTE((byte) 0, (n, s) -> n.getByte(s), (n, p) -> n.setByte(p.getKey(), (byte) p.getValue()), Byte.class, byte.class), //
		SHORT((short) 0, (n, s) -> n.getShort(s), (n, p) -> n.setShort(p.getKey(), (short) p.getValue()), Short.class, short.class), //
		INT(0, (n, s) -> n.getInteger(s), (n, p) -> n.setInteger(p.getKey(), (int) p.getValue()), Integer.class, int.class), //
		LONG(0L, (n, s) -> n.getLong(s), (n, p) -> n.setLong(p.getKey(), (long) p.getValue()), Long.class, long.class), //
		FLOAT(0F, (n, s) -> n.getFloat(s), (n, p) -> n.setFloat(p.getKey(), (float) p.getValue()), Float.class, float.class), //
		DOUBLE(0D, (n, s) -> n.getDouble(s), (n, p) -> n.setDouble(p.getKey(), (double) p.getValue()), Double.class, double.class), //
		STRING(null, (n, s) -> n.getString(s), (n, p) -> n.setString(p.getKey(), (String) p.getValue()), String.class), //
		//TODO NBTBase
		NBT(null, (n, s) -> n.getCompoundTag(s), (n, p) -> n.setTag(p.getKey(), (NBTTagCompound) p.getValue()), NBTTagCompound.class), //
		ITEMSTACK(ItemStack.EMPTY, (n, s) -> new ItemStack(n.getCompoundTag(s)), (n, p) -> n.setTag(p.getKey(), ((ItemStack) p.getValue()).writeToNBT(new NBTTagCompound())), ItemStack.class), //
		BLOCKPOS(null, (n, s) -> BlockPos.fromLong(n.getLong(s)), (n, p) -> n.setLong(p.getKey(), ((BlockPos) p.getValue()).toLong()), BlockPos.class, MutableBlockPos.class), //
		FLUIDSTACK(null, (n, s) -> FluidStack.loadFluidStackFromNBT(n.getCompoundTag(s)), (n, p) -> n.setTag(p.getKey(), ((FluidStack) p.getValue()).writeToNBT(new NBTTagCompound())), FluidStack.class);
		//TODO UUID
		Object defaultValue;
		Class<?>[] classes;
		BiFunction<NBTTagCompound, String, Object> getter;
		BiConsumer<NBTTagCompound, Pair<String, Object>> setter;

		private NBTType(Object defaultValue, BiFunction<NBTTagCompound, String, Object> getter, BiConsumer<NBTTagCompound, Pair<String, Object>> setter, Class<?>... classes) {
			this.defaultValue = defaultValue;
			this.classes = classes;
			this.getter = getter;
			this.setter = setter;
		}

		public static Map<Class<?>, NBTType> m = new HashMap<Class<?>, NBTType>();

		public static boolean validClass(Class<?> clazz) {
			return Enum.class.isAssignableFrom(clazz) || m.get(clazz) != null;
		}

		static {
			for (NBTType n : NBTType.values()) {
				for (Class<?> c : n.classes)
					m.put(c, n);
				register(n);
			}
		}

		@Override
		public void set(NBTTagCompound nbt, String name, Object value) {
			setter.accept(nbt, Pair.of(name, value));
		}

		@Override
		public Object get(NBTTagCompound nbt, String name, Class<?> clazz) {
			return getter.apply(nbt, name);
		}

		@Override
		public boolean classValid(Class<?> clazz) {
			return m.get(clazz) != null;
		}

		@Override
		public Object defaultValue() {
			return defaultValue;
		}
	}

	public static <T> T get(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (Enum.class.isAssignableFrom(clazz)) {
			Optional<Integer> o = getSafe(nbt, name, Integer.class);
			return o.isPresent() ? clazz.getEnumConstants()[o.get()] : null;
		}
		INBTable type = NBTType.m.get(clazz);
		if (type == null)
			throw new IllegalArgumentException();
		if (nbt == null || !nbt.hasKey(name))
			return (T) type.defaultValue();
		return (T) type.get(nbt, name, clazz);
	}

	public static <T> Optional<T> getSafe(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (nbt == null || nbt.hasKey(name))
			return Optional.of(get(nbt, name, clazz));
		return Optional.empty();
	}

	public static NBTTagCompound set(NBTTagCompound nbt, String name, Object value) {
		if (nbt == null || value == null)
			return nbt;
		Class<?> clazz = value.getClass();
		if (Enum.class.isAssignableFrom(clazz))
			return set(nbt, name, ((Enum<?>) value).ordinal());
		INBTable type = NBTType.m.get(clazz);
		if (type == null)
			throw new IllegalArgumentException();
		type.set(nbt, name, value);
		return nbt;
	}

	public static <T> List<T> getList(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (!NBTType.validClass(clazz))
			throw new IllegalArgumentException();
		List<T> values = new ObjectArrayList<T>();
		if (nbt == null || !nbt.hasKey(name, 10))
			return values;
		NBTTagCompound lis = nbt.getCompoundTag(name);
		int size = lis.getInteger("size");
		for (int i = 0; i < size; i++)
			values.add(get(lis, "__" + i, clazz));
		return values;
	}

	public static <T> Optional<List<T>> getListSafe(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (nbt == null || nbt.hasKey(name, 10))
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
		Map<K, V> values = new Object2ObjectOpenHashMap<>();
		if (nbt == null || !nbt.hasKey(name, 10))
			return values;
		NBTTagCompound map = nbt.getCompoundTag(name);
		List<K> keys = getList(map, "key", keyClazz);
		List<V> vals = getList(map, "value", valClazz);
		Validate.isTrue(keys.size() == vals.size());
		for (int i = 0; i < keys.size(); i++)
			values.put(keys.get(i), vals.get(i));
		return values;
	}

	public static <K, V> Optional<Map<K, V>> getMapSafe(NBTTagCompound nbt, String name, Class<K> keyClazz, Class<V> valClazz) {
		if (nbt == null || nbt.hasKey(name, 10))
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
