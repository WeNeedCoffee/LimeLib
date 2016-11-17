package mrriegel.limelib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

	public EntityPlayer getPlayer(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	public IThreadListener getListener(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.getServerWorld();
	}
}
