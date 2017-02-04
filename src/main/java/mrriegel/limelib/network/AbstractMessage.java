package mrriegel.limelib.network;

import io.netty.buffer.ByteBuf;
import mrriegel.limelib.LimeLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractMessage<T extends AbstractMessage<T>> implements IMessage, IMessageHandler<T, IMessage> {

	protected NBTTagCompound nbt = new NBTTagCompound();

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

	public abstract void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side);

	@Override
	public IMessage onMessage(final T message, final MessageContext ctx) {
		Runnable run = () -> {
			EntityPlayer player = (ctx.side.isClient() ? LimeLib.proxy.getPlayer(ctx) : ctx.getServerHandler().playerEntity);
			message.handleMessage(player, message.nbt, ctx.side);
		};
		IThreadListener listener = (ctx.side.isClient() ? LimeLib.proxy.getListener(ctx) : ctx.getServerHandler().playerEntity.getServerWorld());
		listener.addScheduledTask(run);
		return null;
	}

}
