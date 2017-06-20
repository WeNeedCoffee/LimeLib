package mrriegel.limelib.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;

public abstract class CommonSubtypeItem extends CommonItem {

	private final int num;

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
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab))
			for (int i = 0; i < num; i++)
				subItems.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + stack.getItemDamage();
	}

}
