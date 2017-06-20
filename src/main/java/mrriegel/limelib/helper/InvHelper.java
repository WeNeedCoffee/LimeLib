package mrriegel.limelib.helper;

import java.util.List;
import java.util.function.Predicate;

import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.StackWrapper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InvHelper {

	public static boolean hasItemHandler(TileEntity tile, EnumFacing side) {
		if (tile == null)
			return false;
		return tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) || tile instanceof IInventory;
	}

	public static boolean hasItemHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return hasItemHandler(world.getTileEntity(pos), side);
	}

	public static IItemHandler getItemHandler(TileEntity tile, EnumFacing side) {
		if (tile == null)
			return null;
		if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
			return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		if (tile instanceof ISidedInventory)
			return new SidedInvWrapper((ISidedInventory) tile, side);
		if (tile instanceof IInventory)
			return new InvWrapper((IInventory) tile);
		return null;
	}

	public static IItemHandler getItemHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getItemHandler(world.getTileEntity(pos), side);
	}

	public static ItemStack insert(TileEntity tile, ItemStack stack, EnumFacing side) {
		if (tile == null)
			return stack;
		IItemHandler inv = getItemHandler(tile, side);
		return ItemHandlerHelper.insertItemStacked(inv, stack, false);
	}

	public static int canInsert(IItemHandler inv, ItemStack stack) {
		if (inv == null || stack.isEmpty())
			return 0;
		ItemStack s = ItemHandlerHelper.insertItemStacked(inv, stack, true);
		int rest = s.getCount();
		return stack.getCount() - rest;

	}

	public static boolean contains(IItemHandler inv, ItemStack stack) {
		if (inv == null || stack.isEmpty())
			return false;
		for (int i = 0; i < inv.getSlots(); i++) {
			if (ItemHandlerHelper.canItemStacksStack(inv.getStackInSlot(i), stack)) {
				return true;
			}
		}
		return false;
	}

	public static int getAmount(IItemHandler inv, FilterItem fil) {
		if (inv == null || fil == null)
			return 0;
		int amount = 0;
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack slot = inv.getStackInSlot(i);
			if (fil.match(slot))
				amount += slot.getCount();
		}
		return amount;
	}

	public static ItemStack extractItem(IItemHandler inv, FilterItem fil, int num, boolean simulate) {
		if (fil == null)
			return ItemStack.EMPTY;
		return extractItem(inv, (ItemStack s) -> fil.match(s), num, simulate);
	}

	public static ItemStack extractItem(IItemHandler inv, Predicate<ItemStack> pred, int num, boolean simulate) {
		if (inv == null || pred == null)
			return ItemStack.EMPTY;
		ItemStack extracted = ItemStack.EMPTY;
		int missing = num;
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack slot = inv.getStackInSlot(i);
			if (pred.test(slot)) {
				ItemStack ex = inv.extractItem(i, missing, simulate);
				if (!ex.isEmpty()) {
					if (extracted.isEmpty())
						extracted = ex.copy();
					else {
						if (!ItemHandlerHelper.canItemStacksStack(extracted, ex))
							continue;
						extracted.grow(ex.getCount());
					}
					missing -= ex.getCount();
					if (missing == 0) {
						return ItemHandlerHelper.copyStackWithSize(ex, num);
					}
				}
			}
		}
		return extracted;
	}

	public static boolean transfer(IItemHandler from, IItemHandler to, int amount, Predicate<ItemStack> pred) {
		for (int i = 0; i < from.getSlots(); i++) {
			ItemStack st = from.extractItem(i, amount, true);
			if (st.isEmpty() || !pred.test(st))
				continue;
			int ins = st.getCount() - ItemHandlerHelper.insertItemStacked(to, st, true).getCount();
			if (ins == 0)
				continue;
			ItemHandlerHelper.insertItemStacked(to, from.extractItem(i, Math.min(amount, ins), false), false);
			return true;

		}
		return false;
	}

	public static void sort(IItemHandler inv) {
		NonNullList<ItemStack> ex = StackHelper.inv2list(inv);
		List<StackWrapper> wraps = StackWrapper.toWrapperList(ex);
		NonNullList<ItemStack> lis = StackWrapper.toStackList(wraps);
		InvHelper.clear(inv);
		StackHelper.list2inv(lis, inv);
	}

	public static void clear(IItemHandler inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.getStackInSlot(i).isEmpty())
				continue;
			if (inv instanceof IItemHandlerModifiable)
				((IItemHandlerModifiable) inv).setStackInSlot(i, ItemStack.EMPTY);
			else
				inv.extractItem(i, inv.getStackInSlot(i).getCount(), false);
		}
	}

	public static boolean hasFluidHandler(TileEntity tile, EnumFacing side) {
		if (tile == null)
			return false;
		return tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
	}

	public static boolean hasFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return hasFluidHandler(world.getTileEntity(pos), side);
	}

	public static IFluidHandler getFluidHandler(TileEntity tile, EnumFacing side) {
		if (tile == null)
			return null;
		if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))
			return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		return null;
	}

	public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getFluidHandler(world.getTileEntity(pos), side);
	}

	public static boolean contains(IFluidHandler inv, FluidStack stack) {
		if (inv == null || stack == null)
			return false;
		for (IFluidTankProperties p : inv.getTankProperties()) {
			if (stack.isFluidEqual(p.getContents())) {
				return true;
			}
		}
		return false;
	}

	public static int getAmount(IFluidHandler inv, FluidStack stack) {
		if (inv == null || stack == null)
			return 0;
		int amount = 0;
		for (IFluidTankProperties p : inv.getTankProperties()) {
			if (stack.isFluidEqual(p.getContents())) {
				amount += p.getContents().amount;
			}
		}
		return amount;
	}

	public static void clear(IFluidHandler inv) {
		if (inv == null)
			return;
		for (IFluidTankProperties p : inv.getTankProperties()) {
			p.canDrain();
			inv.drain(Integer.MAX_VALUE, true);
		}
	}

}
