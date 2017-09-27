package mrriegel.limelib.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.recipe.ShapedRecipeExt;
import mrriegel.limelib.recipe.ShapelessRecipeExt;
import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeHelper {

	public static final boolean dev = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	private static Map<String, RecipeHelper> helpers = Maps.newHashMap();

	private File DIR;
	private final Set<String> USED_OD_NAMES = Sets.newTreeSet();
	private boolean oldway = true;

	private static RecipeHelper getHelper() {
		String modid = Utils.getCurrentModID();
		RecipeHelper rh = helpers.get(modid);
		if (rh != null)
			return rh;
		rh = new RecipeHelper(modid);
		helpers.put(modid, rh);
		return rh;

	}

	private RecipeHelper(String modid) {
		DIR = new File("").toPath().resolve("../src/main/resources/assets/" + modid + "/recipes/").toFile();
		if (!DIR.exists() && dev)
			DIR.mkdirs();
		//		if (DIR.exists())
		//			Arrays.stream(DIR.listFiles()).forEach(File::delete);
		if (!dev) {
			File jar = Loader.instance().activeModContainer().getSource();
			try {
				JarInputStream jis = new JarInputStream(new FileInputStream(jar));
				JarEntry e = null;
				while ((e = jis.getNextJarEntry()) != null)
					if (e.getName().equals("assets/" + modid + "/recipes/")) {
						oldway = false;
						break;
					}
				jis.close();
			} catch (IOException e) {
			}
		} else
			oldway = false;
	}

	@Deprecated
	public static void addShapedOreRecipe(ItemStack stack, Object... input) {
		addShapedRecipe(stack, input);
	}

	public static void addShapedRecipe(ItemStack stack, Object... input) {
		RecipeHelper rh = getHelper();
		ResourceLocation rl = name(stack, input);
		if (Arrays.stream(input).anyMatch(o -> o instanceof Collection))
			addRecipe(rl, new ShapedRecipeExt(rl, stack, input));
		else if (Arrays.stream(input).anyMatch(o -> o instanceof String && OreDictionary.doesOreNameExist((String) o))) {
			if (rh.oldway)
				addRecipe(rl, new ShapedOreRecipe(rl, stack, input));
			else
				rh.addRecipe(rl, true, true, stack, input);
		} else {
			if (rh.oldway) {
				ShapedPrimer sp = CraftingHelper.parseShaped(input);
				addRecipe(rl, new ShapedRecipes("", sp.width, sp.height, sp.input, stack));
			} else
				rh.addRecipe(rl, true, false, stack, input);
		}
	}

	@Deprecated
	public static void addShapelessOreRecipe(ItemStack stack, Object... input) {
		addShapelessRecipe(stack, input);
	}

	public static void addShapelessRecipe(ItemStack stack, Object... input) {
		RecipeHelper rh = getHelper();
		ResourceLocation rl = name(stack, input);
		if (Arrays.stream(input).anyMatch(o -> o instanceof Collection))
			addRecipe(rl, new ShapelessRecipeExt(rl, stack, input));
		else if (Arrays.stream(input).anyMatch(o -> o instanceof String && OreDictionary.doesOreNameExist((String) o))) {
			if (rh.oldway)
				addRecipe(rl, new ShapelessOreRecipe(rl, stack, input));
			else
				rh.addRecipe(rl, false, true, stack, input);
		} else {
			if (rh.oldway)
				addRecipe(rl, new ShapelessRecipes("", stack, NonNullList.<Ingredient> from(Ingredient.EMPTY, Lists.newArrayList(input).stream().map(o -> CraftingHelper.getIngredient(o)).filter(o -> o != null).collect(Collectors.toList()).toArray(new Ingredient[0]))));
			else
				rh.addRecipe(rl, false, false, stack, input);
		}
	}

	public static void add(IRecipe recipe) {
		Validate.isTrue(!recipe.getClass().getName().startsWith("net.minecraft"), "Use JSON instead");
		ResourceLocation rl = name(recipe.getRecipeOutput(), (Object[]) recipe.getIngredients().toArray(new Ingredient[0]));
		addRecipe(rl, recipe);
	}

	private static void addRecipe(ResourceLocation rl, IRecipe recipe) {
		Validate.isTrue(!recipe.getRecipeOutput().isEmpty() /*&& !recipe.getClass().getName().startsWith("net.minecraft")*/);
		recipe.setRegistryName(rl);
		RegistryHelper.register(recipe);
	}

	/**
	 * @author williewillus (partly)
	 */
	private void addRecipe(ResourceLocation rl, boolean shaped, boolean ore, ItemStack stack, Object... input) {
		if (!dev)
			return;
		Map<String, Object> json = Maps.newHashMap();
		if (shaped) {
			List<String> pattern = Lists.newArrayList();
			int i = 0;
			while (i < input.length && input[i] instanceof String) {
				pattern.add((String) input[i]);
				i++;
			}
			json.put("pattern", pattern);

			Map<String, Map<String, Object>> key = Maps.newHashMap();
			Character curKey = null;
			for (; i < input.length; i++) {
				Object o = input[i];
				if (o instanceof Character) {
					if (curKey != null)
						throw new IllegalArgumentException("Provided two char keys in a row");
					curKey = (Character) o;
				} else {
					if (curKey == null)
						throw new IllegalArgumentException("Providing object without a char key");
					key.put(Character.toString(curKey), serializeItem(o));
					curKey = null;
				}
			}
			json.put("key", key);
		} else {
			json.put("ingredients", Arrays.stream(input).map(o -> serializeItem(o)).collect(Collectors.toList()));
		}
		json.put("type", shaped ? (ore ? "forge:ore_shaped" : "minecraft:crafting_shaped") : (ore ? "forge:ore_shapeless" : "minecraft:crafting_shapeless"));
		json.put("result", serializeItem(stack));

		//		String suffix = stack.getItem().getHasSubtypes() ? "_" + stack.getItemDamage() : "";
		//		File f = new File(DIR, stack.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		if (!stack.isEmpty()) {
			File f = new File(DIR, rl.getResourcePath().replace('/', '|') + ".json");
			writeToFile(f, json);
		} else
			LimeLib.log.warn("ItemStack " + stack + " is empty. Can't create a recipe.");

	}

	private static ResourceLocation name(ItemStack stack, Object... input) {
		List<String> lis = Arrays.stream(input).map(o -> {
			if (o instanceof String)
				return o.toString();
			if (o instanceof Item)
				return ((Item) o).getRegistryName().getResourcePath().toString();
			if (o instanceof Block)
				return ((Block) o).getRegistryName().getResourcePath().toString();
			if (o instanceof ItemStack)
				return ((ItemStack) o).getItem().getRegistryName().getResourcePath().toString();
			if (o instanceof Ingredient)
				return Joiner.on(" ").join(Arrays.stream(((Ingredient) o).getMatchingStacks()).map(s -> s.getItem().getRegistryName().getResourcePath().toString()).sorted().collect(Collectors.toList()));
			return "";
		}).collect(Collectors.toList());
		return new ResourceLocation(Utils.getCurrentModID(), stack.getItem().getRegistryName().getResourcePath() + "/" + stack.getItemDamage() + "#" + stack.getCount() + "_" + (Math.abs(lis.hashCode()) % 9999));
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

	/**
	 * @author williewillus
	 */
	private Map<String, Object> serializeItem(Object thing) {
		if (thing instanceof Item) {
			return serializeItem(new ItemStack((Item) thing));
		}
		if (thing instanceof Block) {
			return serializeItem(new ItemStack((Block) thing));
		}
		if (thing instanceof ItemStack) {
			ItemStack stack = (ItemStack) thing;
			Map<String, Object> ret = Maps.newHashMap();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
				ret.put("data", stack.getItemDamage());
			}
			if (stack.getCount() > 1) {
				ret.put("count", stack.getCount());
			}
			if (stack.hasTagCompound()) {
				throw new IllegalArgumentException("nbt not implemented");
			}
			return ret;
		}
		if (thing instanceof String) {
			Map<String, Object> ret = Maps.newHashMap();
			USED_OD_NAMES.add((String) thing);
			ret.put("item", "#" + ((String) thing).toUpperCase());
			return ret;
		}

		throw new IllegalArgumentException("Not a block, item, stack, or od name");
	}

	/**
	 * @author williewillus
	 */
	public static void generateConstants() {
		if (!dev)
			return;
		for (RecipeHelper rh : helpers.values()) {
			List<Map<String, Object>> json = Lists.newArrayList();
			for (String s : rh.USED_OD_NAMES) {
				Map<String, Object> entry = new HashMap<>();
				entry.put("name", s.toUpperCase());
				entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
				json.add(entry);
			}
			if (!rh.USED_OD_NAMES.isEmpty()) {
				File file = new File(rh.DIR, "_constants.json");
				writeToFile(file, json);
			}
		}
	}

	private static void writeToFile(File file, Object o) {
		String newJson = Utils.getGSON().toJson(o).trim();
		String oldJson = null;
		try {
			oldJson = !file.exists() ? "" : Files.lines(file.toPath()).collect(Collectors.joining(Configuration.NEW_LINE)).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!oldJson.equals(newJson)) {
			try (FileWriter fw = new FileWriter(file)) {
				Utils.getGSON().toJson(o, fw);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class CompoundIngredient extends net.minecraftforge.common.crafting.CompoundIngredient {

		protected CompoundIngredient(Collection<Ingredient> children) {
			super(children.stream().filter(i -> i != null).collect(Collectors.toList()));
		}

	}
}
