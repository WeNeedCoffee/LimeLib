package kdp.limelib.util;

import org.apache.commons.lang3.Validate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class GlobalBlockPos {
	private final BlockPos pos;
	private final int dimension;
	private transient World world;

	public GlobalBlockPos(BlockPos pos, int dimension) {
		this.pos = pos;
		this.dimension = dimension;
	}

	public GlobalBlockPos(BlockPos pos, World world) {
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlobalBlockPos other = (GlobalBlockPos) obj;
		if (dimension != other.dimension)
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
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

	public IBlockState getBlockState() {
		return getWorld().getBlockState(getPos());
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (pos != null)
			compound.setLong("Gpos", pos.toLong());
		compound.setInt("Gdim", dimension);
		return compound;
	}

	public static GlobalBlockPos loadGlobalPosFromNBT(NBTTagCompound nbt) {
		if (!nbt.hasKey("Gpos") || !nbt.hasKey("Gdim"))
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
