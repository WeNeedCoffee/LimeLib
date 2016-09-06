package mrriegel.testmod;

import mrriegel.limelib.item.CommonItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Loader;

public class TestItem extends CommonItem {

	public TestItem() {
		super("nano");
		setCreativeTab(CreativeTabs.REDSTONE);
		System.out.println(Loader.instance().activeModContainer().getName());
		System.out.println("zip");
	}

}
