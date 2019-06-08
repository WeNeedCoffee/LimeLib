package kdp.limelib.helper.nbt;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import net.minecraft.nbt.CompoundNBT;

public class NBTBuilder {

    private final CompoundNBT nbt;

    private NBTBuilder(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public static NBTBuilder of(CompoundNBT nbt) {
        return new NBTBuilder(Objects.requireNonNull(nbt));
    }

    public static NBTBuilder of() {
        return new NBTBuilder(new CompoundNBT());
    }

    public CompoundNBT build() {
        return nbt;
    }

    public NBTBuilder set(String key, boolean value) {
        nbt.putBoolean(key, value);
        return this;
    }

    public NBTBuilder set(String key, byte value) {
        nbt.putByte(key, value);
        return this;
    }

    public NBTBuilder set(String key, char value) {
        set(key, (Object) value);
        return this;
    }

    public NBTBuilder set(String key, short value) {
        nbt.putShort(key, value);
        return this;
    }

    public NBTBuilder set(String key, int value) {
        nbt.putInt(key, value);
        return this;
    }

    public NBTBuilder set(String key, long value) {
        nbt.putLong(key, value);
        return this;
    }

    public NBTBuilder set(String key, float value) {
        nbt.putFloat(key, value);
        return this;
    }

    public NBTBuilder set(String key, double value) {
        nbt.putDouble(key, value);
        return this;
    }

    public NBTBuilder set(String key, Object value) {
        NBTHelper.set(nbt, key, value);
        return this;
    }

    public NBTBuilder setCollection(String key, Collection<?> values) {
        NBTHelper.setCollection(nbt, key, values);
        return this;
    }

    public NBTBuilder setMap(String key, Map<?, ?> values) {
        NBTHelper.setMap(nbt, key, values);
        return this;
    }
}
