package mrriegel.limelib;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
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

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public RayTraceResult getClientRayTrace() {
		return Minecraft.getMinecraft().objectMouseOver;
	}

}
