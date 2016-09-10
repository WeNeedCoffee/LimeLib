package mrriegel.limelib.helper;

import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.google.common.collect.Lists;

public class ParticleHelper {

	public static List<Vec3d> getVecs(BlockPos pos1, BlockPos pos2, double frequence) {
		return getVecs(pos1.getX() + .5, pos1.getY() + .5, pos1.getZ() + .5, pos2.getX() + .5, pos2.getY() + .5, pos2.getZ() + .5, frequence);
	}

	public static List<Vec3d> getVecs(double x1, double y1, double z1, double x2, double y2, double z2, double frequence) {
		List<Vec3d> lis = Lists.newArrayList();
		final Vec3d ovec = new Vec3d(x2 - x1, y2 - y1, z2 - z1);
		int amount = (int) (ovec.lengthVector() * frequence);
		Vec3d toAdd = new Vec3d(ovec.xCoord / amount, ovec.yCoord / amount, ovec.zCoord / amount);
		Vec3d foo = Vec3d.ZERO;
		Random rand = new Random();
		for (int i = 0; i < amount + 1; i++) {
			lis.add(new Vec3d(x1 + foo.xCoord, y1 + foo.yCoord, z1 + foo.zCoord));
			foo = foo.add(toAdd);
		}
		return lis;
	}

}
