package mrriegel.limelib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BasicBlock extends Block {

	public BasicBlock(Material material, String name, String modid) {
		super(material);
		this.setUnlocalizedName(modid + ":" + name);
		this.setRegistryName(name);
		GameRegistry.registerBlock(this);
	}

}
