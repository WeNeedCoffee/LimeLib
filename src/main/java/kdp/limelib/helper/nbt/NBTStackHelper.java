package kdp.limelib.helper.nbt;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

public class NBTStackHelper {

    public static boolean hasTag(ItemStack stack, String key) {
        return NBTHelper.hasTag(stack.getTag(), key);
    }

    public static CompoundNBT removeTag(ItemStack stack, String key) {
        return NBTHelper.removeTag(stack.getTag(), key);
    }

    public static <T extends INBT> Optional<T> getTagOptional(ItemStack stack, String key) {
        return NBTHelper.getTagOptional(stack.getTag(), key);
    }

    public static <T extends INBT> Optional<T> getTagOptional(ItemStack stack, String key, Class<T> clazz) {
        return NBTHelper.getTagOptional(stack.getTag(), key, clazz);
    }

    public static <T> T get(ItemStack stack, String key, Class<T> clazz) {
        return NBTHelper.get(stack.getTag(), key, clazz);
    }

    public static <T> Optional<T> getOptional(ItemStack stack, String key, Class<T> clazz) {
        return NBTHelper.getOptional(stack.getTag(), key, clazz);
    }

    public static <T> CompoundNBT set(ItemStack stack, String key, T value) {
        return NBTHelper.set(stack.getOrCreateTag(), key, value);
    }

    public static <T, C extends Collection<T>> C getCollection(ItemStack stack, String key, Class<T> clazz) {
        return NBTHelper.getCollection(stack.getTag(), key, clazz);
    }

    public static <T, C extends Collection<T>> C getCollection(ItemStack stack, String key, Class<T> clazz,
            Supplier<C> supplier) {
        return NBTHelper.getCollection(stack.getTag(), key, clazz, supplier);
    }

    public static <T> CompoundNBT setCollection(ItemStack stack, String key, Collection<T> values) {
        return NBTHelper.setCollection(stack.getOrCreateTag(), key, values);
    }

    public static <K, V> Map<K, V> getMap(ItemStack stack, String key, Class<K> keyClazz, Class<V> valClazz) {
        return NBTHelper.getMap(stack.getTag(), key, keyClazz, valClazz);
    }

    public static <K, V> Map<K, V> getMap(ItemStack stack, String key, Class<K> keyClazz, Class<V> valClazz,
            Supplier<Map<K, V>> supplier) {
        return NBTHelper.getMap(stack.getTag(), key, keyClazz, valClazz, supplier);
    }

    public static <K, V> CompoundNBT setMap(ItemStack stack, String key, Map<K, V> values) {
        return NBTHelper.setMap(stack.getOrCreateTag(), key, values);

    }

}
