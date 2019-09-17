package kdp.limelib.tile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import kdp.limelib.network.GenericTileMessage;
import kdp.limelib.network.PacketHandler;

public abstract class GenericTile extends TileEntity {

    public static final String POS_KEY = "#²»đ";
    public static Set<GenericTile> toSync = Collections.newSetFromMap(new WeakHashMap<>());

    private boolean needsSync;
    private Object2BooleanOpenHashMap<UUID> syncMap = new Object2BooleanOpenHashMap<>();

    public GenericTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return serializeNBT();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT n = writeToSyncNBT(new CompoundNBT());
        if (n == null) {
            return null;
        }
        return new SUpdateTileEntityPacket(pos, 1337, n);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readFromSyncNBT(pkt.getNbtCompound());
    }

    public abstract void readFromSyncNBT(CompoundNBT compound);

    public abstract CompoundNBT writeToSyncNBT(CompoundNBT compound);

    public boolean needsSync() {
        return needsSync;
    }

    public void markForSync() {
        this.needsSync = true;
    }

    public void unmarkForSync() {
        this.needsSync = false;
    }

    public void sync() {
        markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0);
        /*if (!world.isRemote) {
            for (ServerPlayerEntity player : world.getEntitiesWithinAABB(ServerPlayerEntity.class,
                    new AxisAlignedBB(pos.add(-11, -11, -11), pos.add(11, 11, 11)))) {
                SUpdateTileEntityPacket p = getUpdatePacket();
                if (p != null)
                    player.connection.sendPacket(p);
            }
        }*/
    }

    @Nullable
    public List<ItemStack> getDroppingItems() {
        return null;
    }

    public void writeToStack(ItemStack stack) {
    }

    public void readFromStack(ItemStack stack) {
    }

    public boolean interact(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return false;
    }

    public void handleMessage(PlayerEntity player, CompoundNBT nbt) {
    }

    public final void sendMessage(CompoundNBT nbt) {
        nbt.putLong(POS_KEY, pos.toLong());
        PacketHandler.sendToServer(new GenericTileMessage(nbt));
    }

    public void neighborChanged(BlockPos fromPos, boolean isMoving) {
    }
}
