package mrriegel.limelib.helper;

import java.util.List;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.util.Utils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.google.common.collect.Lists;

public class ParticleHelper {

	public static List<Vec3d> getVecsForLine(BlockPos pos1, BlockPos pos2, double frequence) {
		return getVecsForLine(pos1.getX() + .5, pos1.getY() + .5, pos1.getZ() + .5, pos2.getX() + .5, pos2.getY() + .5, pos2.getZ() + .5, frequence);
	}

	public static List<Vec3d> getVecsForLine(double x1, double y1, double z1, double x2, double y2, double z2, double frequence) {
		List<Vec3d> lis = Lists.newArrayList();
		final Vec3d ovec = new Vec3d(x2 - x1, y2 - y1, z2 - z1);
		int amount = (int) (ovec.lengthVector() * frequence);
		Vec3d toAdd = new Vec3d(ovec.xCoord / amount, ovec.yCoord / amount, ovec.zCoord / amount);
		Vec3d foo = Vec3d.ZERO;
		for (int i = 0; i < amount + 1; i++) {
			lis.add(new Vec3d(x1 + foo.xCoord, y1 + foo.yCoord, z1 + foo.zCoord));
			foo = foo.add(toAdd);
		}
		return lis;
	}

	public static List<Vec3d> getVecsForCircle(BlockPos pos1, double radius, double frequence, EnumFacing.Axis axis) {
		return getVecsForCircle(pos1.getX() + .5, pos1.getY() + .5, pos1.getZ() + .5, radius, frequence, axis);
	}

	public static List<Vec3d> getVecsForCircle(double x1, double y1, double z1, double radius, double frequence, EnumFacing.Axis axis) {
		List<Vec3d> lis = Lists.newArrayList();
		int amount = (int) (2 * Math.PI * radius * frequence);
		double degree = 360 / (double) amount;
		double add = (LimeLib.proxy.getClientWorld().getTotalWorldTime() / ((int) radius + 1)) % 360;
		add = 0;
		for (double i = 0 + add; i < 360 + add; i += degree) {
			Vec3d foo = null;
			double value = i * (Math.PI / 180D);
			switch (axis) {
			case Y:
				foo = new Vec3d(Math.cos(value) * radius, 0, Math.sin(value) * radius);
				break;
			case X:
				foo = new Vec3d(0, Math.cos(value) * radius, Math.sin(value) * radius);
				break;
			case Z:
				foo = new Vec3d(Math.cos(value) * radius, Math.sin(value) * radius, 0);
				break;
			}
			lis.add(new Vec3d(x1 + foo.xCoord, y1 + foo.yCoord, z1 + foo.zCoord));
		}
		return lis;
	}

	public static List<Vec3d> getVecsForExplosion(double force, double frequence, EnumFacing.Axis axis) {
		List<Vec3d> lis = Lists.newArrayList();
		for (Vec3d vec : ParticleHelper.getVecsForCircle(0, 0, 0, force, frequence, axis))
			lis.add(new Vec3d(vec.xCoord, vec.yCoord, vec.zCoord));
		return lis;
	}

	public static Vec3d getVecForSpirale(double force, double speed, double frequence, boolean reverse, EnumFacing.Axis axis) {
		List<Vec3d> lis = ParticleHelper.getVecsForCircle(0, 0, 0, force, frequence, axis);
		if (reverse)
			lis = Lists.reverse(lis);
		//		System.out.println(lis.size()+" ");
		int index = ((int) ((System.currentTimeMillis() % Integer.MAX_VALUE) * speed)) % lis.size();
		//		System.out.println("index "+index);
		Vec3d vec = lis.get(index);
		switch (axis) {
		case Y:
			return new Vec3d(vec.xCoord, 0, vec.zCoord);
		case X:
			return new Vec3d(0, vec.yCoord, vec.zCoord);
		case Z:
			return new Vec3d(vec.xCoord, vec.yCoord, 0);
		}
		return null;
	}

	public static List<Vec3d> getVecsForBlock(BlockPos pos, int amount) {
		List<Vec3d> lis = Lists.newArrayList();
		for (int i = 0; i < amount; i++) {
			lis.add(new Vec3d(pos.getX() + Utils.getRandomNumber(0., 1.), pos.getY() + Utils.getRandomNumber(0., 1.), pos.getZ() + Utils.getRandomNumber(0., 1.)));
		}
		return lis;
	}

	public static final ResourceLocation roundParticle = new ResourceLocation(LimeLib.MODID + ":particle/round_particle");
	public static final ResourceLocation sparkleParticle = new ResourceLocation(LimeLib.MODID + ":particle/sparkle_particle");
	public static final ResourceLocation squareParticle = new ResourceLocation(LimeLib.MODID + ":particle/square_particle");

}
