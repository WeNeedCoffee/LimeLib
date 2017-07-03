package mrriegel.limelib.helper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

import mrriegel.limelib.recipe.ShapedRecipeExt;
import mrriegel.limelib.recipe.ShapelessRecipeExt;
import mrriegel.limelib.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;

public class RecipeHelper {

	public static void addShapedOreRecipe(ItemStack stack, Object... input) {
		ResourceLocation rl = name(stack, input);
		addRecipe(rl, new ShapedRecipeExt(rl, stack, input));
	}

	public static void addShapedRecipe(ItemStack stack, Object... input) {
		ResourceLocation rl = name(stack, input);
		ShapedPrimer sp = CraftingHelper.parseShaped(input);
		addRecipe(rl, new ShapedRecipes("", sp.width, sp.height, sp.input, stack));
	}

	public static void addShapelessOreRecipe(ItemStack stack, Object... input) {
		ResourceLocation rl = name(stack, input);
		addRecipe(rl, new ShapelessRecipeExt(rl, stack, input));
	}

	public static void addShapelessRecipe(ItemStack stack, Object... input) {
		ResourceLocation rl = name(stack, input);
		addRecipe(rl, new ShapelessRecipes("", stack, NonNullList.<Ingredient> from(Ingredient.EMPTY, Lists.newArrayList(input).stream().map(o -> CraftingHelper.getIngredient(o)).filter(o -> o != null).collect(Collectors.toList()).toArray(new Ingredient[0]))));
	}

	public static void add(IRecipe recipe) {
		ResourceLocation rl = name(recipe.getRecipeOutput(), recipe.getIngredients());
		addRecipe(rl, recipe);
	}

	private static void addRecipe(ResourceLocation rl, IRecipe recipe) {
		Validate.isTrue(!recipe.getRecipeOutput().isEmpty());
		recipe.setRegistryName(rl);
		RegistryHelper.register(recipe);
	}

	private static ResourceLocation name(ItemStack stack, Object... input) {
		return new ResourceLocation(Utils.getCurrentModID(), stack.getItem().getRegistryName().getResourcePath() + "_" + (Math.abs(Lists.newArrayList(input).hashCode()) % 9999));
	}

	public static Ingredient getIngredient(Object obj) {
		Ingredient ret = CraftingHelper.getIngredient(obj);
		if (ret != null)
			return ret;
		List<Ingredient> lis = Lists.newArrayList();
		if (obj instanceof Collection) {
			for (Object o : (Collection<?>) obj)
				lis.add(CraftingHelper.getIngredient(o));

		}
		return new CompoundIngredient(lis);
	}

	private static class CompoundIngredient extends net.minecraftforge.common.crafting.CompoundIngredient {

		protected CompoundIngredient(Collection<Ingredient> children) {
			super(children.stream().filter(i -> i != null).collect(Collectors.toList()));
		}

	}
}
