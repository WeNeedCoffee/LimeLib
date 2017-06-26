package mrriegel.limelib.helper;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber
public class RegistryHelper {
	private static List<IForgeRegistry<?>> regs = Lists.newArrayList();
	static {
		for (Field f : ForgeRegistries.class.getDeclaredFields()) {
			try {
				regs.add((IForgeRegistry<?>) f.get(null));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<IForgeRegistryEntry<?>> entries = Lists.newArrayList();

	@SubscribeEvent
	public static <T extends IForgeRegistryEntry<T>> void regw(@SuppressWarnings("rawtypes") RegistryEvent.Register event) {
		Class<?> clazz = (Class<?>) event.getGenericType();
		for (IForgeRegistryEntry<?> entry : entries) {
			if (clazz.isAssignableFrom(entry.getClass()))
				event.getRegistry().register((T) entry);
		}
	}

	static boolean oldway = true;

	public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistryEntry<T> entry) {
		if (oldway)
			registerOld(entry);
		else
			entries.add(entry);
	}

	public static <T extends IForgeRegistryEntry<T>> void registerOld(IForgeRegistryEntry<T> entry) {
		IForgeRegistry<T> reg = null;
		for (IForgeRegistry<?> r : regs) {
			if (r.getRegistrySuperType().isAssignableFrom(entry.getRegistryType())) {
				reg = (IForgeRegistry<T>) r;
				break;
			}
		}
		if (reg != null)
			reg.register((T) entry);
	}

}
