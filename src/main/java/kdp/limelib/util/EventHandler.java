package kdp.limelib.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import kdp.limelib.tile.GenericTile;

public class EventHandler {

    @SubscribeEvent
    public static void tick(WorldTickEvent event) {
        if (event.phase == Phase.END && event.side == LogicalSide.SERVER) {

            try {
                if (event.world.getWorldInfo().getGameTime() % 4 == 0) {
                    Iterator<GenericTile> it = GenericTile.toSync.iterator();
                    while (it.hasNext()) {
                        GenericTile next = it.next();
                    }
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
