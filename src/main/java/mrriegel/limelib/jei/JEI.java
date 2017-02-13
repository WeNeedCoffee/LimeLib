package mrriegel.limelib.jei;

import java.util.List;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

@JEIPlugin
public class JEI implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeHandlers(new ShapedRecipeExtHandler(registry.getJeiHelpers()));
		registry.addRecipeHandlers(new ShapelessRecipeExtHandler(registry.getJeiHelpers()));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

	public static void toStackList(List<Object> lis) {
		for (int i = 0; i < lis.size(); i++) {
			Object o = lis.get(i);
			if (o instanceof Item)
				lis.set(i, new ItemStack((Item) o, 1, OreDictionary.WILDCARD_VALUE));
			if (o instanceof Block)
				lis.set(i, new ItemStack((Block) o, 1, OreDictionary.WILDCARD_VALUE));
		}
	}

}
