package mrriegel.limelib.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.stream.Collectors;

import mrriegel.limelib.Config;
import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.network.EnergySyncMessage;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.PlayerClickMessage;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.tile.IOwneable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandler {

	@SubscribeEvent
	public static void left(LeftClickBlock event) {
		if (event.getEntityPlayer() != null) {
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			if (tile instanceof IOwneable) {
				IOwneable o = (IOwneable) tile;
				if (!o.canAccess(event.getEntityPlayer().getDisplayNameString())) {
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
				if (!o.canAccess(event.getEntityPlayer().getDisplayNameString())) {
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
					Iterator<TileEntity> it = event.world.loadedTileEntityList.stream().filter(t -> t instanceof CommonTile && !t.isInvalid()).collect(Collectors.toList()).iterator();
					while (it.hasNext()) {
						CommonTile tile = (CommonTile) it.next();
						if (tile.needsSync()) {
							tile.sync();
							tile.setSyncDirty(false);
						}
					}
				}
			} catch (ConcurrentModificationException e) {
			}
			DataPartRegistry reg = DataPartRegistry.get(event.world);
			if (reg != null) {
				Iterator<DataPart> it = reg.getParts().stream().filter(p -> p != null && event.world.isBlockLoaded(p.getPos())).collect(Collectors.toList()).iterator();
				while (it.hasNext()) {
					DataPart part = it.next();
					part.updateServer(event.world);
					part.ticksExisted++;
					if (event.world.getTotalWorldTime() % 200 == 0)
						reg.sync(part.getPos());
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER) {
			if (Config.showEnergy && event.player.world.getTotalWorldTime() % 20 == 0) {
				PacketHandler.sendTo(new EnergySyncMessage((EntityPlayerMP) event.player), (EntityPlayerMP) event.player);
			}
			//			for (IInventory inv : event.player.openContainer.inventorySlots.stream().map(s -> s.inventory).distinct().collect(Collectors.toList())) {
			//				Predicate<ItemStack> pred = s -> s.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE) || s.getItem() == Item.getItemFromBlock(Blocks.DIRT) || s.getItem() == Item.getItemFromBlock(Blocks.STONE);
			//				InvHelper.extractItem(new InvWrapper(inv), pred, 32, false);
			//			}
		}
	}

	@SubscribeEvent
	public static void attachWorld(AttachCapabilitiesEvent<World> event) {
		event.addCapability(DataPartRegistry.LOCATION, new CapabilityDataPart.CapaProvider(event.getObject()));
	}

	@SubscribeEvent
	public static void login(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote)
			((IThreadListener) event.player.world).addScheduledTask(() -> {
				DataPartRegistry reg = DataPartRegistry.get(event.player.world);
				if (reg != null)
					reg.getParts().forEach(p -> reg.sync(p.getPos()));
			});
	}

	private static long lastClick = System.currentTimeMillis();

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void interact(PlayerInteractEvent event) {
		boolean left = event instanceof LeftClickBlock || event instanceof LeftClickEmpty;
		boolean right = event instanceof RightClickBlock || event instanceof RightClickEmpty || event instanceof RightClickItem;
		if ((left || right) && !event.isCanceled()) {
			DataPart part = DataPart.rayTrace(event.getEntityPlayer());
			if (part != null && !event.getEntityPlayer().isSneaking()) {
				if (event.getWorld().isRemote) {
					if (System.currentTimeMillis() - lastClick > (left ? 150 : 40)) {
						PacketHandler.sendToServer(new PlayerClickMessage(part.getPos(), event.getHand(), left));
						if (left)
							part.onLeftClicked(event.getEntityPlayer(), event.getHand());
						else {
							part.onRightClicked(event.getEntityPlayer(), event.getHand());
							event.getEntityPlayer().swingArm(event.getHand());
						}
						lastClick = System.currentTimeMillis();
					}
				}
				if (event.isCancelable())
					event.setCanceled(true);
				event.setResult(Result.DENY);
				if (event instanceof LeftClickBlock) {
					((LeftClickBlock) event).setUseBlock(Result.DENY);
					((LeftClickBlock) event).setUseItem(Result.DENY);
				} else if (event instanceof RightClickBlock) {
					((RightClickBlock) event).setUseBlock(Result.DENY);
					((RightClickBlock) event).setUseItem(Result.DENY);
				}
			}
		}
	}
}
