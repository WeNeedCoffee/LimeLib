package mrriegel.limelib.network;

import mrriegel.limelib.tile.CommonTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class TileMessage extends AbstractMessage<TileMessage> {

	public TileMessage() {
		super();
	}

	public TileMessage(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		TileEntity tile = player.worldObj.getTileEntity(BlockPos.fromLong(nbt.getLong("pos")));
		if (tile instanceof CommonTile)
			((CommonTile) tile).handleMessage((EntityPlayerMP) player, nbt);
	}

}
