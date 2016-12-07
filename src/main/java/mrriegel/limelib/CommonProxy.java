package mrriegel.limelib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

	public EntityPlayer getPlayer(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	public IThreadListener getListener(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.getServerWorld();
	}

	public World getClientWorld() {
		throw new IllegalStateException();
	}

	public EntityPlayer getClientPlayer() {
		throw new IllegalStateException();
	}

	public RayTraceResult getClientRayTrace() {
		throw new IllegalStateException();
	}
}
