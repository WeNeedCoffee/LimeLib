package kdp.limelib.helper.nbt;

import java.util.function.Supplier;

import net.minecraft.nbt.INBTBase;

public interface INBTConverter<T> {
	boolean classValid(Class<?> clazz);

	INBTBase toNBT(T value);

	T toValue(INBTBase nbt, Class<? extends T> clazz);

	default Supplier<T> defaultValue(Class<? extends T> clazz) {
		return () -> null;
	}
}
