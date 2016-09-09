package mrriegel.limelib.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mrriegel.limelib.LimeLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

public class Utils {

	public static String getModID() {
		ModContainer mc = Loader.instance().activeModContainer();
		return mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer) mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
	}

	public static List<Enum> getEnums(Class<? extends Enum> clazz) {
		List<Enum> lis = Lists.newArrayList();
		EnumSet enums = EnumSet.allOf(clazz);
		Map<Integer, Enum> map = Maps.newHashMap();
		for (Object o : enums) {
			Enum e = (Enum) o;
			map.put(e.ordinal(), e);
		}
		for (int i = 0; i < map.size(); i++) {
			lis.add(map.get(i));
		}
		return lis;
	}

	public static List<Integer> split(int ii, int splits) {
		List<Integer> ints = Lists.newArrayList();
		for (int i = 0; i < splits; i++)
			ints.add(ii / splits);
		for (int i = 0; i < ii % splits; i++)
			ints.set(i, ints.get(i) + 1);
		return ints;
	}

	public static String formatNumber(int value) {
		if (value < 1000)
			return String.valueOf(value);
		else if (value < 1000000)
			return String.valueOf(Math.round(value) / 1000D) + "K";
		else if (value < 1000000000)
			return String.valueOf(Math.round(value / 1000) / 1000D) + "M";
		else
			return String.valueOf(Math.round(value / 1000000) / 1000D) + "G";
	}

	public static FakePlayer getFakePlayer(WorldServer world) {
		return FakePlayerFactory.get(world, new GameProfile(UUID.fromString("672ec311-27a5-449e-925c-69a55980d378"), LimeLib.MODID + "_FakePlayer"));
	}

	public static RayTraceResult rayTrace(Entity entity, double distance) {
		Vec3d vec3d = entity.getPositionEyes(0);
		Vec3d vec3d1 = entity.getLook(0);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * distance, vec3d1.yCoord * distance, vec3d1.zCoord * distance);
		return entity.worldObj.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	public static RayTraceResult rayTrace(EntityPlayer player) {
		return rayTrace(player, player.capabilities.isCreativeMode ? 5F : 4.5F);
	}

	public static List<Long> getLongList(List<BlockPos> list) {
		List<Long> lis = Lists.newArrayList();
		for (BlockPos p : list)
			if (p != null)
				lis.add(p.toLong());
		return lis;
	}

	public static List<BlockPos> getBlockPosList(List<Long> list) {
		List<BlockPos> lis = Lists.newArrayList();
		for (Long p : list)
			if (p != null)
				lis.add(BlockPos.fromLong(p));
		return lis;
	}
}
