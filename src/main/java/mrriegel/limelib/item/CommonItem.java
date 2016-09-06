package mrriegel.limelib.item;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonItem extends Item {

	public CommonItem(String name) {
		System.out.println(Loader.instance().activeModContainer().getName());
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public void registerItem() {
		GameRegistry.register(this);
	}

	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		String trans = "tooltip." + getRegistryName();
		if (I18n.hasKey(trans))
			tooltip.add(I18n.format(trans));
	}

}
