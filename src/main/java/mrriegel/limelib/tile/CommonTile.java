package mrriegel.limelib.tile;

import java.util.List;

import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.TileGuiMessage;
import mrriegel.limelib.network.TileMessage;
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

import com.google.common.collect.Lists;

public class CommonTile extends TileEntity {

	private boolean syncDirty;

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
		return new SPacketUpdateTileEntity(this.pos, 1, serializeNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public boolean needsSync() {
		return syncDirty;
	}

	public void markForSync() {
		if (onServer())
			syncDirty = true;
	}

	public void setSyncDirty(boolean syncDirty) {
		this.syncDirty = syncDirty;
	}

	public void sync() {
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 8);
	}

	public boolean isUsable(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this || isInvalid() ? false : player.getDistanceSq(getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D) <= 64.0D;
	}

	public List<ItemStack> getDroppingItems() {
		return Lists.newArrayList();
	}

	public boolean openGUI(EntityPlayerMP player) {
		return false;
	}

	public final void sendOpenGUI() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("pos", pos.toLong());
		PacketHandler.sendToServer(new TileGuiMessage(nbt));
	}

	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
	}

	public final void sendMessage(NBTTagCompound nbt) {
		nbt.setLong("pos", pos.toLong());
		PacketHandler.sendToServer(new TileMessage(nbt));
	}

	public final int getX() {
		return pos.getX();
	}

	public final int getY() {
		return pos.getY();
	}

	public final int getZ() {
		return pos.getZ();
	}

	public IBlockState getBlockState() {
		return world.getBlockState(pos);
	}

	public boolean onServer() {
		return !world.isRemote;
	}

	public boolean onClient() {
		return !onServer();
	}

}
