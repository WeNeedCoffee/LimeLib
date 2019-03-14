package kdp.limelib.network;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class OpenGuiMessage extends AbstractMessage {

	public OpenGuiMessage() {
	}

	public OpenGuiMessage(String modID, int guiID, @Nullable BlockPos pos) {
		if (pos != null)
			nbt.setLong("pos", pos.toLong());
		nbt.setString("modid", modID);
		nbt.setInt("guiid", guiID);
	}

	@Override
	public void handleMessage(EntityPlayer player) {
		BlockPos p = nbt.hasKey("pos") ? BlockPos.fromLong(nbt.getLong("pos")) : BlockPos.ORIGIN;

	}

}
