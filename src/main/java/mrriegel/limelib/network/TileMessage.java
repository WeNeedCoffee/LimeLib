package mrriegel.limelib.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import mrriegel.limelib.tile.CommonTile;

public class TileMessage<T extends CommonTile> extends AbstractMessage<TileMessage<T>> {
	
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
			((CommonTile) tile).handleMessage(nbt);
	}

}
