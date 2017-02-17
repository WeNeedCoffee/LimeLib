package mrriegel.limelib.network;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;

public class DataPartSyncMessage extends AbstractMessage<DataPartSyncMessage> {

	public DataPartSyncMessage() {
	}

	public DataPartSyncMessage(DataPart part) {
		part.writeDataToNBT(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		GlobalBlockPos pos = GlobalBlockPos.loadGlobalPosFromNBT(NBTHelper.getTag(nbt, "gpos"));
		String name = NBTHelper.getString(nbt, "naMe");
		DataPartRegistry reg = DataPartRegistry.get(player.world);
		if (reg != null) {
			DataPart p = reg.getDataPart(pos, name);
			if (p != null) {
				p.readDataFromNBT(nbt);
			} else {
				try {
					Class<?> clazz = Class.forName(nbt.getString("class"));
					if (clazz != null && DataPart.class.isAssignableFrom(clazz)) {
						DataPart part = (DataPart) ConstructorUtils.invokeConstructor(clazz);
						if (part != null) {
							part.readDataFromNBT(nbt);
							reg.partMap.put(part.getGlobalPos(), part);
						}
					}
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
