package mrriegel.limelib.network;

import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

@Deprecated
public class DataPartSyncMessage extends AbstractMessage<DataPartSyncMessage> {

	public DataPartSyncMessage() {
	}

	public DataPartSyncMessage(DataPart part) {
		part.writeToNBT(nbt);
		nbt.setLong("DATApos", part.getPos().toLong());
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		DataPartSavedData data = DataPartSavedData.get(player.worldObj);
		DataPart part = data.getDataPart(BlockPos.fromLong(nbt.getLong("DATApos")));
		if (part != null) {
			part.readFromNBT(nbt);
		}
	}

}
