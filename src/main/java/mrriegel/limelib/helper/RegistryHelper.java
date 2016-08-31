package mrriegel.limelib.helper;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegistryHelper {

	public static void registerItem(Item item) {
		GameRegistry.register(item);
	}

}
