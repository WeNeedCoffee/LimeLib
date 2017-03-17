package mrriegel.limelib.recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import mrriegel.limelib.helper.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

public class ShapedRecipeExt implements IRecipe {
	public static final int MAX_CRAFT_GRID_WIDTH = 3;
	public static final int MAX_CRAFT_GRID_HEIGHT = 3;

	@Nonnull
	protected ItemStack output = ItemStack.EMPTY;
	protected Object[] input = null;
	protected int width = 0;
	protected int height = 0;
	protected boolean mirrored = true;

	public ShapedRecipeExt(@Nonnull ItemStack result, Object... recipe) {
		output = result.copy();

		String shape = "";
		int idx = 0;

		if (recipe[idx] instanceof Boolean) {
			mirrored = (Boolean) recipe[idx];
			if (recipe[idx + 1] instanceof Object[]) {
				recipe = (Object[]) recipe[idx + 1];
			} else {
				idx = 1;
			}
		}

		if (recipe[idx] instanceof String[]) {
			String[] parts = ((String[]) recipe[idx++]);
			for (String s : parts) {
				width = s.length();
				shape += s;
			}

			height = parts.length;
		} else {
			while (recipe[idx] instanceof String) {
				String s = (String) recipe[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}

		if (width * height != shape.length()) {
			String ret = "Invalid shaped ore recipe: ";
			for (Object tmp : recipe) {
				ret += tmp + ", ";
			}
			ret += output;
			throw new RuntimeException(ret);
		}

		HashMap<Character, Object> itemMap = new HashMap<Character, Object>();

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character) recipe[idx];
			Object in = recipe[idx + 1];

			if (in instanceof ItemStack) {
				itemMap.put(chr, ((ItemStack) in).copy());
			} else if (in instanceof Item) {
				itemMap.put(chr, new ItemStack((Item) in));
			} else if (in instanceof Block) {
				itemMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
			} else if (in instanceof String) {
				itemMap.put(chr, OreDictionary.getOres((String) in));
			} else if (in instanceof List) {
				StackHelper.toStackList((List<Object>) in);
				itemMap.put(chr, in);
			} else {
				throw new RuntimeException("wrong input");
			}
		}

		input = new Object[width * height];
		int x = 0;
		for (char chr : shape.toCharArray()) {
			input[x++] = itemMap.get(chr);
		}
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
		return output.copy();
	}

	@Override
	public int getRecipeSize() {
		return input.length;
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
			for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}

				if (mirrored && checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Object target = null;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input[width - subX - 1 + subY * width];
					} else {
						target = input[subX + subY * width];
					}
				}

				ItemStack slot = inv.getStackInRowAndColumn(x, y);

				if (target instanceof ItemStack) {
					if (!OreDictionary.itemMatches((ItemStack) target, slot, false)) {
						return false;
					}
				} else if (target instanceof List) {
					boolean matched = false;

					Iterator<Object> itr = ((List<Object>) target).iterator();
					while (itr.hasNext() && !matched) {
						Object nex = itr.next();
						if (nex instanceof ItemStack)
							matched = OreDictionary.itemMatches((ItemStack) nex, slot, false);
						else if (nex instanceof Item)
							matched = slot.getItem() == nex;
						else if (nex instanceof Block)
							matched = slot.getItem() == Item.getItemFromBlock((Block) nex);
					}

					if (!matched) {
						return false;
					}
				} else if (target == null && !slot.isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

	public ShapedRecipeExt setMirrored(boolean mirror) {
		mirrored = mirror;
		return this;
	}

	public Object[] getInput() {
		return this.input;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
