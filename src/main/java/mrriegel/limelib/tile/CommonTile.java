package mrriegel.limelib.tile;

import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.TileGuiMessage;
import mrriegel.limelib.network.TileMessage;
import mrriegel.limelib.network.TileSyncMessage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonTile extends TileEntity {

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public void sync(EntityPlayerMP player) {
		markDirty();
		if (hasWorldObj() && !worldObj.isRemote && player.getPosition().getDistance(pos.getX(), pos.getY(), pos.getZ()) < 32)
			try {
				player.connection.sendPacket(getUpdatePacket());
			} catch (Error e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
	}

	public void sync() {
		if (hasWorldObj() && !worldObj.isRemote)
			for (EntityPlayer p : worldObj.playerEntities) {
				sync((EntityPlayerMP) p);
			}
	}

	public void syncSafe(EntityPlayerMP player) {
		if (worldObj.isRemote)
			return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("pos", pos.toLong());
		PacketHandler.sendTo(new TileSyncMessage(nbt), player);
	}

	public void syncSafe() {
		if (worldObj.isRemote)
			return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("pos", pos.toLong());
		PacketHandler.sendToDimension(new TileSyncMessage(nbt), worldObj.provider.getDimension());
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public ItemStack[] getDroppingItems() {
		return new ItemStack[0];
	}

	public boolean openGUI(EntityPlayerMP player) {
		return false;
	}

	public final void sendOpenGUI() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("pos", pos.toLong());
		PacketHandler.sendToServer(new TileGuiMessage(nbt));
	}

	public void handleMessage(EntityPlayerMP player, NBTTagCompound nbt) {
	}

	public final void sendMessage(NBTTagCompound nbt) {
		nbt.setLong("pos", pos.toLong());
		PacketHandler.sendToServer(new TileMessage(nbt));
	}

}
