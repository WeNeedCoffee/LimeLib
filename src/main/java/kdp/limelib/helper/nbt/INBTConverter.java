package kdp.limelib.helper.nbt;

import java.util.function.Supplier;

import net.minecraft.nbt.INBT;

public interface INBTConverter<T> {

    boolean classValid(Class<?> clazz);

    INBT toNBT(T value);

    T toValue(INBT nbt, Class<? extends T> clazz);

    default Supplier<T> defaultValue(Class<? extends T> clazz) {
        return () -> null;
    }
}
