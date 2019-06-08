package kdp.limelib.network;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class OpenGuiMessage extends AbstractMessage {

    public OpenGuiMessage() {
    }

    public OpenGuiMessage(String modID, int guiID, @Nullable BlockPos pos) {
        if (pos != null)
            nbt.putLong("pos", pos.toLong());
        nbt.putString("modid", modID);
        nbt.putInt("guiid", guiID);
    }

    @Override
    public void handleMessage(PlayerEntity player) {
        BlockPos p = nbt.contains("pos") ? BlockPos.fromLong(nbt.getLong("pos")) : BlockPos.ZERO;

    }

}
