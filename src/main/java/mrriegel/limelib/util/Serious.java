package mrriegel.limelib.util;

import java.lang.reflect.Field;
import java.util.Random;

import com.google.common.collect.Lists;

import mrriegel.limelib.Config;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.helper.RegistryHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class Serious {

	public static void preinit() {
		String k = "\u0075\u006E\u006B\u006E\u006F\u0077\u006E";
		ResourceLocation rl = new ResourceLocation(k, k);
		RandomShapedRecipe rsh = new RandomShapedRecipe(rl);
		try {
			Field f = Impl.class.getDeclaredField("registryName");
			f.setAccessible(true);
			f.set(rsh, rl);
			RegistryHelper.register(rsh);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
	}

	public static void init() {
		if (Config.commandBlockCreativeTab) {
			Blocks.COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
			Blocks.CHAIN_COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
			Blocks.REPEATING_COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
		}
	}

	private static class RandomShapedRecipe extends ShapedOreRecipe {

		public RandomShapedRecipe(ResourceLocation rl) {
			super(rl, new ItemStack(Items.NETHER_STAR), "gre", 'g', "dustGlowstone", 'r', "dustRedstone", 'e', "logWood");
		}

		@Override
		public NonNullList<Ingredient> getIngredients() {
			NonNullList<Ingredient> lis = NonNullList.create();
			for (int i = 0; i < 3; i++) {
				lis.add(RecipeHelper.getIngredient(Lists.newArrayList(ForgeRegistries.ITEMS.getValues()).get(new Random().nextInt(ForgeRegistries.ITEMS.getValues().size()))));
				//										lis.add(RecipeHelper.getIngredient(OreDictionary.getOreNames()[new Random().nextInt(OreDictionary.getOreNames().length)]));
			}
			return lis;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return NBTStackHelper.set(super.getRecipeOutput(), "display", NBTHelper.set(new NBTTagCompound(), "Name", "Is it worth it?"));
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting var1) {
			return NBTStackHelper.set(new ItemStack(Blocks.CLAY), "display", NBTHelper.set(new NBTTagCompound(), "Name", "I don't think so."));
		}

	}
}
