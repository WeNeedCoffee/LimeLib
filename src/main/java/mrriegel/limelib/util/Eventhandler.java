package mrriegel.limelib.util;

import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.tile.IOwneable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
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
			}
		}
	}

	@SubscribeEvent
	public void clone(Clone event) {
		EntityPlayer old = event.getOriginal();
		EntityPlayer neu = event.getEntityPlayer();
		neu.getEntityData().merge(old.getEntityData());
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(ParticleHelper.roundParticle);
		event.getMap().registerSprite(ParticleHelper.sparkleParticle);
		event.getMap().registerSprite(ParticleHelper.squareParticle);
	}

}
