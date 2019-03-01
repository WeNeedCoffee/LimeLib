package kdp.limelib.helper;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;

public class NBTBuilder {

	private final NBTTagCompound nbt;

	private NBTBuilder(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	public static NBTBuilder of(NBTTagCompound nbt) {
		return new NBTBuilder(Objects.requireNonNull(nbt));
	}

	public static NBTBuilder of() {
		return new NBTBuilder(new NBTTagCompound());
	}

	public NBTTagCompound build() {
		return nbt;
	}

	public NBTBuilder set(String key, boolean value) {
		nbt.setBoolean(key, value);
		return this;
	}

	public NBTBuilder set(String key, byte value) {
		nbt.setByte(key, value);
		return this;
	}

	public NBTBuilder set(String key, short value) {
		nbt.setShort(key, value);
		return this;
	}

	public NBTBuilder set(String key, int value) {
		nbt.setInt(key, value);
		return this;
	}

	public NBTBuilder set(String key, long value) {
		nbt.setLong(key, value);
		return this;
	}

	public NBTBuilder set(String key, float value) {
		nbt.setFloat(key, value);
		return this;
	}

	public NBTBuilder set(String key, double value) {
		nbt.setDouble(key, value);
		return this;
	}
}
