package mrriegel.limelib.util;

import java.util.Random;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class Serious {
	public static void init() {
		{
			class RandomShapedRecipe extends ShapedOreRecipe {

				public RandomShapedRecipe() {
					super(new ItemStack(Items.NETHER_STAR), "gre", 'g', "dustGlowstone", 'r', "dustRedstone", 'e', "logWood");
				}

				@Override
				public Object[] getInput() {
					Object[] oa = new Object[3];
					for (int i = 0; i < 3; i++) {
						oa[i] = OreDictionary.getOreNames()[new Random().nextInt(OreDictionary.getOreNames().length)];
					}
					return oa;
					//				return super.getInput();
				}

				@Override
				public ItemStack getRecipeOutput() {
					return NBTStackHelper.setTag(super.getRecipeOutput(), "display", NBTHelper.setString(new NBTTagCompound(), "Name", "Is it worth it?"));
				}

				@Override
				public ItemStack getCraftingResult(InventoryCrafting var1) {
					return NBTStackHelper.setTag(new ItemStack(Blocks.CLAY), "display", NBTHelper.setString(new NBTTagCompound(), "Name", "I don't think so."));
				}

			}
			RecipeSorter.register(LimeLib.MODID + ":random", RandomShapedRecipe.class, Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
			GameRegistry.addRecipe(new RandomShapedRecipe());
		}
	}
}
