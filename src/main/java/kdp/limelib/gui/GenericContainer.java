package kdp.limelib.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public abstract class GenericContainer extends Container {

    protected final PlayerInventory playerInventory;
    protected final Map<String, Object> inventories;

    public GenericContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory,
            Map<String, Object> inventories) {
        super(type, id);
        this.playerInventory = playerInventory;
        Validate.isTrue(inventories.values().stream()
                .allMatch(o -> o instanceof IInventory || o instanceof IItemHandler));
        this.inventories = inventories != null ? inventories : Collections.emptyMap();
        this.modifyInventories();
        this.initSlots();
    }

    public GenericContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory,
            Pair<String, Object>... inventories) {
        this(type, id, playerInventory,//
                inventories != null ?
                        Arrays.stream(inventories).collect(Collectors.toMap(Pair::getKey, Pair::getValue)) :
                        null);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    protected Slot addSlot(Slot slotIn) {
        Validate.isTrue(slotIn instanceof GenericSlot, "Use a GenericSlot");
        return super.addSlot(slotIn);
    }

    protected abstract void initSlots();

    protected PlayerEntity getPlayer() {
        return playerInventory.player;
    }

    protected InventoryWrapper getInvForName(String name) {
        return InventoryWrapper.of(inventories.get(name));
    }

    protected void modifyInventories() {
    }

    public void update() {
        //TODO call that

    }

    @Nullable
    protected abstract List<InventoryArea> getAllowedSlots(ItemStack stack, IInventory iInventory, int index);

    @Nullable
    protected List<InventoryArea> getAllowedSlots(ItemStack stack, IItemHandler iInventory, int index) {
        return null;
    }

    protected Stream<GenericSlot> getSlotsFor(Object inv) {
        return getAllSlots().filter(s -> inv == s.getWrapper().getInv()).map(GenericSlot.class::cast);
    }

    protected Stream<GenericSlot> getAllSlots() {
        return inventorySlots.stream().map(GenericSlot.class::cast);
    }

    public GenericSlot getSlotFromInv(Object inv, int slotIn) {
        return getSlotsFor(inv).filter(s -> s.getSlotIndex() == slotIn).findAny().orElse(null);
    }

    protected void addSlots(Object inv, int x, int y, int width, int height, int startIndex,
            GenericSlotConstructor slotConstructor, Consumer<GenericSlot> slotInitializer) {
        if (inv == null)
            return;
        InventoryWrapper wrapper = InventoryWrapper.of(inv);
        int invSize = wrapper.getSize();
        for (int h = 0; h < height; ++h) {
            for (int w = 0; w < width; ++w) {
                int index = w + h * width + startIndex;
                if (index >= invSize) {
                    break;
                }
                GenericSlot slot = slotConstructor.construct(wrapper, index, x + w * 18, y + h * 18);
                if (slot != null) {
                    slotInitializer.accept(slot);
                    addSlot(slot);
                }
            }
        }
    }

    protected void addPlayerSlots(int x, int y) {
        addSlots(playerInventory, x, y + 58, 9, 1, 0);
        addSlots(playerInventory, x, y, 9, 3, 9);
    }

    protected void addSlots(Object inv, int x, int y, int width, int height, int startIndex) {
        addSlots(inv, x, y, width, height, startIndex, (inv2, i, xp, yp) -> new GenericSlot(inv2, i, xp, yp), s -> {
        });
    }

    protected void addSlots(Object inv, int x, int y, int width, int height) {
        addSlots(inv, x, y, width, height, 0);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        GenericSlot slot = (GenericSlot) this.inventorySlots.get(index);
        if (playerIn.world.isRemote)
            return ItemStack.EMPTY;
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();

            Object inv = slot.getWrapper().getInv();
            List<InventoryArea> ar = inv instanceof IInventory ?
                    getAllowedSlots(itemstack1, (IInventory) inv, slot.getSlotIndex()) :
                    inv instanceof IItemHandler ? getAllowedSlots(itemstack1, (IInventory) inv, index) : null;
            if (ar == null || ar.isEmpty()) {
                return ItemStack.EMPTY;
            }

            itemstack = itemstack1.copy();

            boolean merged = false;
            for (InventoryArea area : ar) {
                if (area == null || slot.getWrapper().getInv() == area.getInv()) {
                    continue;
                }
                GenericSlot minSlot = getSlotFromInv(area.getInv(), area.getMin());
                GenericSlot maxSlot = getSlotFromInv(area.getInv(), area.getMax());
                if (minSlot == null || maxSlot == null)
                    continue;
                if (area.slots(this).anyMatch(GenericSlot::isGhost)) {
                    for (GenericSlot s : (Iterable<? extends GenericSlot>) (() -> area.slots(this).iterator())) {
                        if (!s.getHasStack() && s.isGhost()) {
                            s.putStack(ItemHandlerHelper.copyStackWithSize(itemstack1, 1));
                            detectAndSendChanges();
                            return ItemStack.EMPTY;
                        }
                    }
                }

                if (this.mergeItemStack(itemstack1, minSlot.slotNumber, maxSlot.slotNumber + 1, area.isReverse())) {
                    merged = true;
                    slot.onSlotChange(itemstack1, itemstack);
                    break;
                }

            }
            if (!merged)
                return ItemStack.EMPTY;
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
            detectAndSendChanges();
        }
        return itemstack;
    }

    public static IInventory of(IItemHandler itemHandler) {
        return new IInventory() {
            @Override
            public int getSizeInventory() {
                return itemHandler.getSlots();
            }

            @Override
            public boolean isEmpty() {
                return IntStream.range(0, itemHandler.getSlots())
                        .allMatch(i -> itemHandler.getStackInSlot(i).isEmpty());
            }

            @Override
            public ItemStack getStackInSlot(int index) {
                return itemHandler.getStackInSlot(index);
            }

            @Override
            public ItemStack decrStackSize(int index, int count) {
                if (count <= 0) {
                    return ItemStack.EMPTY;
                }
                if (itemHandler instanceof IItemHandlerModifiable) {
                    ItemStack current = itemHandler.getStackInSlot(index);
                    if (current.isEmpty()) {
                        return current;
                    } else {
                        ItemStack newIn = ItemHandlerHelper.copyStackWithSize(current, current.getCount() - count);
                        ((IItemHandlerModifiable) itemHandler).setStackInSlot(index, newIn);
                        return ItemHandlerHelper.copyStackWithSize(current, current.getCount() - newIn.getCount());
                    }
                }
                return itemHandler.extractItem(index, count, false);
            }

            @Override
            public ItemStack removeStackFromSlot(int index) {
                return decrStackSize(index, 64);
            }

            @Override
            public void setInventorySlotContents(int index, ItemStack stack) {
                if (itemHandler instanceof IItemHandlerModifiable) {
                    ((IItemHandlerModifiable) itemHandler).setStackInSlot(index, stack);
                } else {
                    itemHandler.extractItem(index, 64, false);
                    itemHandler.insertItem(index, stack, false);
                }

            }

            @Override
            public void markDirty() {
            }

            @Override
            public boolean isUsableByPlayer(PlayerEntity player) {
                return true;
            }

            @Override
            public void clear() {
                IntStream.range(0, itemHandler.getSlots()).forEach(this::removeStackFromSlot);
            }

            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack) {
                return itemHandler.isItemValid(index, stack);
            }

            @Override
            public int getInventoryStackLimit() {
                return IntStream.range(0, itemHandler.getSlots()).map(itemHandler::getSlotLimit).min().orElse(0);
            }
        };
    }

    public interface GenericSlotConstructor {
        GenericSlot construct(Object inventory, int index, int x, int y);
    }

}
