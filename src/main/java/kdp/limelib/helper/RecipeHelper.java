package kdp.limelib.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import kdp.limelib.LimeLib;

public class RecipeHelper {

    private static final Map<String, List<Pair<String, String>>> recipes = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void generateFiles() {
        if (!LimeLib.DEV)
            return;
        try {
            File folder = new File("").toPath().resolve("../src/main/java/kdp/").toFile();
            final String modIDCurrentFolder = folder.list()[0];
            for (Entry<String, List<Pair<String, String>>> e : recipes.entrySet()) {
                if (!modIDCurrentFolder.equals(e.getKey())) {
                    continue;
                }
                boolean notAJar = ModList.get().getModContainerById(e.getKey()).get().getMod().getClass()
                        .getProtectionDomain().getCodeSource().getLocation() == null;
                if (notAJar) {
                    List<String> names = new ArrayList<>();
                    for (Pair<String, String> p : e.getValue()) {
                        String name = p.getLeft();
                        int i = 1;
                        while (names.contains(name)) {
                            name = p.getLeft() + i++;
                        }
                        names.add(name);
                    }
                    File dir = new File("").toPath().resolve("../src/main/resources/data/" + e.getKey() + "/recipes/")
                            .toFile();
                    if (!dir.exists())
                        dir.mkdirs();
                    for (int i = 0; i < names.size(); i++) {
                        Files.write(new File(dir, names.get(i) + ".json").toPath(),
                                e.getValue().get(i).getRight().getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object serializeItem2(Object o, boolean count) {
        Objects.requireNonNull(o);
        if (o instanceof String) {
            Map<String, Object> ret = new LinkedHashMap<>();
            ret.put("tag", new ResourceLocation((String) o).toString());
            return ret;
        }
        if (o instanceof ResourceLocation) {
            Map<String, Object> ret = new LinkedHashMap<>();
            ret.put("tag", o.toString());
            return ret;
        }
        if (o instanceof Item) {
            Map<String, Object> ret = new LinkedHashMap<>();
            ret.put("item", ((Item) o).getRegistryName().toString());
            return ret;
        }
        if (o instanceof Block) {
            Map<String, Object> ret = new LinkedHashMap<>();
            ret.put("item", ((Block) o).getRegistryName().toString());
            return ret;
        }
        if (o instanceof ItemStack) {
            ItemStack s = (ItemStack) o;
            Validate.isTrue(!s.isEmpty(), "ItemStack is empty.");
            Map<String, Object> ret = new LinkedHashMap<>();
            ret.put("item", s.getItem().getRegistryName().toString());
            if (count && s.getCount() > 1)
                ret.put("count", s.getCount());
            return ret;
        }
        if (o instanceof Collection) {
            return ((Collection<?>) o).stream().map(oo -> serializeItem2(oo, count)).collect(Collectors.toList());
        }
        if (o instanceof Object[]) {
            return Arrays.stream((Object[]) o).map(oo -> serializeItem2(oo, count)).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Argument of type " + o.getClass().getName() + " is invalid.");

    }

    private static void validate(ItemStack stack) {
        Validate.isTrue(!stack.isEmpty(), "result must not be empty");
        //		Validate.isTrue(Loader.instance().hasReachedState(LoaderState.INITIALIZATION), "register after preInit");
    }

    public static void addCraftingRecipe(ItemStack result, @Nullable String group, boolean shaped, Object... input) {
        if (!LimeLib.DEV)
            return;
        validate(result);
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("type", shaped ? "minecraft:crafting_shaped" : "minecraft:crafting_shapeless");
        if (!StringUtils.isNullOrEmpty(group))
            json.put("group", group);
        if (shaped) {
            List<String> pattern = new ArrayList<>();
            int i = 0;
            while (i < input.length && input[i] instanceof String) {
                pattern.add((String) input[i]);
                i++;
            }
            json.put("pattern", pattern);

            Map<String, Object> key = new LinkedHashMap<>();
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
                    key.put(Character.toString(curKey), serializeItem2(o, false));
                    curKey = null;
                }
            }
            json.put("key", key);
        } else {
            json.put("ingredients",
                    Arrays.stream(input).map(o -> serializeItem2(o, false)).collect(Collectors.toList()));
        }
        json.put("result", serializeItem2(result, true));
        addRecipe(result.getItem().getRegistryName().getPath(), json);
    }

    public static void addSmeltingRecipe(ItemStack result, Object input, double exp, int time) {
        if (!LimeLib.DEV)
            return;
        validate(result);
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("type", "smelting");
        json.put("ingredient", serializeItem2(input, false));
        json.put("result", result.getItem().getRegistryName().toString());
        json.put("experience", exp);
        json.put("cookingtime", time);
        addRecipe(result.getItem().getRegistryName().getPath(), json);
    }

    public static void addRecipe(String name, Map<String, Object> json) {
        String id = ModLoadingContext.get().getActiveContainer().getNamespace();
        recipes.compute(id, (k, v) -> {
            Pair<String, String> p = Pair.of(name, gson.toJson(json));
            if (v == null) {
                return new ArrayList<>(Collections.singletonList(p));
            } else {
                v.add(p);
                return v;
            }
        });
    }

}
