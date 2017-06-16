package mrriegel.testmod;

import io.netty.buffer.Unpooled;
import mrriegel.limelib.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class TestMessage extends AbstractMessage {

	public TestMessage() {
		super();
	}

	public TestMessage(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		System.out.println(nbt);
		PacketBuffer one = new PacketBuffer(Unpooled.buffer());
		ByteBufUtils.writeTag(one, nbt);
		System.out.println(ByteBufUtils.readTag(one));
		// player.addChatComponentMessage(new
		// TextComponentString(nbt.getString("l") + " Side: " + side));
		// for(NBTBase n:NBTHelper.getObjects(nbt))
		// System.out.println(NBTHelper.getObjectFrom(n));
	}

}
