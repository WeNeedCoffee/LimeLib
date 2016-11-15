package mrriegel.limelib.datapart;

import mrriegel.limelib.network.DataPartSyncMessage;
import mrriegel.limelib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Deprecated
public class DataPart {

	protected boolean isDirty, sync;
	protected World world;
	protected BlockPos pos;

	public void sync(EntityPlayerMP player) {
		PacketHandler.sendTo(new DataPartSyncMessage(this), player);
	}

	public void onUpdate() {

	}

	public void markForSync() {
		this.sync = true;
	}

	public void markDirty() {
		this.isDirty = true;
		DataPartSavedData.get(world).markDirty();
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.pos = BlockPos.fromLong(compound.getLong("DATApos"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("class", getClass().getName());
		compound.setLong("DATApos", pos.toLong());
		return compound;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
		markDirty();
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public World getWorld() {
		return world;
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

	public boolean onServer() {
		return !world.isRemote;
	}

	public boolean onClient() {
		return !onServer();
	}

}
