package mrriegel.limelib.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

}
