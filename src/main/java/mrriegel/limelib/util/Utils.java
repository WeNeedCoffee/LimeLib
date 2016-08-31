package mrriegel.limelib.util;

import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class Utils {
	
	public static String getModID()  {
		ModContainer mc = Loader.instance().activeModContainer();
        return mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
	}

}
