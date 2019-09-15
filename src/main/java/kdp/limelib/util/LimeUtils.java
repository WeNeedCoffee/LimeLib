package kdp.limelib.util;

import java.lang.reflect.Type;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class LimeUtils {

    private static GsonBuilder gsonBuilder;
    private static Gson gson;

    public static void init() {
        //dummy
        LimeUtils.class.hashCode();
    }

    public static Gson getGSON() {
        if (gson != null)
            return gson;
        if (gsonBuilder == null)
            registerDefaultAdapters();
        return gson = gsonBuilder.create();
    }

    private static void registerDefaultAdapters() {
        gsonBuilder = new GsonBuilder().setPrettyPrinting().//
                registerTypeAdapter(CompoundNBT.class, new TypeAdapters.NBTAdapter()).//
                registerTypeAdapter(ItemStack.class, new TypeAdapters.ItemStackAdapter());
        gson = null;
    }

    public static void registerGsonAdapter(Type type, Object adapter) {
        getGSON();
        gsonBuilder.registerTypeAdapter(type, adapter);
        gson = null;
    }

    public static <T> T orElse(T value, T default_) {
        return value != null ? value : default_;
    }

    public static <T, R> R orElse(T value, R default_, Function<T, R> mapper) {
        return value != null ? mapper.apply(value) : default_;
    }

}
