package mrriegel.limelib;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

public interface IIH extends net.minecraftforge.items.IItemHandler {

	default int getAmount(Predicate<ItemStack> predicate) {
		int amount = 0;
		for (int i = 0; i < getSlots(); i++) {
			ItemStack slot = getStackInSlot(i);
			if (predicate.test(slot))
				amount += slot.getCount();
		}
		return amount;
	}

}
