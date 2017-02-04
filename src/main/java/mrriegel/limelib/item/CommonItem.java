package mrriegel.limelib.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonItem extends Item{

	public CommonItem(String name) {
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public void registerItem() {
		GameRegistry.register(this);
	}

	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
