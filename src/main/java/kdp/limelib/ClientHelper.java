package kdp.limelib;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class ClientHelper {
    public static PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
