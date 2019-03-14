package kdp.limelib.util;

import java.util.ConcurrentModificationException;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

public class EventHandler {

	@SubscribeEvent
	public static void tick(WorldTickEvent event) {
		if (event.phase == Phase.END && event.side == LogicalSide.SERVER) {

			try {
				if (event.world.getWorldInfo().getGameTime() % 4 == 0) {

				}
			} catch (ConcurrentModificationException e) {
			}
		}
	}

	@SubscribeEvent
	public void serverAboutToStart(FMLServerAboutToStartEvent event) {
		ServerData.start(event.getServer());
	}

	@SubscribeEvent
	public void serverStopping(FMLServerStoppingEvent event) {
		ServerData.stop();
	}
	
}
