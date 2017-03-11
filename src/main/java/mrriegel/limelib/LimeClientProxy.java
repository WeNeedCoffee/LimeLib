package mrriegel.limelib;

import java.util.Map;

import mrriegel.limelib.particle.CommonParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

public class LimeClientProxy extends LimeCommonProxy {

	public Map<BlockPos, Pair<Long, Long>> energyTiles = Maps.newHashMap();

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
	public double getReachDistance(EntityPlayer player) {
		return Minecraft.getMinecraft().playerController.getBlockReachDistance();
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

	@Override
	public Map<BlockPos, Pair<Long, Long>> energyTiles() {
		return energyTiles;
	}

}
