package mrriegel.limelib.network;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.tile.CommonTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class TileSyncMessage extends AbstractMessage<TileSyncMessage> {
	public TileSyncMessage() {
	}

	public TileSyncMessage(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			if (player.worldObj.getTileEntity(BlockPos.fromLong(NBTHelper.getLong(nbt, "pos"))) instanceof CommonTile) {
				PacketHandler.sendToServer(new TileSyncMessage(nbt));
			}
		} else {
			if (player.worldObj.getTileEntity(BlockPos.fromLong(NBTHelper.getLong(nbt, "pos"))) instanceof CommonTile) {
				((CommonTile) player.worldObj.getTileEntity(BlockPos.fromLong(NBTHelper.getLong(nbt, "pos")))).sync();
			}
		}
	}

}
