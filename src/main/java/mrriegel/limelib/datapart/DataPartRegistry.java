package mrriegel.limelib.datapart;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.DataPartSyncMessage;
import mrriegel.limelib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DataPartRegistry implements INBTSerializable<NBTTagCompound> {

	public static final ResourceLocation LOCATION = new ResourceLocation(LimeLib.MODID + ":datapart");
	public static final BiMap<String, Class<? extends DataPart>> PARTS = HashBiMap.create();

	private Map<BlockPos, DataPart> partMap = Maps.newHashMap();
	public World world;

	public static DataPartRegistry get(World world) {
		if (world == null)
			return null;
		DataPartRegistry reg = world.hasCapability(CapabilityDataPart.DATAPART, null) ? world.getCapability(CapabilityDataPart.DATAPART, null) : null;
		if (reg != null) {
			if (reg.world == null)
				reg.world = world;
			for (DataPart part : reg.getParts())
				if (part.world == null)
					part.world = world;
		}
		return reg;
	}

	public static void register(String name, Class<? extends DataPart> clazz) {
		DataPartRegistry.PARTS.put(name, clazz);
	}

	public DataPart getDataPart(BlockPos pos) {
		return partMap.get(pos);
	}

	public BlockPos nextPos(BlockPos pos) {
		Set<BlockPos> set = Sets.newHashSet();
		int count = 0;
		while (getDataPart(pos) != null) {
			count++;
			if (count > 1000)
				return null;
			set.add(pos);
			while (set.contains(pos))
				pos = pos.offset(EnumFacing.VALUES[world.rand.nextInt(6)]);
		}
		return pos;
	}

	public boolean addDataPart(BlockPos pos, DataPart part, boolean force) {
		Validate.notNull(part);
		if (!PARTS.inverse().containsKey(part.getClass())) {
			LimeLib.log.error(part.getClass() + " not registered.");
			return false;
		}
		if (world.isRemote && !part.clientValid())
			return false;
		part.pos = pos;
		part.world = world;
		if (partMap.get(pos) != null) {
			if (force) {
				partMap.put(pos, part);
				part.onAdded();
				sync(pos, true);
				return true;
			}
			return false;
		} else {
			partMap.put(pos, part);
			part.onAdded();
			sync(pos, true);
			return true;
		}
	}

	public void removeDataPart(BlockPos pos) {
		if (partMap.containsKey(pos)) {
			partMap.get(pos).onRemoved();
			partMap.remove(pos);
			sync(pos, true);
		}
	}

	public void clearWorld() {
		partMap.clear();
	}

	public Collection<DataPart> getParts() {
		return Collections.unmodifiableCollection(partMap.values());
	}

	public void sync(BlockPos pos, boolean toAllPlayers) {
		if (world != null && !world.isRemote && (getDataPart(pos) == null || getDataPart(pos).clientValid())) {
			IMessage message = new DataPartSyncMessage(getDataPart(pos), pos, partMap.values().stream().map(DataPart::getPos).collect(Collectors.toList()));
			if (toAllPlayers)
				for (EntityPlayer player : world.playerEntities)
					PacketHandler.sendTo(message, (EntityPlayerMP) player);
			else
				PacketHandler.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 18));
		}
	}

	public void sync(BlockPos pos) {
		sync(pos, false);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		List<NBTTagCompound> nbts = Lists.newArrayList();
		for (DataPart entry : partMap.values())
			nbts.add(entry.writeDataToNBT(new NBTTagCompound()));
		return NBTHelper.setList(new NBTTagCompound(), "nbts", nbts);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		clearWorld();
		List<NBTTagCompound> nbts = NBTHelper.getList(nbt, "nbts", NBTTagCompound.class);
		for (NBTTagCompound n : nbts) {
			createPart(n);
		}
	}

	public void createPart(NBTTagCompound n) {
		try {
			Class<?> clazz = DataPartRegistry.PARTS.get(n.getString("id"));
			if (clazz != null && DataPart.class.isAssignableFrom(clazz)) {
				DataPart part = ((Class<? extends DataPart>) clazz).newInstance();
				if (part != null) {
					part.setWorld(world);
					part.readDataFromNBT(n);
					addDataPart(part.pos, part, true);
					return;
				}
			}
		} catch (ReflectiveOperationException e) {
		}
		LimeLib.log.error("Failed to create datapart " + n.getString("id"));
	}

}