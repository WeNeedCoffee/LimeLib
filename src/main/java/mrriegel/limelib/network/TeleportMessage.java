package mrriegel.limelib.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class TeleportMessage extends AbstractMessage {

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		Minecraft.getMinecraft().ingameGUI.resetPlayersOverlayFooterHeader();
	}

}
