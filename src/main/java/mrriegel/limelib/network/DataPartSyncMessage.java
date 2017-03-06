package mrriegel.limelib.network;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class DataPartSyncMessage extends AbstractMessage<DataPartSyncMessage> {

	public DataPartSyncMessage() {
	}

	public DataPartSyncMessage(DataPart part, BlockPos pos, List<BlockPos> parts) {
		if (part == null) {
			nbt.setBoolean("removed", true);
			nbt.setLong("poS", pos.toLong());
		} else
			part.writeDataToNBT(nbt);
		NBTHelper.setLongList(nbt, "poss", Utils.getLongList(parts));
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
				}
			}
			List<BlockPos> valids = Utils.getBlockPosList(NBTHelper.getLongList(nbt, "poss"));
			Set<BlockPos> clients = reg.getParts().stream().map(DataPart::getPos).collect(Collectors.toSet());
			clients.removeAll(valids);
			clients.forEach(p -> reg.removeDataPart(p));
		}
	}
}
