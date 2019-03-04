package kdp.limelib.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class AbstractMessage {
	protected NBTTagCompound nbt = new NBTTagCompound();

	public AbstractMessage() {
	}

	public AbstractMessage(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	public void encode(PacketBuffer buffer) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			CompressedStreamTools.write(nbt, dos);
		} catch (IOException e) {
		}
		buffer.writeByteArray(baos.toByteArray());
	}

	public void decode(PacketBuffer buffer) {
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer.readByteArray());
		DataInputStream dis = new DataInputStream(bais);
		try {
			nbt = CompressedStreamTools.read(dis);
		} catch (IOException e) {
		}
	}

	public final void handleMessage(AbstractMessage message, Context context) {
		nbt = message.nbt;
		//Thread.dumpStack();
		context.enqueueWork(() -> {
			System.out.println(
					context.getDirection().getReceptionSide() + " " + context.getDirection().getOriginationSide());
			EntityPlayer player = context.getDirection().getReceptionSide() == LogicalSide.SERVER ? context.getSender()
					: getClientPlayer().get().get();
			handleMessage(player);
			context.setPacketHandled(true);
		});
	}

	public abstract void handleMessage(EntityPlayer player);

	private static Supplier<Supplier<EntityPlayer>> getClientPlayer() {
		return () -> () -> Minecraft.getInstance().player;
	}
}
