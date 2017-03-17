package mrriegel.limelib.jei;

import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mrriegel.limelib.LimeLib;
import mrriegel.limelib.recipe.ShapelessRecipeExt;
import net.minecraft.item.ItemStack;

public class ShapelessRecipeExtHandler extends BlankRecipeWrapper implements IRecipeHandler<ShapelessRecipeExt> {
	private IJeiHelpers jeiHelpers;
	private ShapelessRecipeExt recipe;

	public ShapelessRecipeExtHandler(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public Class<ShapelessRecipeExt> getRecipeClass() {
		return ShapelessRecipeExt.class;
	}

	@Override
	public String getRecipeCategoryUid(ShapelessRecipeExt recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ShapelessRecipeExt recipe) {
		ShapelessRecipeExtHandler wrap = new ShapelessRecipeExtHandler(jeiHelpers);
		wrap.recipe = recipe;
		return wrap;
	}

	@Override
	public boolean isRecipeValid(ShapelessRecipeExt recipe) {
		if (recipe.getRecipeOutput() == null) {
			LimeLib.log.error("Recipe has no outputs. {}");
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.getInput()) {
			if (input instanceof List) {
				if (((List<?>) input).isEmpty()) {
					return false;
				}
			}
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount > 9) {
			LimeLib.log.error("Recipe has too many inputs. {}");
			return false;
		}
		if (inputCount == 0) {
			LimeLib.log.error("Recipe has no inputs. {}");
			return false;
		}
		return true;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = jeiHelpers.getStackHelper();
		ItemStack recipeOutput = recipe.getRecipeOutput();

		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getInput());
		ingredients.setInputLists(ItemStack.class, inputs);

		if (recipeOutput != null) {
			ingredients.setOutput(ItemStack.class, recipeOutput);
		}

	}

}
