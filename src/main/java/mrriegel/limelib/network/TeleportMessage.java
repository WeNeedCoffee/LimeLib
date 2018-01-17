package mrriegel.limelib.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;

public class TeleportMessage extends AbstractMessage {

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		FMLClientHandler.instance().getClient().ingameGUI.resetPlayersOverlayFooterHeader();
	}

}
