package mrriegel.limelib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BasicItem extends Item {

	public BasicItem(String name, String modid) {
		this.setUnlocalizedName(modid + ":"+name);
		this.setRegistryName(name);
		GameRegistry.registerItem(this);
	}
}
