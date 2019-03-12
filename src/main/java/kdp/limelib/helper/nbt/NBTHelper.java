package kdp.limelib.helper.nbt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import kdp.limelib.util.GlobalBlockPos;
import kdp.limelib.util.StackWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

@SuppressWarnings("unchecked")
public class NBTHelper {

	public static final String NULL_INDICES = "_n0lL";
	public static final String NULL_AMOUNT = "_n0lL+";
	public static final String TYPE = "_tYpE";
	public static final String LIST = "_lIsT";
	public static final String KEYS = "_kEyS";
	public static final String VALUES = "_vAlUeS";
	private static Set<INBTConverter<?>> converters = new ReferenceOpenHashSet<>();
	private static Map<Class<?>, INBTConverter<?>> converterCache = new IdentityHashMap<>();

	static {
		init();
	}

	public static boolean hasTag(NBTTagCompound nbt, String key) {
		return nbt != null && nbt.hasKey(key);
	}

	public static NBTTagCompound removeTag(NBTTagCompound nbt, String key) {
		if (nbt == null)
			return nbt;
		nbt.removeTag(key);
		return nbt;
	}

	public static <T extends INBTBase> Optional<T> getTagOptional(NBTTagCompound nbt, String key) {
		return hasTag(nbt, key) ? (Optional<T>) Optional.of(nbt.getTag(key)) : Optional.empty();
	}

	public static <T extends INBTBase> Optional<T> getTagOptional(NBTTagCompound nbt, String key, Class<T> clazz) {
		if (!hasTag(nbt, key))
			return Optional.empty();
		INBTBase n = nbt.getTag(key);
		if (clazz.isAssignableFrom(
				Objects.requireNonNull(n, () -> "Who the heck put null into the NBTTagCompound?").getClass()))
			return (Optional<T>) Optional.of(n);
		else
			return Optional.empty();
	}

	public static String toASCIIString(NBTTagCompound nbt) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			CompressedStreamTools.write(nbt, dos);
		} catch (IOException e) {
		}
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	public static NBTTagCompound fromASCIIString(String s) {
		ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(s));
		DataInputStream dis = new DataInputStream(bais);
		try {
			return CompressedStreamTools.read(dis);
		} catch (IOException e) {
			return null;
		}
	}

	public static int getSize(INBTBase nbt) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			nbt.write(dos);
			dos.close();
		} catch (IOException e) {
		}
		return baos.size();
	}

	public static void init() {
		// Primitives + wrappers
		register(new INBTConverter<Object>() {
			private Object2ByteOpenHashMap<Object> class2id = new Object2ByteOpenHashMap<>();
			{
				byte id = 1;
				class2id.put(boolean.class, id++);
				class2id.put(Boolean.class, id++);
				class2id.put(byte.class, id++);
				class2id.put(Byte.class, id++);
				class2id.put(char.class, id++);
				class2id.put(Character.class, id++);
				class2id.put(short.class, id++);
				class2id.put(Short.class, id++);
				class2id.put(int.class, id++);
				class2id.put(Integer.class, id++);
				class2id.put(float.class, id++);
				class2id.put(Float.class, id++);
				class2id.put(long.class, id++);
				class2id.put(Long.class, id++);
				class2id.put(double.class, id++);
				class2id.put(Double.class, id++);
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return class2id.containsKey(clazz);
			}

			@Override
			public INBTBase toNBT(Object value) {
				switch (class2id.getByte(value.getClass())) {
				case 0:
					throw new RuntimeException("primitives1");
				case 1:
				case 2:
					return new NBTTagByte((byte) (((boolean) value) ? 1 : 0));
				case 3:
				case 4:
					return new NBTTagByte((byte) value);
				case 5:
				case 6:
					return new NBTTagShort((short) ((((char) (value))) + Short.MIN_VALUE));
				case 7:
				case 8:
					return new NBTTagShort((short) value);
				case 9:
				case 10:
					return new NBTTagInt((int) value);
				case 11:
				case 12:
					return new NBTTagInt(Float.floatToRawIntBits((float) value));
				case 13:
				case 14:
					return new NBTTagLong((long) value);
				case 15:
				case 16:
					return new NBTTagLong(Double.doubleToRawLongBits((double) value));
				default:
					throw new RuntimeException("primitives2");
				}
			}

			@Override
			public Object toValue(INBTBase nbt, Class<? extends Object> clazz) {
				switch (class2id.getByte(clazz)) {
				case 0:
					throw new RuntimeException("primitives1");
				case 1:
				case 2:
					return ((NBTTagByte) nbt).getByte() != 0;
				case 3:
				case 4:
					return ((NBTTagByte) nbt).getByte();
				case 5:
				case 6:
					return (char) ((((NBTTagShort) nbt).getShort()) - Short.MIN_VALUE);
				case 7:
				case 8:
					return ((NBTTagShort) nbt).getShort();
				case 9:
				case 10:
					return ((NBTTagInt) nbt).getInt();
				case 11:
				case 12:
					return Float.intBitsToFloat(((NBTTagInt) nbt).getInt());
				case 13:
				case 14:
					return ((NBTTagLong) nbt).getLong();
				case 15:
				case 16:
					return Double.longBitsToDouble(((NBTTagLong) nbt).getLong());
				default:
					throw new RuntimeException("primitives2");
				}
			}

			@Override
			public Supplier<Object> defaultValue(Class<? extends Object> clazz) {
				switch (class2id.getByte(clazz)) {
				case 0:
					throw new RuntimeException("primitives1");
				case 1:
				case 2:
					return () -> false;
				case 3:
				case 4:
					return () -> (byte) 0;
				case 5:
				case 6:
					return () -> (char) 0;
				case 7:
				case 8:
					return () -> (short) 0;
				case 9:
				case 10:
					return () -> 0;
				case 11:
				case 12:
					return () -> 0F;
				case 13:
				case 14:
					return () -> 0L;
				case 15:
				case 16:
					return () -> 0D;
				default:
					throw new RuntimeException("primitives2");
				}
			}

		});
		// Primitive arrays
		register(new INBTConverter<Object>() {
			private Object2ByteOpenHashMap<Object> class2id = new Object2ByteOpenHashMap<>();
			{
				byte id = 1;
				class2id.put(boolean[].class, id++);
				class2id.put(byte[].class, id++);
				class2id.put(char[].class, id++);
				class2id.put(short[].class, id++);
				class2id.put(int[].class, id++);
				class2id.put(float[].class, id++);
				class2id.put(long[].class, id++);
				class2id.put(double[].class, id++);
			}

			@Override
			public boolean classValid(Class<?> clazz) {
				return class2id.containsKey(clazz);
			}

			@Override
			public INBTBase toNBT(Object value) {
				switch (class2id.getByte(value.getClass())) {
				case 0:
					throw new RuntimeException("arrays1");
				case 1:
					boolean[] arrB = (boolean[]) value;
					BitSet bs = new BitSet(arrB.length);
					for (int i = 0; i < arrB.length; i++)
						bs.set(i, arrB[i]);
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setByteArray("v", bs.toByteArray());
					if (arrB.length % 8 != 0)
						if (arrB.length <= Byte.MAX_VALUE) {
							nbt.setByte("s", (byte) arrB.length);
						} else if (arrB.length <= Short.MAX_VALUE) {
							nbt.setShort("s", (short) arrB.length);
						} else {
							nbt.setInt("s", arrB.length);
						}
					return nbt;
				case 2:
					return new NBTTagByteArray((byte[]) value);
				case 3:
					return new NBTTagString(new String((char[]) value));
				case 4:
					short[] arrS = (short[]) value;
					ByteBuffer bb = ByteBuffer.allocate(arrS.length * 2);
					for (short s : arrS)
						bb.putShort(s);
					NBTTagCompound nbtt = new NBTTagCompound();
					nbtt.setByteArray("v", bb.array());
					if (arrS.length % 8 != 0)
						if (arrS.length <= Byte.MAX_VALUE) {
							nbtt.setByte("s", (byte) arrS.length);
						} else if (arrS.length <= Short.MAX_VALUE) {
							nbtt.setShort("s", (short) arrS.length);
						} else {
							nbtt.setInt("s", arrS.length);
						}
					return nbtt;
				case 5:
					return new NBTTagIntArray((int[]) value);
				case 6:
					float[] arrF = (float[]) value;
					int[] retF = new int[arrF.length];
					for (int i = 0; i < arrF.length; i++)
						retF[i] = Float.floatToRawIntBits(arrF[i]);
					return new NBTTagIntArray(retF);
				case 7:
					return new NBTTagLongArray((long[]) value);
				case 8:
					double[] arrD = (double[]) value;
					long[] retD = new long[arrD.length];
					for (int i = 0; i < arrD.length; i++)
						retD[i] = Double.doubleToRawLongBits(arrD[i]);
					return new NBTTagLongArray(retD);
				default:
					throw new RuntimeException("arrays2");
				}
			}

			@Override
			public Object toValue(INBTBase nbt, Class<? extends Object> clazz) {
				switch (class2id.getByte(clazz)) {
				case 0:
					throw new RuntimeException("arrays1");
				case 1:
					NBTTagCompound tag = (NBTTagCompound) nbt;
					byte[] arr = tag.getByteArray("v");
					int num = tag.hasKey("s") ? tag.getInt("s") : arr.length << 3;
					BitSet bs = BitSet.valueOf(arr);
					boolean[] ret = new boolean[num];
					for (int i = 0; i < num; i++) {
						ret[i] = bs.get(i);
					}
					return ret;
				case 2:
					return ((NBTTagByteArray) nbt).getByteArray();
				case 3:
					return ((NBTTagString) nbt).getString().toCharArray();
				case 4:
					NBTTagCompound tag2 = (NBTTagCompound) nbt;
					byte[] arrS = tag2.getByteArray("v");
					int num2 = tag2.hasKey("s") ? tag2.getInt("s") : arrS.length >> 1;
					ByteBuffer bb = ByteBuffer.wrap(arrS);
					short[] ret2 = new short[num2];
					for (int i = 0; i < num2; i++) {
						ret2[i] = bb.getShort();
					}
					return ret2;
				case 5:
					return ((NBTTagIntArray) nbt).getIntArray();
				case 6:
					int[] arrF = ((NBTTagIntArray) nbt).getIntArray();
					float[] retF = new float[arrF.length];
					for (int i = 0; i < arrF.length; i++)
						retF[i] = Float.intBitsToFloat(arrF[i]);
					return retF;
				case 7:
					return ((NBTTagLongArray) nbt).getAsLongArray();
				case 8:
					long[] arrD = ((NBTTagLongArray) nbt).getAsLongArray();
					double[] retD = new double[arrD.length];
					for (int i = 0; i < arrD.length; i++)
						retD[i] = Double.longBitsToDouble(arrD[i]);
					return retD;
				default:
					throw new RuntimeException("arrays2");
				}
			}

			@Override
			public Supplier<Object> defaultValue(Class<? extends Object> clazz) {
				switch (class2id.getByte(clazz)) {
				case 0:
					throw new RuntimeException("arrays1");
				case 1:
					return () -> new boolean[0];
				case 2:
					return () -> new byte[0];
				case 3:
					return () -> new char[0];
				case 4:
					return () -> new short[0];
				case 5:
					return () -> new int[0];
				case 6:
					return () -> new float[0];
				case 7:
					return () -> new long[0];
				case 8:
					return () -> new double[0];
				default:
					throw new RuntimeException("arrays2");
				}
			}
		});
		// Enum
		register(of(null, (n, c) -> {
			Object[] enums = c.getEnumConstants();
			int index = ((NBTTagShort) n).getShort();
			if (index < 0 || index >= enums.length) {
				return null;
			}
			return enums[index];
		}, v -> new NBTTagShort((short) ((Enum<?>) v).ordinal()), c -> c.isEnum()));
		// IForgeRegistryEntry
		register(of(null, (n, c) -> {
			NBTTagCompound entry = (NBTTagCompound) n;
			String id = entry.getString("id"), clas = entry.getString("class");
			try {
				@SuppressWarnings("rawtypes")
				IForgeRegistry<?> reg = RegistryManager.ACTIVE
						.getRegistry((Class<IForgeRegistryEntry>) Class.forName(clas));
				if (reg != null) {
					return reg.getValue(new ResourceLocation(id));
				}
			} catch (ClassNotFoundException e) {
				return null;
			}
			return null;
		}, v -> {
			NBTTagCompound entry = new NBTTagCompound();
			entry.setString("id", v.getRegistryName().toString());
			entry.setString("class", v.getRegistryType().getCanonicalName());
			return entry;
		}, c -> IForgeRegistryEntry.class.isAssignableFrom(c)));
		// INBTBase
		register(of(null, (n, c) -> n, v -> v, c -> INBTBase.class.isAssignableFrom(c)));
		// BlockPos
		register(of(null, (n, c) -> BlockPos.fromLong(((NBTTagLong) n).getLong()), v -> new NBTTagLong(v.toLong()),
				BlockPos.class, MutableBlockPos.class));
		// GlobalBlockPos
		register(of(null, (n, c) -> {
			long[] arr = ((NBTTagLongArray) n).getAsLongArray();
			return new GlobalBlockPos(BlockPos.fromLong(arr[0]), (int) arr[1]);
		}, v -> new NBTTagLongArray(new long[] { v.getPos().toLong(), v.getDimension() }), GlobalBlockPos.class));
		// ItemStack
		register(of(c -> ItemStack.EMPTY, (n, c) -> ItemStack.read((NBTTagCompound) n),
				v -> v.write(new NBTTagCompound()), ItemStack.class));
		// UUID
		register(of(null, (n, c) -> {
			long[] l = ((NBTTagLongArray) n).getAsLongArray();
			return new UUID(l[0], l[1]);
		}, v -> new NBTTagLongArray(new long[] { v.getMostSignificantBits(), v.getLeastSignificantBits() }),
				UUID.class));
		// ResourceLocation
		register(of(null, (n, c) -> new ResourceLocation(((NBTTagString) n).getString()),
				v -> new NBTTagString(v.toString()), ResourceLocation.class));
		// FluidStack
		register(of(null, (n, c) -> FluidStack.loadFluidStackFromNBT((NBTTagCompound) n),
				v -> v.writeToNBT(new NBTTagCompound()), FluidStack.class));
		// String
		register(of(c -> "", (n, c) -> ((NBTTagString) n).getString(), v -> new NBTTagString(v), String.class));
		// StackWrapper
		register(of(null, (n, c) -> StackWrapper.loadStackWrapperFromNBT((NBTTagCompound) n),
				v -> v.writeToNBT(new NBTTagCompound()), StackWrapper.class));
	}

	public static <T> INBTConverter<T> of(@Nullable Function<Class<? extends T>, T> defaultValue,
			BiFunction<INBTBase, Class<?>, T> getter, Function<T, INBTBase> setter, Class<?>... classes) {
		return of(defaultValue, getter, setter, clazz -> ArrayUtils.contains(classes, clazz));
	}

	public static <T> INBTConverter<T> of(@Nullable Function<Class<? extends T>, T> defaultValue,
			BiFunction<INBTBase, Class<?>, T> getter, Function<T, INBTBase> setter, Predicate<Class<?>> pred) {
		return new INBTConverter<T>() {

			@Override
			public boolean classValid(Class<?> clazz) {
				return pred.test(clazz);
			}

			@Override
			public Supplier<T> defaultValue(Class<? extends T> clazz) {
				return () -> defaultValue != null ? defaultValue.apply(clazz) : null;
			}

			@Override
			public INBTBase toNBT(T value) {
				return setter.apply(value);
			}

			@Override
			public T toValue(INBTBase nbt, Class<? extends T> clazz) {
				try {
					return getter.apply(nbt, clazz);
				} catch (ClassCastException e) {
					return defaultValue(clazz).get();
				}
			}

		};
	}

	public static void register(INBTConverter<?> converter) {
		Validate.isTrue(ModLoadingContext.get().getActiveContainer().getCurrentState()
				.ordinal() < ModLoadingStage.COMPLETE.ordinal(), "Register the converter earlier.");
		if (!converters.contains(converter)) {
			converters.add(converter);
		}
	}

	private static @Nonnull INBTConverter<?> getConverter(Class<?> clazz) {
		if (converterCache.containsKey(clazz))
			return converterCache.get(clazz);
		List<INBTConverter<?>> cons = converters.stream().filter(c -> c.classValid(clazz)).collect(Collectors.toList());
		Validate.isTrue(cons.size() < 2, "More than one converter found for " + clazz.getName());
		if (cons.isEmpty())
			throw new IllegalArgumentException("No converter found for " + clazz.getName());
		INBTConverter<?> con = cons.get(0);
		converterCache.put(clazz, con);
		return con;
	}

	public static <T> T get(NBTTagCompound nbt, String key, Class<T> clazz) {
		if (nbt == null)
			return null;
		if (clazz == UUID.class)
			return (T) nbt.getUniqueId(key);
		INBTConverter<T> converter = (INBTConverter<T>) getConverter(clazz);
		if (!nbt.hasKey(key))
			return converter.defaultValue(clazz).get();
		return converter.toValue(nbt.getTag(key), clazz);
	}

	public static <T> Optional<T> getOptional(NBTTagCompound nbt, String key, Class<T> clazz) {
		if (nbt == null)
			return Optional.empty();
		if (clazz == UUID.class && nbt.contains(key + "Most", 99) && nbt.contains(key + "Least", 99))
			return (Optional<T>) Optional.of(nbt.getUniqueId(key));
		if (!nbt.hasKey(key))
			return Optional.empty();
		INBTConverter<T> converter = (INBTConverter<T>) getConverter(clazz);
		return Optional.of(converter.toValue(nbt.getTag(key), clazz));
	}

	public static <T> NBTTagCompound set(NBTTagCompound nbt, String key, T value) {
		if (nbt == null)
			return null;
		if (Objects.requireNonNull(value, () -> "No null value allowed. key: " + key).getClass() == UUID.class) {
			nbt.setUniqueId(key, (UUID) value);
			return nbt;
		}
		INBTConverter<T> converter = (INBTConverter<T>) getConverter(value.getClass());
		nbt.setTag(key, converter.toNBT(value));
		return nbt;
	}

	public static <T, C extends Collection<T>> C getCollection(NBTTagCompound nbt, String key, Class<T> clazz) {
		Supplier<C> supplier = null;
		if (clazz == boolean.class || clazz == Boolean.class) {
			supplier = () -> (C) new BooleanArrayList();
		} else if (clazz == byte.class || clazz == Byte.class) {
			supplier = () -> (C) new ByteArrayList();
		} else if (clazz == char.class || clazz == Character.class) {
			supplier = () -> (C) new CharArrayList();
		} else if (clazz == short.class || clazz == Short.class) {
			supplier = () -> (C) new ShortArrayList();
		} else if (clazz == int.class || clazz == Integer.class) {
			supplier = () -> (C) new IntArrayList();
		} else if (clazz == float.class || clazz == Float.class) {
			supplier = () -> (C) new FloatArrayList();
		} else if (clazz == long.class || clazz == Long.class) {
			supplier = () -> (C) new LongArrayList();
		} else if (clazz == double.class || clazz == Double.class) {
			supplier = () -> (C) new DoubleArrayList();
		} else
			supplier = () -> (C) new ArrayList<>();
		return getCollection(nbt, key, clazz, supplier);
	}

	public static <T, C extends Collection<T>> C getCollection(NBTTagCompound nbt, String key, Class<T> clazz,
			Supplier<C> supplier) {
		C result = supplier.get();
		if (nbt == null)
			return result;
		NBTTagCompound collectionTag = nbt.getCompound(key);
		if (collectionTag == null || collectionTag.isEmpty())
			return result;
		if (collectionTag.contains(NULL_AMOUNT, 3)) {
			result.addAll(Collections.nCopies(collectionTag.getInt(NULL_AMOUNT), null));
			return result;
		}
		INBTConverter<T> converter = (INBTConverter<T>) getConverter(clazz);
		INBTBase n = collectionTag.getTag(LIST);
		List<T> tmpResults = new ArrayList<>();
		if (n instanceof NBTTagByteArray) {
			byte[] array = ((NBTTagByteArray) n).getByteArray();
			for (byte b : array) {
				tmpResults.add(converter.toValue(new NBTTagByte(b), clazz));
			}
		} else if (n instanceof NBTTagIntArray) {
			int[] array = ((NBTTagIntArray) n).getIntArray();
			byte type = collectionTag.getByte(TYPE);
			IntFunction<INBTBase> func = null;
			switch (type) {
			case 2:
				func = i -> new NBTTagShort((short) i);
				break;
			case 3:
				func = i -> new NBTTagInt(i);
				break;
			case 5:
				func = i -> new NBTTagFloat(Float.intBitsToFloat(i));
				break;
			default:
				break;
			}
			for (int i : array) {
				tmpResults.add(converter.toValue(func.apply(i), clazz));
			}
		} else if (n instanceof NBTTagLongArray) {
			long[] array = ((NBTTagLongArray) n).getAsLongArray();
			byte type = collectionTag.getByte(TYPE);
			LongFunction<INBTBase> func = null;
			switch (type) {
			case 4:
				func = l -> new NBTTagLong(l);
				break;
			case 6:
				func = l -> new NBTTagDouble(Double.longBitsToDouble(l));
				break;
			default:
				break;
			}
			for (long l : array) {
				tmpResults.add(converter.toValue(func.apply(l), clazz));
			}
		} else if (n instanceof NBTTagList) {
			for (INBTBase nn : (NBTTagList) n) {
				tmpResults.add(converter.toValue(nn, clazz));
			}
		} else {
			throw new IllegalStateException("unexpected value: " + n);
		}
		int[] nullIndices = collectionTag.getIntArray(NULL_INDICES);
		for (int i : nullIndices) {
			tmpResults.add(i, null);
		}
		result.addAll(tmpResults);
		return result;
	}

	public static <T> NBTTagCompound setCollection(NBTTagCompound nbt, String key, Collection<T> values) {
		if (nbt == null)
			return null;
		NBTTagCompound collectionTag = new NBTTagCompound();
		nbt.setTag(key, collectionTag);
		int nullAmount = Collections.frequency(values, null);
		if (values.isEmpty() || values.size() == nullAmount) {
			collectionTag.setInt(NULL_AMOUNT, nullAmount);
			return nbt;
		}
		Set<INBTConverter<T>> converters = new ReferenceOpenHashSet<>();
		IntArrayList nullIndices = new IntArrayList();
		int tmp = 0;
		for (Object o : values)
			if (o != null) {
				INBTConverter<T> converter = (INBTConverter<T>) getConverter(o.getClass());
				converters.add(converter);
				tmp++;
			} else {
				nullIndices.add(tmp++);
			}
		if (converters.size() != 1)
			throw new IllegalArgumentException(
					"Found " + converters.size() + " converters for " + values.stream().filter(Objects::nonNull)
							.map(Object::getClass).distinct().collect(Collectors.toList()) + ". Only 1 allowed.");
		INBTConverter<T> converter = converters.iterator().next();
		if (!nullIndices.isEmpty()) {
			collectionTag.setIntArray(NULL_INDICES, nullIndices.toIntArray());
		}
		NBTTagCollection<? extends INBTBase> collection = null;
		Collection<T> nonNullValues = values.stream().filter(Objects::nonNull).collect(Collectors.toList());
		INBTBase base = converter.toNBT(nonNullValues.iterator().next());
		if (base instanceof NBTTagByte) {
			ByteArrayList list = new ByteArrayList();
			for (T o : nonNullValues) {
				list.add(((NBTTagByte) converter.toNBT(o)).getByte());
			}
			collection = new NBTTagByteArray(list.toByteArray());
		} else if (base instanceof NBTTagShort || base instanceof NBTTagInt || base instanceof NBTTagFloat) {
			IntArrayList list = new IntArrayList();
			for (T o : nonNullValues) {
				if (base instanceof NBTTagShort || base instanceof NBTTagInt) {
					list.add(((NBTPrimitive) converter.toNBT(o)).getInt());
				} else {
					list.add(Float.floatToRawIntBits(((NBTTagFloat) converter.toNBT(o)).getFloat()));
				}
			}
			collection = new NBTTagIntArray(list.toIntArray());
			collectionTag.setByte(TYPE, base.getId());
		} else if (base instanceof NBTTagLong || base instanceof NBTTagDouble) {
			LongArrayList list = new LongArrayList();
			for (T o : nonNullValues) {
				if (base instanceof NBTTagLong) {
					list.add(((NBTTagLong) converter.toNBT(o)).getLong());
				} else {
					list.add(Double.doubleToRawLongBits(((NBTTagDouble) converter.toNBT(o)).getDouble()));
				}
			}
			collection = new NBTTagLongArray(list.toLongArray());
			collectionTag.setByte(TYPE, base.getId());
		} else {
			NBTTagList list = new NBTTagList();
			for (T o : nonNullValues) {
				list.add(converter.toNBT(o));
			}
			collection = list;
		}
		collectionTag.setTag(LIST, collection);
		return nbt;
	}

	public static <K, V> Map<K, V> getMap(NBTTagCompound nbt, String key, Class<K> keyClazz, Class<V> valClazz) {
		Supplier<Map<K, V>> supplier = null;
		if (keyClazz == boolean.class || keyClazz == Boolean.class) {
			supplier = () -> new Object2ObjectArrayMap<>(2);
		} else if (keyClazz == byte.class || keyClazz == Byte.class) {
			supplier = () -> (Map<K, V>) new Byte2ObjectOpenHashMap<>();
		} else if (keyClazz == char.class || keyClazz == Character.class) {
			supplier = () -> (Map<K, V>) new Char2ObjectOpenHashMap<>();
		} else if (keyClazz == short.class || keyClazz == Short.class) {
			supplier = () -> (Map<K, V>) new Short2ObjectOpenHashMap<>();
		} else if (keyClazz == int.class || keyClazz == Integer.class) {
			supplier = () -> (Map<K, V>) new Int2ObjectOpenHashMap<>();
		} else if (keyClazz == float.class || keyClazz == Float.class) {
			supplier = () -> (Map<K, V>) new Float2ObjectOpenHashMap<>();
		} else if (keyClazz == long.class || keyClazz == Long.class) {
			supplier = () -> (Map<K, V>) new Long2ObjectOpenHashMap<>();
		} else if (keyClazz == double.class || keyClazz == Double.class) {
			supplier = () -> (Map<K, V>) new Double2ObjectOpenHashMap<>();
		} else
			supplier = () -> new HashMap<>();
		return getMap(nbt, key, keyClazz, valClazz, supplier);
	}

	public static <K, V> Map<K, V> getMap(NBTTagCompound nbt, String key, Class<K> keyClazz, Class<V> valClazz,
			Supplier<Map<K, V>> supplier) {
		Map<K, V> result = supplier.get();
		if (nbt == null)
			return result;
		NBTTagCompound mapTag = nbt.getCompound(key);
		List<K> keys = getCollection(mapTag, KEYS, keyClazz, ArrayList::new);
		List<V> values = getCollection(mapTag, VALUES, valClazz, ArrayList::new);
		Validate.isTrue(keys.size() == values.size(), "No equal amount of keys and values." + System.lineSeparator()
				+ keys + System.lineSeparator() + values);
		IntStream.range(0, keys.size()).forEach(i -> result.put(keys.get(i), values.get(i)));
		return result;
	}

	public static <K, V> NBTTagCompound setMap(NBTTagCompound nbt, String key, Map<K, V> values) {
		if (nbt == null || values.isEmpty())
			return null;
		NBTTagCompound mapTag = new NBTTagCompound();
		nbt.setTag(key, mapTag);
		setCollection(mapTag, KEYS, values.keySet());
		setCollection(mapTag, VALUES, values.values());
		return nbt;
	}
}
