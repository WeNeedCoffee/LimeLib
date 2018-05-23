package mrriegel.limelib.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import mrriegel.limelib.LimeLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractMessage implements IMessage, IMessageHandler<AbstractMessage, IMessage>, Packet<INetHandler> {

	protected NBTTagCompound nbt = new NBTTagCompound();
	public boolean shouldSend = true;

	public AbstractMessage() {
	}

	public AbstractMessage(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	//TODO remove side
	public abstract void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side);

	@Override
	public IMessage onMessage(final AbstractMessage message, final MessageContext ctx) {
		//FMLCommonHandler.instance().getWorldThread(ctx.netHandler); //TODO
		Runnable run = () -> {
			EntityPlayer player = (ctx.side.isClient() ? LimeLib.proxy.getClientPlayer() : ctx.getServerHandler().player);
			message.handleMessage(player, message.nbt.copy(), ctx.side);
		};
		IThreadListener listener = (ctx.side.isClient() ? LimeLib.proxy.getClientListener() : ctx.getServerHandler().player.getServerWorld());
		listener.addScheduledTask(run);
		return null;
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		fromBytes(buf);
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		toBytes(buf);
	}

	@Override
	public void processPacket(INetHandler handler) {
		IThreadListener itl = FMLCommonHandler.instance().getWorldThread(handler);
		itl.addScheduledTask(() -> {
			EntityPlayer player = handler instanceof NetHandlerPlayServer ? ((NetHandlerPlayServer) handler).player : FMLClientHandler.instance().getClientPlayerEntity();
			handleMessage(player, nbt, player.world.isRemote ? Side.CLIENT : Side.SERVER);
		});
	}

}
