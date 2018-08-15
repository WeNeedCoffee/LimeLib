package mrriegel.limelib.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import mrriegel.limelib.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NBTHelper {

	private static Set<INBTable<?>> iNBTs = new ReferenceOpenHashSet<>();

	static {
			//TODO init;
	}
	
	public static boolean hasTag(NBTTagCompound nbt, String keyName) {
		return nbt != null && nbt.hasKey(keyName);
	}

	//TODO return nbt
	public static void removeTag(NBTTagCompound nbt, String keyName) {
		if (nbt == null)
			return;
		nbt.removeTag(keyName);
	}

	public static String toASCIIString(NBTTagCompound nbt) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			CompressedStreamTools.write(nbt, dos);
		} catch (IOException e) {
		}
		return Utils.toASCII(baos.toString());
	}

	public static NBTTagCompound fromASCIIString(String s) {
		ByteArrayInputStream bais = new ByteArrayInputStream(Utils.fromASCII(s).getBytes());
		DataInputStream dis = new DataInputStream(bais);
		try {
			return CompressedStreamTools.read(dis);
		} catch (IOException e) {
			return null;
		}
	}

	public static int getSize(NBTTagCompound nbt) {
		ByteBuf buf = Unpooled.buffer();
		ByteBufUtils.writeTag(buf, nbt);
		return buf.readableBytes();
	}

	public static Pair<NBTTagCompound, NBTTagCompound> split(NBTTagCompound nbt) {
		if (nbt == null)
			return null;
		List<Pair<String, NBTBase>> ns = nbt.getKeySet().stream().map(s -> Pair.of(s, nbt.getTag(s))).collect(Collectors.toList());
		int nsize = ns.size();
		if (nsize < 2)
			return Pair.of(nbt, new NBTTagCompound());
		NBTTagCompound a = new NBTTagCompound(), b = new NBTTagCompound();
		List<Pair<String, NBTBase>> as = ns.subList(0, (nsize + 1) / 2);
		List<Pair<String, NBTBase>> bs = ns.subList((nsize + 1) / 2, nsize);
		for (Pair<String, NBTBase> p : as)
			a.setTag(p.getKey(), p.getValue());
		for (Pair<String, NBTBase> p : bs)
			b.setTag(p.getKey(), p.getValue());
		return Pair.of(a, b);
	}

	@Deprecated
	public static void register(INBTable<?> n) {
		if (!iNBTs.contains(n))
			iNBTs.add(n);
	}

	public static interface INBTable<T> {

		//TODO replaced by asNBT,pls remove
		void set(NBTTagCompound nbt, String name, T value);

		T get(NBTTagCompound nbt, String name, Class<T> clazz);

		boolean classValid(Class<?> clazz);

		NBTBase asNBT(T value);

		default T defaultValue() {
			return null;
		}

		default Map<?, ?> getEmptyMap() {
			return new Object2ObjectOpenHashMap<>();
		}

		default List<T> getEmptyList() {
			return new ObjectArrayList<>();
		}

		//TODO getMap & getList classes
	}

	private static INBTable<?> getINBT(Class<?> clazz) {
		for (INBTable<?> n : iNBTs)
			if (n.classValid((Class<?>) clazz))
				return n;
		return null;
	}

	static {
		//enum
		register(new INBTable<Enum>() {

			@Override
			public void set(NBTTagCompound nbt, String name, Enum value) {
				nbt.setInteger(name, value.ordinal());
			}

			@Override
			public Enum<?> get(NBTTagCompound nbt, String name, Class<Enum> clazz) {
				return clazz.getEnumConstants()[nbt.getInteger(name)];
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return Enum.class.isAssignableFrom(clazz);
			}

			@Override
			public NBTBase asNBT(Enum value) {
				return new NBTTagInt(value.ordinal());
			}
		});
		register(new INBTable<IForgeRegistryEntry>() {

			@Override
			public void set(NBTTagCompound nbt, String name, IForgeRegistryEntry value) {
				NBTTagCompound entry = new NBTTagCompound();
				entry.setString("id", value.getRegistryName().toString());
				entry.setString("class", value.getRegistryType().getCanonicalName());
				nbt.setTag(name, entry);
			}

			@Override
			public IForgeRegistryEntry get(NBTTagCompound nbt, String name, Class<IForgeRegistryEntry> clazz) {
				NBTTagCompound entry = nbt.getCompoundTag(name);
				String id = entry.getString("id"), clas = entry.getString("class");
				try {
					IForgeRegistry reg = GameRegistry.findRegistry((Class<IForgeRegistryEntry>) Class.forName(clas));
					if (reg != null) {
						return reg.getValue(new ResourceLocation(id));
					}
				} catch (ClassNotFoundException e) {
					return null;
				}
				return null;
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return IForgeRegistryEntry.class.isAssignableFrom(clazz);
			}

			@Override
			public NBTBase asNBT(IForgeRegistryEntry value) {
				NBTTagCompound entry = new NBTTagCompound();
				entry.setString("id", value.getRegistryName().toString());
				entry.setString("class", value.getRegistryType().getCanonicalName());
				return entry;
			}
		});
		register(of(false, (n, s) -> n.getBoolean(s), (n, p) -> n.setBoolean(p.getKey(), p.getValue()), c -> c == Boolean.class || c == boolean.class));
		//		for (NBTType t : NBTType.values())
		//			register(of(t.defaultValue, t.getter, t.setter, t.classes));
	}

	public static <T> INBTable<T> of(T defaultValue, BiFunction<NBTTagCompound, String, T> getter, BiConsumer<NBTTagCompound, Pair<String, T>> setter, Class<?>... classes) {
		return of(defaultValue, getter, setter, clazz -> Arrays.stream(classes).anyMatch(c -> clazz == c));
	}

	public static <T> INBTable<T> of(T defaultValue, BiFunction<NBTTagCompound, String, T> getter, BiConsumer<NBTTagCompound, Pair<String, T>> setter, Predicate<Class<?>> pred) {
		return new INBTable<T>() {

			@Override
			public void set(NBTTagCompound nbt, String name, T value) {
				setter.accept(nbt, Pair.of(name, value));
			}

			@Override
			public T get(NBTTagCompound nbt, String name, Class<T> clazz) {
				return getter.apply(nbt, name);
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return pred.apply(clazz);
			}

			@Override
			public T defaultValue() {
				return defaultValue;
			}

			@Override
			public NBTBase asNBT(T value) {
				// TODO mach ma
				return null;
			}

		};
	}

	private enum NBTType/*TODO implements INBTable*/ {
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
		//TODO blockstate,regsitryname,UUID
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

		public static Map<Class<?>, NBTType> m = new HashMap<>();

		public static boolean validClass(Class<?> clazz) {
			return Enum.class.isAssignableFrom(clazz) || m.get(clazz) != null;
		}

		static {
			for (NBTType n : NBTType.values()) {
				for (Class<?> c : n.classes)
					m.put(c, n);
				//				register(n);
			}
		}

		public void set(NBTTagCompound nbt, String name, Object value) {
			setter.accept(nbt, Pair.of(name, value));
		}

		public Object get(NBTTagCompound nbt, String name, Class<?> clazz) {
			return getter.apply(nbt, name);
		}

		public boolean classValid(Class<?> clazz) {
			return m.get(clazz) != null;
		}

		public Object defaultValue() {
			return defaultValue;
		}
	}

	public static <T> T get(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (Enum.class.isAssignableFrom(clazz)) {
			Optional<Integer> o = getSafe(nbt, name, Integer.class);
			return o.isPresent() ? clazz.getEnumConstants()[o.get()] : null;
		}
		NBTType type = NBTType.m.get(clazz);
		if (type == null)
			throw new IllegalArgumentException();
		if (nbt == null || !nbt.hasKey(name))
			return (T) type.defaultValue();
		return (T) type.get(nbt, name, clazz);
	}

	@Deprecated
	//TODO add getoptional
	private static <T> T get(NBTTagCompound nbt, String name) {
		try {
			return (T) get(nbt, name, Class.forName(nbt.getString(name + "_clazz")));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	//TODO rename to getOptional
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
		NBTType type = NBTType.m.get(clazz);
		if (type == null)
			throw new IllegalArgumentException();
		type.set(nbt, name, value);
		//		nbt.setString(name + "_clazz", clazz.getName());
		return nbt;
	}

	public static <T> List<T> getList(NBTTagCompound nbt, String name, Class<T> clazz) {
		if (!NBTType.validClass(clazz))
			throw new IllegalArgumentException();
		List<T> values = new ObjectArrayList<>();
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

	//TODO change parameter to collection
	public static NBTTagCompound setList(NBTTagCompound nbt, String name, List<?> values) {
		if (nbt == null || values.isEmpty())
			return nbt;
		if (false) {
			List<Class<?>> classes = values.stream().filter(Objects::nonNull).map(Object::getClass).distinct().collect(Collectors.toList());
			if (classes.size() != 1)
				throw new IllegalArgumentException();
			IntArrayList nulls = new IntArrayList();
			for (int i = 0; i < values.size(); i++)
				if (values.get(i) == null)
					nulls.add(i);
			Class<?> clazz = classes.get(0);
			INBTable n = getINBT(clazz);
			if (n == null)
				throw new IllegalArgumentException();
			NBTBase base = n.asNBT(values.stream().filter(Objects::nonNull).findFirst().get());
			if (Arrays.asList(Boolean.class, Byte.class).contains(clazz))
				/*byte array*/;
			else if (Arrays.asList(Short.class, Integer.class, Float.class).contains(clazz) || clazz.isEnum())
				/*int array*/;
			else if (Arrays.asList(Long.class, Double.class).contains(clazz))
				/*long array*/;
			else {
				/*taglist*/;
				NBTTagList list = new NBTTagList();
				for (Object v : values) {
					if (v != null)
						list.appendTag(n.asNBT(v));
					else
						list.appendTag(new NBTNull());
				}
				if (clazz == String.class) {
					;
				} else {
					;
				}
			}
		}
		//TODO use nbttaglist/bytearray/intarray/longarray
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

	@Deprecated
	private static <K, V> Map<K, V> getMap(NBTTagCompound nbt, String name, Class<K> keyClazz, Class<V> valClazz, Supplier<Map<K, V>> supp) {
		return getMap(nbt, name, keyClazz, valClazz);
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

	private static class NBTNull extends NBTTagByte {

		private NBTNull() {
			super((byte) 0);
		}

		@Override
		public byte getId() {
			return 10;
		}

	}

	private static class NBTTagLongArray extends net.minecraft.nbt.NBTTagLongArray {

		private static Field dataField = ReflectionHelper.findField(net.minecraft.nbt.NBTTagLongArray.class, "data", "field_193587_b");

		public NBTTagLongArray(List<Long> list) {
			super(list);
		}

		public NBTTagLongArray(long[] array) {
			super(array);
		}

		public long[] getLongArray() {
			try {
				return (long[]) dataField.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return new long[0];
			}
		}

	}
}
