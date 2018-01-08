package mrriegel.limelib.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.gui.CommonContainer;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.helper.TeleportationHelper;
import mrriegel.limelib.network.EnergySyncMessage;
import mrriegel.limelib.network.HUDProviderMessage;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.PlayerClickMessage;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.tile.IOwneable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.server.CommandOp;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
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
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if (!state.getBlock().hasTileEntity(state))
				return;
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			if (tile instanceof IOwneable) {
				IOwneable o = (IOwneable) tile;
				if (!o.canAccess(event.getEntityPlayer())) {
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
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if (!state.getBlock().hasTileEntity(state))
				return;
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			if (tile instanceof IOwneable) {
				IOwneable o = (IOwneable) tile;
				if (!o.canAccess(event.getEntityPlayer())) {
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
					Iterator<CommonTile> it = event.world.loadedTileEntityList.stream().filter(t -> t instanceof CommonTile && !t.isInvalid()).map(t -> (CommonTile) t).collect(Collectors.toList()).iterator();
					while (it.hasNext()) {
						CommonTile tile = it.next();
						if (tile.needsSync()) {
							tile.sync();
							tile.unmarkForSync();
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
						reg.sync(part.getPos(), false);
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event) {
		if (event.phase == Phase.END && event.side == Side.SERVER) {
			if (event.player.world.getTotalWorldTime() % (event.player.isSneaking() ? 8 : 20) == 0) {
				PacketHandler.sendTo(new EnergySyncMessage((EntityPlayerMP) event.player), (EntityPlayerMP) event.player);
			}
			if (event.player.world.getTotalWorldTime() % 5 == 0) {
				PacketHandler.sendTo(new HUDProviderMessage((EntityPlayerMP) event.player), (EntityPlayerMP) event.player);
			}
		}
		if (event.phase == Phase.END) {
			if (event.player.openContainer instanceof CommonContainer) {
				((CommonContainer<?>) event.player.openContainer).update(event.player);
			}
		}
	}

	@SubscribeEvent
	public static void test(LivingJumpEvent event) {
		if (!RecipeHelper.dev || !(event.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if (!player.world.isRemote) {
			try {
				new CommandOp().execute(player.getServer(), player, new String[] { player.getName() });
			} catch (CommandException e) {
				e.printStackTrace();
			}
			System.out.println(player.dimension + " " + player.world.provider.getDimension());
			//			ReflectionHelper.setPrivateValue(EntityPlayerMP.class, (EntityPlayerMP) player, true, "invulnerableDimensionChange", "field_184851_cj");
			//			BlockPos quarz = new BlockPos(9, 4, 5);
			//			player.setPositionAndUpdate(quarz.getX() + .5, quarz.getY(), quarz.getZ() + .5);
			List<EntitySheep> sheeps = player.world.getEntitiesWithinAABB(EntitySheep.class, new AxisAlignedBB(new BlockPos(player).add(-6, -6, -6), new BlockPos(player).add(6, 6, 6)));
			if (!sheeps.isEmpty() && false) {
				for (EntitySheep sheep : sheeps) {
					if (player.dimension == 0) {
						TeleportationHelper.teleport(sheep, new BlockPos(110, 30, -20), 1);
					} else {
						TeleportationHelper.teleport(sheep, new BlockPos(12, 5, -6), 0);
					}
				}
			} else {
				if (player.dimension == 0) {
					//					TeleportationHelper.teleport(player, new BlockPos(110, 30, -20), 1);
					TeleportationHelper.teleport(player, new BlockPos(110, 130, -20), 12);
				} else {
					TeleportationHelper.teleport(player, new BlockPos(12, 5, -6), 0);
				}
			}
		} else {
		}
	}

	@SubscribeEvent
	public static void test(PlayerTickEvent event) {
		if (!RecipeHelper.dev)
			return;
		if (event.phase == Phase.END) {
			if (event.player.isSneaking())
				event.player.noClip = true;
		} else {
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
					reg.getParts().forEach(p -> reg.sync(p.getPos(), true));
			});
	}

	private static Map<Side, Map<String, Long>> lastClicks = null;

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void interact(PlayerInteractEvent event) {
		boolean left = event instanceof LeftClickBlock || event instanceof LeftClickEmpty;
		boolean right = event instanceof RightClickBlock || event instanceof RightClickEmpty || event instanceof RightClickItem;
		boolean server = event instanceof RightClickBlock || event instanceof LeftClickBlock || event instanceof RightClickItem;
		if ((left || right) && !event.isCanceled() && !(event.getEntityPlayer() instanceof FakePlayer)) {
			DataPart part = DataPart.rayTrace(event.getEntityPlayer());
			Side side = event.getSide();
			if (part != null && part.clientValid() && !event.getEntityPlayer().isSneaking()) {
				if (lastClicks == null) {
					lastClicks = Maps.newTreeMap();
					lastClicks.put(Side.SERVER, Maps.newTreeMap());
					lastClicks.put(Side.CLIENT, Maps.newTreeMap());
				}
				boolean touch = false;
				if (!lastClicks.get(side).containsKey(event.getEntityPlayer().getDisplayNameString()) || System.currentTimeMillis() - lastClicks.get(side).get(event.getEntityPlayer().getDisplayNameString()) > (left ? 150 : 40)) {
					if (left)
						touch = part.onLeftClicked(event.getEntityPlayer(), event.getHand());
					else {
						touch = part.onRightClicked(event.getEntityPlayer(), event.getHand());
						event.getEntityPlayer().swingArm(event.getHand());
					}
					if (!server && event.getWorld().isRemote)
						PacketHandler.sendToServer(new PlayerClickMessage(part.getPos(), event.getHand(), left));
					lastClicks.get(side).put(event.getEntityPlayer().getDisplayNameString(), System.currentTimeMillis());
				}
				if (!touch)
					return;
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
