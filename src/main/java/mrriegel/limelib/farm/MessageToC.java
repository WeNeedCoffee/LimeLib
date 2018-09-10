package mrriegel.limelib.farm;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageToC implements IMessage, IMessageHandler<MessageToC, IMessage> {

	NBTTagCompound nbt;

	public MessageToC() {
	}

	public MessageToC(TileFarm tile) {

	}

	@Override
	public IMessage onMessage(MessageToC message, MessageContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

}
