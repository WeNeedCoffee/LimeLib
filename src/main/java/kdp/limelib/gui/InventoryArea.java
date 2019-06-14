package kdp.limelib.gui;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.IItemHandler;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class InventoryArea {
    private final Object inv;
    private final int min, max;
    private final boolean reverse;

    public InventoryArea(Object inv, int min, int max, boolean reverse) {
        super();
        this.inv = inv;
        this.min = min;
        this.max = max;
        this.reverse = reverse;
    }

    public InventoryArea(Object inv, int min, int max) {
        this(inv, min, max, false);
    }

    @Override
    public String toString() {
        return "Area [inv=" + inv + ", min=" + min + ", max=" + max + "]";
    }

    public Object getInv() {
        return inv;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean isReverse() {
        return reverse;
    }

    public Stream<GenericSlot> slots(GenericContainer container) {
        return IntStream.range(min, max + 1).mapToObj(i -> container.getSlotFromInv(inv, i));
    }

    public static InventoryArea getAreaForEntireInv(GenericContainer container, IInventory inv) {
        return getAreaForInv(container, inv, 0, inv.getSizeInventory());
    }

    public static InventoryArea getAreaForEntireInv(GenericContainer container, IItemHandler inv) {
        return getAreaForInv(container, inv, 0, inv.getSlots());
    }

    public static InventoryArea getAreaForInv(GenericContainer container, Object inv, int start, int total) {
        IntArrayList l = container.getAllSlots()
                .filter(s -> inv == s.getWrapper().getInv() && s.getSlotIndex() >= start && s
                        .getSlotIndex() < total + start).mapToInt(Slot::getSlotIndex).sorted()
                .collect(IntArrayList::new, IntArrayList::add, IntArrayList::addAll);
        return l.isEmpty() ? null : new InventoryArea(inv, l.getInt(0), l.getInt(l.size() - 1));
    }

    public static InventoryArea getAreaForInv(GenericContainer container, IInventory inv, int start, int total) {
        return getAreaForInv(container, (Object) inv, start, total);
    }

    public static InventoryArea getAreaForInv(GenericContainer container, IItemHandler inv, int start, int total) {
        return getAreaForInv(container, (Object) inv, start, total);
    }
}
