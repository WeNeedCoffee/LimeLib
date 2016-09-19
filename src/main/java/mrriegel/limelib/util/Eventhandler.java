package mrriegel.limelib.util;

import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.tile.IOwneable;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Eventhandler {

	@SubscribeEvent
	public void tick(LeftClickBlock event) {
		if (event.getEntityPlayer() != null) {
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			if (tile instanceof IOwneable) {
				IOwneable o = (IOwneable) tile;
				if (!o.canAccess(event.getEntityPlayer().getName())) {
					// if (!event.getWorld().isRemote)
					// event.getEntityPlayer().addChatComponentMessage(new
					// TextComponentString("No permission!"));
					event.setCanceled(true);
					event.setResult(Result.DENY);
					event.setUseBlock(Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent
	public void tick(RightClickBlock event) {
		if (event.getEntityPlayer() != null) {
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			if (tile instanceof IOwneable) {
				IOwneable o = (IOwneable) tile;
				if (!o.canAccess(event.getEntityPlayer().getName())) {
					// if (!event.getWorld().isRemote)
					// event.getEntityPlayer().addChatComponentMessage(new
					// TextComponentString("No permission!"));
					// event.setCanceled(true);
					event.setResult(Result.DENY);
					event.setUseBlock(Result.DENY);
				}
			} else {
				BlockPos pos = event.getPos().up();
				for (Vec3d vec : ParticleHelper.getVecsForExplosion(pos, .54, 20, Axis.X)) {
					Minecraft.getMinecraft().effectRenderer.addEffect(new CommonParticle(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, vec.xCoord, vec.yCoord, vec.zCoord).setMaxAge2(40).setFlouncing(.009).setTexture(ParticleHelper.squareParticle).setNoClip(true));
				}
			}
		}
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(ParticleHelper.roundParticle);
		event.getMap().registerSprite(ParticleHelper.sparkleParticle);
		event.getMap().registerSprite(ParticleHelper.squareParticle);
	}

}
