package mrriegel.limelib.recipe;

import java.util.Collections;
import java.util.List;

import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.util.FilterItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import com.google.common.collect.Lists;

public class RecipeItemHandler extends AbstractRecipe<ItemStack, IItemHandler> {

	public RecipeItemHandler(List<ItemStack> output, boolean order, Object... input) {
		super(output, order, input);
	}

	@Override
	protected List<ItemStack> getIngredients(IItemHandler object) {
		List<ItemStack> lis = Lists.newArrayList();
		for (int i = 0; i < object.getSlots(); i++)
			lis.add(object.getStackInSlot(i));
		if (!order)
			lis.removeAll(Collections.singleton(null));
		return lis;
	}

	@Override
	public void removeIngredients(IItemHandler object) {
		for (Object o : getInput()) {
			FilterItem f = null;
			if (o instanceof Item)
				f = new FilterItem((Item) o);
			if (o instanceof Block)
				f = new FilterItem((Block) o);
			if (o instanceof String)
				f = new FilterItem((String) o);
			if (o instanceof ItemStack) {
				f = new FilterItem((ItemStack) o);
			}
			InvHelper.extractItem(object, f, 1, false);
		}
	}

	@Override
	public List<ItemStack> getResult(IItemHandler object) {
		return getOutput();
	}

}
