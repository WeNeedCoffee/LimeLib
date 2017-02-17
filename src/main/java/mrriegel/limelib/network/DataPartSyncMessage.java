package mrriegel.limelib.network;

import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class DataPartSyncMessage extends AbstractMessage<DataPartSyncMessage> {

	public DataPartSyncMessage() {
	}

	public DataPartSyncMessage(DataPart part, BlockPos pos) {
		if (part == null) {
			nbt.setBoolean("removed", true);
			nbt.setLong("poS", pos.toLong());
		} else
			part.writeDataToNBT(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		BlockPos pos = BlockPos.fromLong(NBTHelper.getLong(nbt, "poS"));
		DataPartRegistry reg = DataPartRegistry.get(player.world);
		if (reg != null) {
			if (nbt.getBoolean("removed"))
				reg.removeDataPart(pos);
			else {
				DataPart p = reg.getDataPart(pos);
				if (p != null) {
					p.readDataFromNBT(nbt);
					p.setWorld(player.world);
				} else {
					reg.createPart(nbt);
					p = reg.getDataPart(pos);
					if (p != null)
						p.setWorld(player.world);
				}
			}
		}
	}

}
