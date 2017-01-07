package mrriegel.limelib.util;

import java.util.ConcurrentModificationException;

import mrriegel.limelib.Config;
import mrriegel.limelib.network.EnergySyncMessage;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.tile.IOwneable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Iterables;

public class Eventhandler {

	@SubscribeEvent
	public static void left(LeftClickBlock event) {
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
	public static void right(RightClickBlock event) {
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
	public static void tick(WorldTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER) {
			try {
				if (event.world.getTotalWorldTime() % 4 == 0) {
					for (TileEntity tile : Iterables.filter(event.world.loadedTileEntityList, t -> t instanceof CommonTile)) {
						if (tile instanceof CommonTile) {
							if (((CommonTile) tile).needsSync()) {
								((CommonTile) tile).sync();
								((CommonTile) tile).setSyncDirty(false);
							}
						}
					}
				}
			} catch (ConcurrentModificationException e) {
			}
		}
	}

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER) {
			if (Config.showEnergy && event.player.worldObj.getTotalWorldTime() % 20 == 0) {
				PacketHandler.sendTo(new EnergySyncMessage((EntityPlayerMP) event.player), (EntityPlayerMP) event.player);
			}
		}
	}

}
