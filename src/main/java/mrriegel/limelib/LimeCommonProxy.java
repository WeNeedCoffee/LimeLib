package mrriegel.limelib;

import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class LimeCommonProxy {

	public Side getSide() {
		return Side.SERVER;
	}

	public double getReachDistance(EntityPlayer player) {
		return ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
	}

	public World getClientWorld() {
		throw new UnsupportedOperationException();
	}

	public EntityPlayer getClientPlayer() {
		throw new UnsupportedOperationException();
	}

	public RayTraceResult getClientRayTrace() {
		throw new UnsupportedOperationException();
	}

	public IThreadListener getClientListener() {
		throw new UnsupportedOperationException();
	}

	public void renderParticle(CommonParticle par) {
	}

}
