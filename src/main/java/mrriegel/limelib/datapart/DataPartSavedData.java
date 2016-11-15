package mrriegel.limelib.datapart;

import java.util.List;
import java.util.Map;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Deprecated
public class DataPartSavedData extends WorldSavedData {

	private Map<BlockPos, DataPart> dataParts = Maps.newHashMap();

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
		MapStorage storage = world.getPerWorldStorage();
		DataPartSavedData instance = (DataPartSavedData) storage.getOrLoadData(DataPartSavedData.class, name);
		if (instance == null) {
			instance = new DataPartSavedData(name);
			storage.setData(name, instance);
		}
		return instance;
	}

	public DataPart getDataPart(BlockPos pos) {
		return dataParts.get(pos);
	}

	public boolean addDataPart(World world, BlockPos pos, DataPart part, boolean force) {
		part.world = world;
		part.pos = pos;
		if (dataParts.get(part.pos) != null) {
			if (force) {
				dataParts.put(part.pos, part);
				markDirty();
				return true;
			}
			return false;
		} else {
			dataParts.put(part.pos, part);
			markDirty();
			return true;
		}
	}

	public void removeDataPart(BlockPos pos) {
		dataParts.remove(pos);
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		dataParts.clear();
		for (NBTTagCompound n : NBTHelper.getTagList(nbt, "nbts")) {
			try {
				DataPart part = (DataPart) ConstructorUtils.invokeConstructor(Class.forName(n.getString("class")));
				part.readFromNBT(n);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		List<NBTTagCompound> nbts = Lists.newArrayList();
		for (DataPart part : dataParts.values()) {
			NBTTagCompound n = new NBTTagCompound();
			part.writeToNBT(n);
			nbts.add(n);
		}
		NBTHelper.setTagList(nbt, "nbts", nbts);
		return nbt;
	}

	@SubscribeEvent
	public static void join(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP) {
			for (DataPart part : get(event.getWorld()).dataParts.values()) {
				if (part != null)
					part.sync((EntityPlayerMP) event.getEntity());
			}
		}
	}

	@SubscribeEvent
	public static void tickServer(WorldTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER) {
			for (DataPart part : get(event.world).dataParts.values()) {
				if (part != null) {
					if (part.isDirty) {
						for (EntityPlayerMP player : event.world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(part.pos.add(-20, -20, -20), part.pos.add(20, 20, 20))))
							part.sync(player);
						part.isDirty = false;
					}
					part.onUpdate();
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
