package kdp.limelib;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

public class PseudoEntity {
    protected final World world;
    public int id;
    public double posX, posY, posZ, lastX, lastY, lastZ;
    protected Mover mover;
    protected float pitch, yaw;
    private Int2LongMap syncMap = new Int2LongOpenHashMap();
    protected FakePlayer fakePlayer;

    public PseudoEntity(World world) {
        this(world, 0, 0, 0);
    }

    public PseudoEntity(World world, double posX, double posY, double posZ) {
        this.world = Objects.requireNonNull(world);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.syncMap.defaultReturnValue(Long.MAX_VALUE);
        if (!world.isRemote) {
            Random ran = new Random(hashCode());
            UUID uuid = new UUID(ran.nextLong(), ran.nextLong());
            this.fakePlayer = new FakePlayer((ServerWorld) world, new GameProfile(uuid, "[" + uuid.toString() + "]"));
        }
    }

    public static class Mover {
        public final double speed, distance;
        public final Vec3d dest, dir;

        public static Mover of(Vec3d pos, Vec3d dest, double speed) {
            if (pos.equals(dest))
                return new Mover(speed, 0, dest, Vec3d.ZERO);
            double distance = pos.distanceTo(dest);
            if (distance <= 0 && false)
                return null;
            return new Mover(speed, distance, dest, dest.subtract(pos));
        }

        public static Mover of(CompoundNBT nbt) {
            return new Mover(nbt);
        }

        private Mover(double speed, double distance, Vec3d dest, Vec3d dir) {
            this.speed = speed;
            this.distance = distance;
            this.dest = dest;
            this.dir = dir;
        }

        private Mover(CompoundNBT nbt) {
            this.speed = nbt.getDouble("speed");
            this.distance = nbt.getDouble("dist");
            this.dest = new Vec3d(nbt.getDouble("dex"), nbt.getDouble("dey"), nbt.getDouble("dez"));
            this.dir = new Vec3d(nbt.getDouble("dix"), nbt.getDouble("diy"), nbt.getDouble("diz"));
        }

        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putDouble("speed", speed);
            nbt.putDouble("dist", distance);
            nbt.putDouble("dex", dest.x);
            nbt.putDouble("dey", dest.y);
            nbt.putDouble("dez", dest.z);
            nbt.putDouble("dix", dir.x);
            nbt.putDouble("diy", dir.y);
            nbt.putDouble("diz", dir.z);
            return nbt;
        }
    }
}
