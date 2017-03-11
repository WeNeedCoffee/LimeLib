package mrriegel.limelib.datapart;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DataPart {

	protected BlockPos pos;
	protected World world;
	public int ticksExisted;

	public void updateServer(World world) {
	}

	public void updateClient(World world) {
	}

	public void onAdded() {
	}

	public void onRemoved() {
	}

	public void onRightClicked(EntityPlayer player, EnumHand hand) {
	}

	public void onLeftClicked(EntityPlayer player, EnumHand hand) {
	}

	public boolean clientValid() {
		return true;
	}

	public final void readDataFromNBT(NBTTagCompound compound) {
		pos = BlockPos.fromLong(NBTHelper.getLong(compound, "poS"));
		readFromNBT(compound);
	}

	public final NBTTagCompound writeDataToNBT(NBTTagCompound compound) {
		writeToNBT(compound);
		compound.setString("id", DataPartRegistry.PARTS.inverse().get(getClass()));
		compound.setLong("poS", pos.toLong());
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}

	protected final DataPartRegistry getRegistry() {
		DataPartRegistry d = DataPartRegistry.get(world);
		assert d != null;
		return d;
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
