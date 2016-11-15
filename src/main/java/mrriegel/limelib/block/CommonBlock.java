package mrriegel.limelib.block;

import mrriegel.limelib.item.CommonItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonBlock extends Block {

	public CommonBlock(Material materialIn, String name) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public void registerBlock() {
		GameRegistry.register(this);
		GameRegistry.register(getItemBlock());
	}

	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	protected ItemBlock getItemBlock() {
		return new CommonItemBlock(this);
	}

}
