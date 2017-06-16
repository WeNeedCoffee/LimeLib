package mrriegel.limelib.recipe;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Deprecated
public class ShapelessRecipeExt implements IRecipe {
	static {
		RecipeSorter.register(LimeLib.MODID + ":shapelessExt", ShapelessRecipeExt.class, Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@Nonnull
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Object> input = NonNullList.create();

	public ShapelessRecipeExt(@Nonnull ItemStack result, Object... recipe) {
		output = result.copy();
		for (Object in : recipe) {
			if (in instanceof ItemStack) {
				input.add(((ItemStack) in).copy());
			} else if (in instanceof Item) {
				input.add(new ItemStack((Item) in));
			} else if (in instanceof Block) {
				input.add(new ItemStack((Block) in));
			} else if (in instanceof String) {
				input.add(OreDictionary.getOres((String) in));
			} else if (in instanceof List) {
				StackHelper.toStackList((List<Object>) in);
				input.add(in);
			} else {
				throw new RuntimeException("wrong input");
			}
		}
	}

	// @Override
	public int getRecipeSize() {
		return input.size();
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	@Override
	public boolean matches(InventoryCrafting var1, World world) {
		NonNullList<Object> required = NonNullList.create();
		required.addAll(input);

		for (int x = 0; x < var1.getSizeInventory(); x++) {
			ItemStack slot = var1.getStackInSlot(x);

			if (!slot.isEmpty()) {
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();

				while (req.hasNext()) {
					boolean match = false;
					Object next = req.next();

					if (next instanceof ItemStack) {
						match = OreDictionary.itemMatches((ItemStack) next, slot, false);
					} else if (next instanceof List) {
						Iterator<Object> itr = ((List<Object>) next).iterator();
						while (itr.hasNext() && !match) {
							Object nex = itr.next();
							if (nex instanceof ItemStack)
								match = OreDictionary.itemMatches((ItemStack) nex, slot, false);
							else if (nex instanceof Item)
								match = slot.getItem() == nex;
							else if (nex instanceof Block)
								match = slot.getItem() == Item.getItemFromBlock((Block) nex);
						}
					}
					if (match) {
						inRecipe = true;
						required.remove(next);
						break;
					}
				}
				if (!inRecipe) {
					return false;
				}
			}
		}

		return required.isEmpty();
	}

	public NonNullList<Object> getInput() {
		return this.input;
	}

	@Override
	@Nonnull
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceLocation getRegistryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFit(int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}
}
