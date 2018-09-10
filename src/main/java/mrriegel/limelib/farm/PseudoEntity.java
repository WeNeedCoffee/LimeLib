package mrriegel.limelib.farm;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class PseudoEntity implements INBTSerializable<NBTTagCompound> {

	protected final World world;
	public double posX, posY, posZ, lastX, lastY, lastZ;
	protected double speed, totalDistance;
	protected boolean moving;
	protected Vec3d dest, dir;
	public int id;

	public PseudoEntity(World world, double posX, double posY, double posZ) {
		super();
		this.world = Objects.requireNonNull(world);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", posX);
		nbt.setDouble("y", posY);
		nbt.setDouble("z", posZ);
		nbt.setDouble("speed", speed);
		nbt.setDouble("dist", totalDistance);
		nbt.setBoolean("moving", moving);
		if (dest != null) {
			nbt.setDouble("ax", dest.x);
			nbt.setDouble("ay", dest.y);
			nbt.setDouble("az", dest.z);
		}
		if (dir != null) {
			nbt.setDouble("bx", dir.x);
			nbt.setDouble("by", dir.y);
			nbt.setDouble("bz", dir.z);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		posX = lastX = nbt.getDouble("x");
		posY = lastY = nbt.getDouble("y");
		posZ = lastZ = nbt.getDouble("z");
		speed = nbt.getDouble("speed");
		totalDistance = nbt.getDouble("dist");
		moving = nbt.getBoolean("moving");
		if (nbt.hasKey("ax")) {
			dest = new Vec3d(nbt.getDouble("ax"), nbt.getDouble("ay"), nbt.getDouble("az"));
		}
		if (nbt.hasKey("bx")) {
			dir = new Vec3d(nbt.getDouble("bx"), nbt.getDouble("by"), nbt.getDouble("bz"));
		}
	}

	public void update() {
		lastX = posX;
		lastY = posY;
		lastZ = posZ;
		if (moving) {
			Vec3d current = new Vec3d(posX, posY, posZ);
			double distance = current.distanceTo(dest);
			double distancePerTick = speed / 10;
			double percent = distance / totalDistance;
			double diff2Max = Math.abs(.5 - percent) * -1 + 1;
			distancePerTick *= diff2Max * 1.5;
			if (distance < distancePerTick) {
				posX = dest.x;
				posY = dest.y;
				posZ = dest.z;
				moving = false;
				dest = null;
				dir = null;
				onArrival();
			} else {
				double scale = distancePerTick / dir.lengthVector();
				posX += dir.x * scale;
				posY += dir.y * scale;
				posZ += dir.z * scale;
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
		Validate.isTrue(speed > 0, "speed must not be negative");
		this.dest = new Vec3d(x, y, z);
		this.speed = speed;
		this.dir = dest.subtract(posX, posY, posZ);
		this.totalDistance = getPosition().distanceTo(dest);
		this.moving = totalDistance > 0;
		if (!moving) {
			dest = null;
			dir = null;
			onArrival();
		}
	}

}
