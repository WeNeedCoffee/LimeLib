package mrriegel.limelib;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public IThreadListener getListener(MessageContext ctx) {
		return Minecraft.getMinecraft();
	}

}
