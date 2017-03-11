package mrriegel.limelib.jei;

import java.util.Arrays;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mrriegel.limelib.LimeLib;
import mrriegel.limelib.recipe.ShapedRecipeExt;
import net.minecraft.item.ItemStack;

public class ShapedRecipeExtHandler extends BlankRecipeWrapper implements IRecipeHandler<ShapedRecipeExt>, IShapedCraftingRecipeWrapper {
	private IJeiHelpers jeiHelpers;
	private ShapedRecipeExt recipe;

	public ShapedRecipeExtHandler(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
	}

	@Override
	public Class<ShapedRecipeExt> getRecipeClass() {
		return ShapedRecipeExt.class;
	}

	@Override
	public String getRecipeCategoryUid(ShapedRecipeExt recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ShapedRecipeExt recipe) {
		ShapedRecipeExtHandler wrap = new ShapedRecipeExtHandler(jeiHelpers);
		wrap.recipe = recipe;
		return wrap;
	}

	@Override
	public boolean isRecipeValid(ShapedRecipeExt recipe) {
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
		List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(Arrays.asList(recipe.getInput()));
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, recipeOutput);

	}

	@Override
	public int getWidth() {
		return recipe.getWidth();
	}

	@Override
	public int getHeight() {
		return recipe.getHeight();
	}

}
