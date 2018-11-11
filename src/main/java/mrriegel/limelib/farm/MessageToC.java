package mrriegel.limelib.farm;

import io.netty.buffer.ByteBuf;
import mrriegel.limelib.farm.PseudoEntity.Mover;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageToC implements IMessage, IMessageHandler<MessageToC, IMessage> {

	NBTTagCompound nbt;

	public MessageToC() {
	}

	public MessageToC(TileFarm tile) {
		nbt = new NBTTagCompound();
		nbt.setLong("pos", tile.getPos().toLong());
		nbt.setTag("tag", tile.farmers.iterator().next().mover.serializeNBT());
	}

	@Override
	public IMessage onMessage(MessageToC message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			TileFarm tile = (TileFarm) Minecraft.getMinecraft().world.getTileEntity(BlockPos.fromLong(message.nbt.getLong("pos")));
			tile.farmers.iterator().next().mover = Mover.of(message.nbt.getCompoundTag("tag"));
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

}
