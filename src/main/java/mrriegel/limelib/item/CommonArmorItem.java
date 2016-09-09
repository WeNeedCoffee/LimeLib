package mrriegel.limelib.item;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonArmorItem extends ItemArmor {

	public CommonArmorItem(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String name) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
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
		for (int i = 0; i < 100; i++) {
			String trans = "tooltip." + getRegistryName() + i;
			if (I18n.hasKey(trans))
				tooltip.add(I18n.format(trans));
		}
	}

}
