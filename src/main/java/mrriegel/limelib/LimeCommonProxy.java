package mrriegel.limelib;

import java.util.Map;

import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

public class LimeCommonProxy {

	public EntityPlayer getPlayer(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	public IThreadListener getListener(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.getServerWorld();
	}

	public Side getSide() {
		return Side.SERVER;
	}

	public double getReachDistance(EntityPlayer player) {
		return ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
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

	public void renderParticle(CommonParticle par) {
	}

	public Map<BlockPos, Pair<Long, Long>> energyTiles() {
		return ImmutableMap.of();
	}

	public boolean isKeyDown(int key) {
		throw new UnsupportedOperationException();
	}

}
