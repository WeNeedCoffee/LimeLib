package mrriegel.limelib.block;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.item.CommonItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

	public <T extends Comparable<T>, V extends T> void changeProperty(World world, BlockPos pos, IProperty<T> property, V value) {
		if (!getBlockState().getProperties().contains(property))
			LimeLib.log.warn("Property " + property.getName() + " doesn't fit to " + getRegistryName() + ".");
		else {
			IBlockState state = world.getBlockState(pos);
			if (!state.getValue(property).equals(value)) {
				world.setBlockState(pos, state.withProperty(property, value));
				world.markBlockRangeForRenderUpdate(pos, pos);
			}
		}
	}

}
