package mrriegel.limelib.datapart;

import java.util.Map;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

@Deprecated
public class DataPart {

	public static Map<GlobalBlockPos, DataPart> partMap;

	protected GlobalBlockPos pos;

	public void update(World world) {

	}

	public void markDirty() {
		DataPartSavedData.get(getWorld()).markDirty();
	}

	public void readFromNBT(NBTTagCompound compound) {
		pos = GlobalBlockPos.loadGlobalPosFromNBT(NBTHelper.getTag(compound, "gpos"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("class", getClass().getName());
		NBTTagCompound nbt = new NBTTagCompound();
		pos.writeToNBT(nbt);
		NBTHelper.setTag(compound, "gpos", nbt);
		return compound;
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

	//	public boolean onServer() {
	//		return !world.isRemote;
	//	}
	//
	//	public boolean onClient() {
	//		return !onServer();
	//	}

	public static DataPart getDataPart(GlobalBlockPos pos) {
		if (DataPart.partMap == null)
			DataPart.partMap = Maps.newHashMap();
		return DataPart.partMap.get(pos);
	}

	public static DataPart getDataPart(World world, BlockPos pos) {
		return getDataPart(new GlobalBlockPos(pos, world));
	}

	public static boolean addDataPart(GlobalBlockPos pos, DataPart part, boolean force) {
		if (DataPart.partMap == null)
			DataPart.partMap = Maps.newHashMap();
		part.pos = pos;
		if (DataPart.partMap.get(pos) != null) {
			if (force) {
				DataPart.partMap.put(pos, part);
				part.markDirty();
				return true;
			}
			return false;
		} else {
			System.out.println("1 " + DataPart.partMap);
			DataPart.partMap.put(pos, part);
			System.out.println("2 " + DataPart.partMap.toString());
			part.markDirty();
			return true;
		}
	}

	public static boolean addDataPart(World world, BlockPos pos, DataPart part, boolean force) {
		return addDataPart(new GlobalBlockPos(pos, world), part, force);
	}

	public static void removeDataPart(GlobalBlockPos pos) {
		if (DataPart.partMap == null)
			DataPart.partMap = Maps.newHashMap();
		if (DataPart.partMap.containsKey(pos)) {
			DataPart.partMap.get(pos).markDirty();
			DataPart.partMap.remove(pos);
		}
	}

}
