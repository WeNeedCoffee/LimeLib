package kdp.limelib.helper;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InventoryHelper {

    public static boolean hasItemHandler(TileEntity tile, Direction side) {
        if (tile == null)
            return false;
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                .isPresent() || tile instanceof IInventory;
    }

    public static boolean hasItemHandler(IBlockReader world, BlockPos pos, Direction side) {
        return hasItemHandler(world.getTileEntity(pos), side);
    }

    public static IItemHandler getItemHandler(TileEntity tile, Direction side) {
        if (tile == null)
            return null;
        LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (cap.isPresent())
            return cap.orElse(null);
        if (tile instanceof ISidedInventory)
            return new SidedInvWrapper((ISidedInventory) tile, side);
        if (tile instanceof IInventory)
            return new InvWrapper((IInventory) tile);
        return null;
    }

    public static IItemHandler getItemHandler(IBlockReader world, BlockPos pos, Direction side) {
        return getItemHandler(world.getTileEntity(pos), side);
    }

    public static int canInsert(IItemHandler inv, ItemStack stack) {
        if (inv == null || stack.isEmpty())
            return 0;
        ItemStack s = ItemHandlerHelper.insertItemStacked(inv, stack, true);
        return stack.getCount() - s.getCount();
    }

    public static boolean contains(IItemHandler inv, Predicate<ItemStack> pred) {
        if (inv == null || pred == null)
            return false;
        return IntStream.range(0, inv.getSlots()).anyMatch(i -> pred.test(inv.getStackInSlot(i)));
    }

    public static int getAmount(IItemHandler inv, Predicate<ItemStack> pred) {
        if (inv == null || pred == null)
            return 0;
        return IntStream.range(0, inv.getSlots()).mapToObj(inv::getStackInSlot).filter(s -> pred.test(s))
                .mapToInt(ItemStack::getCount).sum();
    }

    public static ItemStack extractItem(IItemHandler inv, Predicate<ItemStack> pred, int num, boolean simulate) {
        if (inv == null || pred == null)
            return ItemStack.EMPTY;
        ItemStack extracted = ItemStack.EMPTY;
        int missing = num;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (missing > 0 && pred.test(slot)) {
                ItemStack ex = inv.extractItem(i, missing, simulate);
                if (!ex.isEmpty()) {
                    if (extracted.isEmpty()) {
                        extracted = ex.copy();
                        pred = s -> ItemHandlerHelper.canItemStacksStack(s, ex);
                        missing = Math.min(missing, extracted.getMaxStackSize());
                    } else {
                        extracted.grow(ex.getCount());
                    }
                    missing -= ex.getCount();
                }
            }
        }
        return extracted;
    }

}
