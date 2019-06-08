package kdp.limelib;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientHelper {
    public static EntityPlayer getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
