package mrriegel.limelib.item;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public abstract class CommonSubtypeItem extends CommonItem {

	int num;

	public CommonSubtypeItem(String name, int num) {
		super(name);
		this.num = num;
		setHasSubtypes(true);
	}

	@Override
	public void initModel() {
		for (int i = 0; i < num; i++)
			ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i = 0; i < num; i++)
			subItems.add(new ItemStack(itemIn, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + stack.getItemDamage();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String trans = "tooltip." + getRegistryName() + "_" + stack.getItemDamage();
		if (I18n.hasKey(trans))
			tooltip.add(I18n.format(trans));
	}

	// public abstract Class<? extends Enum> getEnum();

}
