package kdp.limelib.util;

import java.util.Objects;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import org.apache.commons.lang3.Validate;

public class GlobalBlockPos {
    private final BlockPos pos;
    private final int dimension;
    private transient World world;

    public GlobalBlockPos(@Nonnull BlockPos pos, int dimension) {
        this.pos = Objects.requireNonNull(pos);
        this.dimension = dimension;
    }

    public GlobalBlockPos(@Nonnull BlockPos pos, World world) {
        this(pos, world.dimension.getType().getId());
        Validate.isTrue(!world.isRemote);
    }

    @Override
    public String toString() {
        return "GlobalBlockPos [pos=" + pos + ", dimension=" + dimension + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimension;
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GlobalBlockPos that = (GlobalBlockPos) o;
        return dimension == that.dimension && Objects.equals(pos, that.pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }

    public World getWorld() {
        if (world != null)
            return world;
        return world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(dimension));
    }

    public TileEntity getTile() {
        if (getWorld() == null)
            return null;
        return getWorld().getTileEntity(getPos());
    }

    public BlockState getBlockState() {
        return getWorld().getBlockState(getPos());
    }

    public CompoundNBT writeToNBT(CompoundNBT compound) {
        compound.putLong("Gpos", pos.toLong());
        compound.putInt("Gdim", dimension);
        return compound;
    }

    public static GlobalBlockPos loadGlobalPosFromNBT(CompoundNBT nbt) {
        if (!nbt.contains("Gpos") || !nbt.contains("Gdim"))
            return null;
        GlobalBlockPos pos = new GlobalBlockPos(BlockPos.fromLong(nbt.getLong("Gpos")), nbt.getInt("Gdim"));
        return pos.getPos() != null ? pos : null;
    }

    public static GlobalBlockPos fromTile(TileEntity tile) {
        if (tile == null)
            return null;
        return new GlobalBlockPos(tile.getPos(), tile.getWorld());
    }
}
