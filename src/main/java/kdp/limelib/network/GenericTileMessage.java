package kdp.limelib.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import kdp.limelib.LimeLib;
import kdp.limelib.tile.GenericTile;

public class GenericTileMessage extends AbstractMessage {

    public GenericTileMessage() {
    }

    public GenericTileMessage(CompoundNBT nbt) {
        super(nbt);
    }

    @Override
    public void handleMessage(PlayerEntity player) {
        TileEntity tile = player.world.getTileEntity(BlockPos.fromLong(nbt.getLong(GenericTile.POS_KEY)));
        nbt.remove(GenericTile.POS_KEY);
        if (tile instanceof GenericTile) {
            ((GenericTile) tile).handleMessage(player, nbt);
            tile.markDirty();
        } else {
            LimeLib.LOG.warn("Tile entity on server is missing at " + BlockPos.fromLong(nbt.getLong("pos")));
        }
    }

}
