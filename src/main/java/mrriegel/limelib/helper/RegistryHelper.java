package mrriegel.limelib.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import mrriegel.limelib.LimeLib;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(modid = LimeLib.MODID)
public class RegistryHelper {
	private static final List<IForgeRegistryEntry<?>> entries = Lists.newArrayList();
	private static final Map<Pair<Item, Integer>, ModelResourceLocation> models = Maps.newHashMap();

	@SubscribeEvent
	public static <T extends IForgeRegistryEntry<T>> void registerEvent(@SuppressWarnings("rawtypes") RegistryEvent.Register event) {
		Class<?> clazz = (Class<?>) event.getGenericType();
		Set<IForgeRegistryEntry<?>> toRemove = Sets.newHashSet();
		for (IForgeRegistryEntry<?> entry : entries) {
			if (clazz.isAssignableFrom(entry.getClass())) {
				event.getRegistry().register(entry);
				toRemove.add(entry);
			}
		}
		toRemove.forEach(e -> entries.remove(e));
	}

	@SubscribeEvent
	public static void registerModelEvent(ModelRegistryEvent event) {
		for (Map.Entry<Pair<Item, Integer>, ModelResourceLocation> e : models.entrySet()) {
			ModelLoader.setCustomModelResourceLocation(e.getKey().getKey(), e.getKey().getValue(), e.getValue());
		}
	}

	public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistryEntry<T> entry) {
		entries.add(entry);
	}

	public static void initModel(Item item, int meta, ModelResourceLocation mrl) {
		Validate.isTrue(item != null && item != Items.AIR);
		models.put(Pair.of(item, meta), mrl);
	}

}
