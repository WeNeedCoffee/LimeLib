package mrriegel.limelib;

import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class LimeClientProxy extends LimeCommonProxy {

	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public IThreadListener getListener(MessageContext ctx) {
		return Minecraft.getMinecraft();
	}

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public RayTraceResult getClientRayTrace() {
		return Minecraft.getMinecraft().objectMouseOver;
	}

	@Override
	public void renderParticle(CommonParticle par) {
		Minecraft.getMinecraft().effectRenderer.addEffect(par);
	}

}
