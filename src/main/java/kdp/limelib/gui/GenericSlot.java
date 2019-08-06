package kdp.limelib.gui;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

public class GenericSlot extends Slot {
    private Predicate<ItemStack> pred;
    private boolean ghost;
    private boolean immutable;
    private ItemStack stack;
    public final IItemHandler itemHandler;
    //cached wrapper
    private InventoryWrapper wrapper;
    private Consumer<? super GenericSlot> changeConsumer;

    public GenericSlot(IInventory inventory, int index, int xPosition, int yPosition) {
        this((Object) inventory, index, xPosition, yPosition);

    }

    public GenericSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this((Object) itemHandler, index, xPosition, yPosition);
    }

    public GenericSlot(InventoryWrapper wrapper, int index, int xPosition, int yPosition) {
        super(wrapper.getInventory().orElse(null), index, xPosition, yPosition);
        this.itemHandler = wrapper.getItemHandler().orElse(null);
    }

    public GenericSlot(Object inventory, int index, int xPosition, int yPosition) {
        super(inventory instanceof IInventory ? (IInventory) inventory : null, index, xPosition, yPosition);
        this.itemHandler = this.inventory == null ? null : (IItemHandler) inventory;

    }

    public GenericSlot setFilter(Predicate<ItemStack> pred) {
        this.pred = pred;
        return this;
    }

    public GenericSlot setGhost() {
        this.ghost = true;
        return this;
    }

    public GenericSlot setImmutable(ItemStack stack) {
        this.immutable = true;
        this.stack = stack.copy();
        return this;
    }

    public GenericSlot onChange(Consumer<? super GenericSlot> changeConsumer) {
        this.changeConsumer = changeConsumer;
        return this;
    }

    public boolean isGhost() {
        return ghost;
    }

    private boolean isItemHandler() {
        return itemHandler != null;
    }

    public InventoryWrapper getWrapper() {
        return wrapper == null ? wrapper = InventoryWrapper.of(itemHandler != null ? itemHandler : inventory) : wrapper;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (immutable) {
            return false;
        }
        if (pred != null) {
            return pred.test(stack);
        }
        if (ghost) {
            putStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
            return false;
        }
        return isItemHandler() ?
                itemHandler.isItemValid(getSlotIndex(), stack) :
                inventory.isItemValidForSlot(getSlotIndex(), stack);
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (immutable) {
            return ItemStack.EMPTY;
        }
        if (ghost) {
            putStack(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
        if (isItemHandler()) {
            if (itemHandler instanceof IItemHandlerModifiable) {
                ItemStack current = itemHandler.getStackInSlot(getSlotIndex());
                ItemStack neu = ItemHandlerHelper.copyStackWithSize(current, current.getCount() - amount);
                ItemStack ret = ItemHandlerHelper.copyStackWithSize(current, current.getCount() - neu.getCount());
                ((IItemHandlerModifiable) itemHandler).setStackInSlot(getSlotIndex(), neu);
                return ret;
            } else {
                return itemHandler.extractItem(getSlotIndex(), amount, false);
            }
        }
        return super.decrStackSize(amount);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        if (immutable) {
            return false;
        }
        if (ghost) {
            ItemStack current = playerIn.inventory.getItemStack();
            if (!current.isEmpty() && isItemValid(current)) {
                current = ItemHandlerHelper.copyStackWithSize(current, 1);
                putStack(current);
            }
            return false;
        }
        return isItemHandler() ?
                !itemHandler.extractItem(getSlotIndex(), 1, true).isEmpty() :
                super.canTakeStack(playerIn);
    }

    @Override
    public void putStack(ItemStack stack) {
        if (immutable) {
            return;
        }
        if (isItemHandler()) {
            if (itemHandler instanceof IItemHandlerModifiable) {
                ((IItemHandlerModifiable) itemHandler).setStackInSlot(getSlotIndex(), stack);
            } else {
                ItemStack old = itemHandler.getStackInSlot(getSlotIndex());
                if (!old.isEmpty()) {
                    StringBuilder builder = new StringBuilder("Why is the slot not empty?" + System.lineSeparator());
                    builder.append("old: ").append(old).append(System.lineSeparator());
                    builder.append("new: ").append(stack).append(System.lineSeparator());
                    builder.append("index: ").append(getSlotIndex()).append(System.lineSeparator());
                    builder.append("inv: ").append(isItemHandler() ? itemHandler : inventory);
                    throw new IllegalArgumentException(builder.toString());
                }
                itemHandler.insertItem(getSlotIndex(), stack, false);
            }
            this.onSlotChanged();
        } else {
            super.putStack(stack);
        }
    }

    @Override
    public void onSlotChanged() {
        if (immutable) {
            return;
        }
        if (!isItemHandler()) {
            super.onSlotChanged();
        }
        if (changeConsumer != null) {
            changeConsumer.accept(this);
        }
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
        if (!isItemHandler()) {
            super.onSlotChange(p_75220_1_, p_75220_2_);
        }
    }

    @Override
    public int getSlotStackLimit() {
        if (immutable) {
            return 64;
        }
        return isItemHandler() ? itemHandler.getSlotLimit(getSlotIndex()) : super.getSlotStackLimit();
    }

    @Override
    public ItemStack getStack() {
        if (immutable) {
            return stack;
        }
        return isItemHandler() ? itemHandler.getStackInSlot(getSlotIndex()) : super.getStack();
    }

    @Override
    public boolean isSameInventory(Slot other) {
        if (other instanceof GenericSlot) {
            return isItemHandler() ? itemHandler == ((GenericSlot) other).itemHandler : inventory == other.inventory;
        }
        if (other instanceof SlotItemHandler) {
            return isItemHandler() && itemHandler == ((SlotItemHandler) other).getItemHandler();
        }
        return super.isSameInventory(other);
    }

}
