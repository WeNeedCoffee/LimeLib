package mrriegel.limelib.datapart;

import java.util.Collection;
import java.util.Iterator;

import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DataPartRegistry {

	public Multimap<GlobalBlockPos, DataPart> partMap = HashMultimap.<GlobalBlockPos, DataPart> create();

	public static DataPartRegistry get(World world) {
		if (world.hasCapability(CapabilityDataPart.DATAPART, null)) {
			return world.getCapability(CapabilityDataPart.DATAPART, null);
		}
		return null;
	}

	public Collection<DataPart> getDataParts(GlobalBlockPos pos) {
		return partMap.get(pos);
	}

	public Collection<DataPart> getDataParts(World world, BlockPos pos) {
		return getDataParts(new GlobalBlockPos(pos, world));
	}

	public DataPart getDataPart(GlobalBlockPos pos, String name) {
		for (DataPart d : getDataParts(pos))
			if (d.getName().equals(name))
				return d;
		return null;
	}

	public DataPart getDataPart(World world, BlockPos pos, String name) {
		for (DataPart d : getDataParts(world, pos))
			if (d.getName().equals(name))
				return d;
		return null;
	}

	public boolean addDataPart(GlobalBlockPos pos, DataPart part, boolean force) {
		part.pos = pos;
		if (partMap.get(pos) != null && partMap.get(pos).stream().anyMatch(p -> p.getName().equals(part.getName()))) {
			if (force) {
				partMap.put(pos, part);
				part.sync();
				return true;
			}
			return false;
		} else {
			partMap.put(pos, part);
			part.sync();
			return true;
		}
	}

	public boolean addDataPart(World world, BlockPos pos, DataPart part, boolean force) {
		return addDataPart(new GlobalBlockPos(pos, world), part, force);
	}

	public boolean removeDataPart(GlobalBlockPos pos, String name) {
		Collection<DataPart> col = getDataParts(pos);
		boolean removed = false;
		if (partMap.containsKey(pos)) {
			Iterator<DataPart> it = col.iterator();
			while (it.hasNext()) {
				DataPart next = it.next();
				if (next.getName().equals(name)) {
					it.remove();
					removed = true;
					break;
				}
			}
			partMap.replaceValues(pos, col);
		}
		return removed;
	}

	public void removeAllDataParts(GlobalBlockPos pos) {
		if (partMap.containsKey(pos)) {
			partMap.removeAll(pos);
		}
	}

}
