package mrriegel.limelib.datapart;

import java.util.List;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Deprecated
public class DataPartSavedData extends WorldSavedData {

	private static final String DATA_NAME = "DataPart";

	public DataPartSavedData() {
		this(DATA_NAME);
	}

	public DataPartSavedData(String name) {
		super(name);
	}

	public static DataPartSavedData get(World world) {
		return get(world, DATA_NAME);
	}

	public static DataPartSavedData get(World world, String name) {
		if (world == null)
			return null;
		MapStorage storage = world.getPerWorldStorage();
		DataPartSavedData instance = (DataPartSavedData) storage.getOrLoadData(DataPartSavedData.class, name);
		if (instance == null) {
			instance = new DataPartSavedData(name);
			storage.setData(name, instance);
		}
		return instance;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (DataPart.partMap == null)
			DataPart.partMap = Maps.newHashMap();
		else
			DataPart.partMap.clear();
		System.out.println("zoop");
		for (NBTTagCompound n : NBTHelper.getTagList(nbt, "nbts")) {
			try {
				System.out.println(n.getString("class"));
				Class<?> clazz = Class.forName(n.getString("class"));
				System.out.println(clazz);
				if (clazz != null && clazz.isAssignableFrom(DataPart.class)) {
					DataPart part = (DataPart) clazz.newInstance();
					System.out.println(part);
					if (part != null) {
						part.readFromNBT(n);
						if (DimensionManager.isDimensionRegistered(part.pos.getDimension()))
							DataPart.partMap.put(part.pos, part);
						else
							LimeLib.log.error("Dimension " + part.pos.getDimension() + " is not registered.");
					}
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		List<NBTTagCompound> nbts = Lists.newArrayList();
		if (DataPart.partMap == null)
			DataPart.partMap = Maps.newHashMap();
		for (DataPart part : DataPart.partMap.values()) {
			NBTTagCompound n = new NBTTagCompound();
			part.writeToNBT(n);
			nbts.add(n);
		}
		NBTHelper.setTagList(nbt, "nbts", nbts);
		return nbt;
	}

	@SubscribeEvent
	public static void tickServer(WorldTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER) {
			if (event.world.getTotalWorldTime() % 30 == 0) {
				//				System.out.println(DataPart.partMap);
			}
			if (DataPart.partMap == null)
				DataPart.partMap = Maps.newHashMap();
			for (DataPart part : DataPart.partMap.values()) {
				if (part != null && part.getWorld() == event.world && part.getWorld().isBlockLoaded(part.getPos())) {
					part.update(event.world);
				}
			}
		}
	}

	//	@SubscribeEvent
	//	public static void tickClient(ClientTickEvent event) {
	//		Minecraft mc = Minecraft.getMinecraft();
	//		if (event.phase == Phase.END && mc.theWorld != null) {
	//			for (DataPart part : get(mc.theWorld).dataParts.values()) {
	//				if (part != null) {
	//					part.onUpdate();
	//				}
	//			}
	//		}
	//	}

}
