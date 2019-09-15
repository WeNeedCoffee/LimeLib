package kdp.limelib.helper;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;

import org.apache.commons.lang3.Validate;

import kdp.limelib.LimeLib;

@Mod.EventBusSubscriber(modid = LimeLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHelper {
    private static final Set<IForgeRegistryEntry<?>> ENTRIES = Collections.newSetFromMap(new IdentityHashMap<>());
    private static boolean registered = false;

    @SubscribeEvent
    public static void registerEvent(RegistryEvent.Register event) {
        registered = true;
        Class<?> clazz = (Class<?>) event.getGenericType();
        Iterator<IForgeRegistryEntry<?>> it = ENTRIES.iterator();
        while (it.hasNext()) {
            IForgeRegistryEntry<?> entry = it.next();
            if (clazz.isAssignableFrom(entry.getClass())) {
                event.getRegistry().register(entry);
                it.remove();
            }
        }

    }

    public static <T extends IForgeRegistryEntry<T>> T register(T entry) {
        Validate.isTrue(!registered, "Too late to register entries");
        ENTRIES.add(Objects.requireNonNull(entry, "entry must not be null"));
        return entry;
    }

    public static <T extends IForgeRegistryEntry<T>> T unregister(T entry) {
        Validate.isTrue(!registered, "Too late to unregister entries");
        ENTRIES.remove(Objects.requireNonNull(entry, "entry must not be null"));
        return entry;
    }
}
