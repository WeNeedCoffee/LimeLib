package mrriegel.limelib.datapart;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DataPart {

	protected BlockPos pos;
	protected World world;

	public void updateServer(World world) {
	}

	public void updateClient(World world) {
	}

	public void onAdded() {
	}

	public void onRemoved() {
	}

	public final void readDataFromNBT(NBTTagCompound compound) {
		pos = BlockPos.fromLong(NBTHelper.getLong(compound, "poS"));
		readFromNBT(compound);
	}

	public final NBTTagCompound writeDataToNBT(NBTTagCompound compound) {
		writeToNBT(compound);
		compound.setString("class", getClass().getName());
		compound.setLong("poS", pos.toLong());
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public final int getX() {
		return pos.getX();
	}

	public final int getY() {
		return pos.getY();
	}

	public final int getZ() {
		return pos.getZ();
	}
}
