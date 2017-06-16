package mrriegel.limelib.jei;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IFocus.Mode;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@JEIPlugin
public class JEI implements IModPlugin {

	private static IJeiRuntime runtime;

	@Override
	public void register(IModRegistry registry) {
		// registry.addRecipeHandlers(new
		// ShapedRecipeExtHandler(registry.getJeiHelpers()));
		// registry.addRecipeHandlers(new
		// ShapelessRecipeExtHandler(registry.getJeiHelpers()));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
	}

	public static IJeiRuntime getRuntime() {
		return runtime;
	}

	public static void showRecipes(ItemStack stack) {
		showRecipes((Object) stack);
	}

	public static void showUsage(ItemStack stack) {
		showUsage((Object) stack);
	}

	public static void showRecipes(FluidStack stack) {
		showRecipes((Object) stack);
	}

	public static void showUsage(FluidStack stack) {
		showUsage((Object) stack);
	}

	private static void showRecipes(Object stack) {
		runtime.getRecipesGui().show(runtime.getRecipeRegistry().createFocus(Mode.OUTPUT, stack));
	}

	private static void showUsage(Object stack) {
		runtime.getRecipesGui().show(runtime.getRecipeRegistry().createFocus(Mode.INPUT, stack));
	}

	public static void showCategories(List<String> strings) {
		runtime.getRecipesGui().showCategories(strings);
	}

	public static void showCategories(String string) {
		showCategories(Lists.newArrayList(string));
	}

}
