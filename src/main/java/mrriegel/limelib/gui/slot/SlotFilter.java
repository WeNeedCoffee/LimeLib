package mrriegel.limelib.gui.slot;

import java.util.function.Function;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFilter extends Slot {

	private Function<ItemStack, Boolean> func;

	public SlotFilter(IInventory inventoryIn, int index, int xPosition, int yPosition, Function<ItemStack, Boolean> func) {
		super(inventoryIn, index, xPosition, yPosition);
		this.func = func;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return func.apply(stack).booleanValue();
	}

}
