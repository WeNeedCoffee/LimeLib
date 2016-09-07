package mrriegel.limelib.tile;

import net.minecraft.item.ItemStack;

public interface IDataKeeper {

	void writeToStack(ItemStack stack);

	void readFromStack(ItemStack stack);
}
