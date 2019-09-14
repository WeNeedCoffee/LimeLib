package kdp.limelib.util;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

import kdp.limelib.helper.nbt.NBTHelper;

public class ItemFilter implements Predicate<ItemStack>, IItemHandlerModifiable, INBTSerializable<CompoundNBT> {

    private List<ItemStack> items;
    private boolean whiteList, nbt, damage, ore, mod;

    @Override
    public int getSlots() {
        return items.size();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        setStackInSlot(slot, stack);
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        items.set(slot, stack);
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int arg0) {
        return items.get(arg0);
    }

    @Override
    public boolean test(ItemStack t) {
        return items.stream().filter(s -> !s.isEmpty()).anyMatch(s -> {
            /*
             * if(ore) { return ItemTags.ACACIA_LOGS.contains(null) }
             */
            if (mod) {
                return s.getItem().getRegistryName().getNamespace()
                        .equals(t.getItem().getRegistryName().getNamespace());
            }
            if (nbt) {
                return ItemStack.areItemStackTagsEqual(s, t);
            }
            if (damage) {
                return s.getDamage() == t.getDamage();
            }
            return s.getItem() == t.getItem();
        }) || !whiteList;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return NBTHelper.setCollection(new CompoundNBT(), "items", items);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        items = NBTHelper.getCollection(nbt, "items", ItemStack.class);
    }

}
