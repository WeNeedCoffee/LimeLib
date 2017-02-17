package mrriegel.limelib.datapart;

import javax.annotation.Nonnull;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.DataPartSyncMessage;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class DataPart {

	GlobalBlockPos pos;
	private String name = firstName();

	public void updateServer(World world) {
	}

	public void updateClient(World world) {
	}

	public void sync() {
		if (!getWorld().isRemote)
			PacketHandler.sendToAllAround(new DataPartSyncMessage(this), new TargetPoint(pos.getDimension(), getX(), getY(), getZ(), 12));
	}

	public final void readDataFromNBT(NBTTagCompound compound) {
		pos = GlobalBlockPos.loadGlobalPosFromNBT(NBTHelper.getTag(compound, "gpos"));
		name = NBTHelper.getString(compound, "naMe");
		readFromNBT(compound);
	}

	public final NBTTagCompound writeDataToNBT(NBTTagCompound compound) {
		writeToNBT(compound);
		compound.setString("class", getClass().getName());
		compound.setString("naMe", name);
		NBTTagCompound nbt = new NBTTagCompound();
		pos.writeToNBT(nbt);
		NBTHelper.setTag(compound, "gpos", nbt);
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}

	public String getName() {
		return name;
	}

	protected abstract @Nonnull String firstName();

	public void setName(String name) {
		this.name = name;
		sync();
	}
	public GlobalBlockPos getGlobalPos() {
		return pos;
	}
	public World getWorld() {
		return pos.getWorld();
	}

	public BlockPos getPos() {
		return pos.getPos();
	}

	public final int getX() {
		return pos.getPos().getX();
	}

	public final int getY() {
		return pos.getPos().getY();
	}

	public final int getZ() {
		return pos.getPos().getZ();
	}

}
