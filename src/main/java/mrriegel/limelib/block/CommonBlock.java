package mrriegel.limelib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class CommonBlock extends Block {

	public CommonBlock(Material materialIn, String name) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public void registerBlock() {

	}

}
