package mrriegel.limelib.jei;

import java.util.Collections;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mrriegel.limelib.LimeLib;
import mrriegel.limelib.recipe.ShapelessRecipeExt;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ShapelessRecipeExtHandler implements IRecipeHandler<ShapelessRecipeExt>, IRecipeWrapper {
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
		this.recipe = recipe;
		return this;
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

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
