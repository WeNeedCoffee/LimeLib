package mrriegel.limelib.farm;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class PseudoEntity implements INBTSerializable<NBTTagCompound> {

	protected final World world;
	public int id;
	public double posX, posY, posZ, lastX, lastY, lastZ;
	protected Mover mover;
	protected float pitch, yaw;
	private Int2LongMap syncMap = new Int2LongOpenHashMap();

	public PseudoEntity(World world, double posX, double posY, double posZ) {
		super();
		this.world = Objects.requireNonNull(world);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.syncMap.defaultReturnValue(Long.MAX_VALUE);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", posX);
		nbt.setDouble("y", posY);
		nbt.setDouble("z", posZ);
		if (mover != null)
			nbt.setTag("mover", mover.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		posX = lastX = nbt.getDouble("x");
		posY = lastY = nbt.getDouble("y");
		posZ = lastZ = nbt.getDouble("z");
		if (nbt.hasKey("mover"))
			mover = Mover.of(nbt.getCompoundTag("mover"));
	}

	public void update() {
		lastX = posX;
		lastY = posY;
		lastZ = posZ;
		if (mover != null) {
			Vec3d current = new Vec3d(posX, posY, posZ);
			double distance = current.distanceTo(mover.dest);
			double distancePerTick = mover.speed / 10;
			double percent = distance / mover.distance;
			double diff2Max = Math.abs(.5 - percent) * -1 + 1;
			distancePerTick *= diff2Max * 1.5;
			if (distance < distancePerTick) {
				posX = mover.dest.x;
				posY = mover.dest.y;
				posZ = mover.dest.z;
				onArrival();
				mover = null;
			} else {
				double scale = distancePerTick / mover.distance;
				posX += mover.dir.x * scale;
				posY += mover.dir.y * scale;
				posZ += mover.dir.z * scale;
			}
		}
	}

	protected void onArrival() {
	}

	protected void onDeath() {
	}

	public Vec3d getPosition() {
		return new Vec3d(posX, posY, posZ);
	}

	public void move(BlockPos pos, double speed) {
		move(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, speed);
	}

	public void move(Vec3d vec, double speed) {
		move(vec.x, vec.y, vec.z, speed);
	}

	public void move(double x, double y, double z, double speed) {
		Validate.isTrue(!world.isRemote, " don't move on client");
		Validate.isTrue(speed > 0, "speed must not be negative");
		mover = Mover.of(getPosition(), new Vec3d(x, y, z), speed);
		if (mover == null) {
			onArrival();
		} else {
			//TODO send move to client
			sync(SyncType.MOVEMENT);
			yaw = (float) (Math.atan2(0, 0) - Math.atan2(mover.dir.z, mover.dir.x));

		}
	}

	public void sync(SyncType type) {
		syncMap.put(type.ordinal(), System.currentTimeMillis());
	}

	public AxisAlignedBB hitbox() {
		return null;
	}

	protected enum SyncType {
		POSITION, MOVEMENT;
	}

	public static class Mover {
		public final double speed, distance;
		public final Vec3d dest, dir;

		public static Mover of(Vec3d pos, Vec3d dest, double speed) {
			double distance = pos.distanceTo(dest);
			if (distance <= 0)
				return null;
			return new Mover(speed, distance, dest, dest.subtract(pos));
		}

		public static Mover of(NBTTagCompound nbt) {
			return new Mover(nbt);
		}

		private Mover(double speed, double distance, Vec3d dest, Vec3d dir) {
			this.speed = speed;
			this.distance = distance;
			this.dest = dest;
			this.dir = dir;
		}

		private Mover(NBTTagCompound nbt) {
			this.speed = nbt.getDouble("speed");
			this.distance = nbt.getDouble("dist");
			this.dest = new Vec3d(nbt.getDouble("dex"), nbt.getDouble("dey"), nbt.getDouble("dez"));
			this.dir = new Vec3d(nbt.getDouble("dix"), nbt.getDouble("diy"), nbt.getDouble("diz"));
		}

		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setDouble("speed", speed);
			nbt.setDouble("dist", distance);
			nbt.setDouble("dex", dest.x);
			nbt.setDouble("dey", dest.y);
			nbt.setDouble("dez", dest.z);
			nbt.setDouble("dix", dir.x);
			nbt.setDouble("diy", dir.y);
			nbt.setDouble("diz", dir.z);
			return nbt;
		}
	}

}
