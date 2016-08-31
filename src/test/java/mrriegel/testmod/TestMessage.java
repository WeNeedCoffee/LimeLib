package mrriegel.testmod;

import mrriegel.limelib.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;

public class TestMessage extends AbstractMessage<TestMessage> {

	public TestMessage() {
		super();
	}

	public TestMessage(NBTTagCompound nbt) {
		super(nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		player.addChatComponentMessage(new TextComponentString(nbt.getString("l") + " Side: " + side));
	}

}
