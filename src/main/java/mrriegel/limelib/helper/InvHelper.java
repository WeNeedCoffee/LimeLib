package mrriegel.limelib.helper;

import mrriegel.limelib.util.FilterItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
	public static boolean hasItemHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getItemHandler(world, pos, side) != null;
	}

	public static boolean hasItemHandler(TileEntity tile, EnumFacing facing) {
		return getItemHandler(tile, facing) != null;
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
		return getItemHandler(WorldHelper.getTile(world, pos), side);
	}

	public static ItemStack insert(TileEntity tile, ItemStack stack, EnumFacing side) {
		if (tile == null)
			return stack;
		IItemHandler inv = getItemHandler(tile, side);
		return ItemHandlerHelper.insertItemStacked(inv, stack, false);
	}

	public static int canInsert(IItemHandler inv, ItemStack stack) {
		if (inv == null || stack == null)
			return 0;
		ItemStack s = ItemHandlerHelper.insertItemStacked(inv, stack, true);
		int rest = s == null ? 0 : s.stackSize;
		return stack.stackSize - rest;

	}

	public static boolean contains(IItemHandler inv, ItemStack stack) {
		if (inv == null || stack == null)
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
				amount += slot.stackSize;
		}
		return amount;
	}

	public static ItemStack extractItem(IItemHandler inv, FilterItem fil, int num, boolean simulate) {
		if (inv == null || fil == null)
			return null;
		ItemStack extracted = null;
		int missing = num;
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack slot = inv.getStackInSlot(i);
			if (fil.match(slot)) {
				ItemStack ex = inv.extractItem(i, missing, simulate);
				if (ex != null) {
					if (extracted == null)
						extracted = ex.copy();
					else {
						if (!ItemHandlerHelper.canItemStacksStack(extracted, ex))
							continue;
					}
					missing -= ex.stackSize;
					if (missing == 0)
						return ItemHandlerHelper.copyStackWithSize(slot, num);
				}
			}
		}
		return null;
	}

	public static void clear(IItemHandler inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.getStackInSlot(i) == null)
				continue;
			if (inv instanceof IItemHandlerModifiable)
				((IItemHandlerModifiable) inv).setStackInSlot(i, null);
			else
				inv.extractItem(i, inv.getStackInSlot(i).stackSize, false);
		}
	}

	public static boolean hasFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getFluidHandler(world, pos, side) != null;
	}

	public static boolean hasFluidHandler(TileEntity tile, EnumFacing facing) {
		return getFluidHandler(tile, facing) != null;
	}

	public static IFluidHandler getFluidHandler(TileEntity tile, EnumFacing side) {
		if (tile == null)
			return null;
		if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))
			return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		return null;
	}

	public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getFluidHandler(WorldHelper.getTile(world, pos), side);
	}

	// public static FluidStack insert(TileEntity tile, FluidStack stack,
	// EnumFacing side) {
	// if (tile == null)
	// return stack;
	// IFluidHandler inv = getFluidHandler(tile, side);
	// inv.fill(stack, true);
	// return ItemHandlerHelper.insertItemStacked(inv, stack, false);
	// }
	//
	// public static int canInsert(IItemHandler inv, FluidStack stack) {
	// if (inv == null || stack == null)
	// return 0;
	// ItemStack s = ItemHandlerHelper.insertItemStacked(inv, stack, true);
	// int rest = s == null ? 0 : s.stackSize;
	// return stack.stackSize - rest;
	//
	// }

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

	// public static FluidStack extractItem(IFluidHandler inv, FilterItem fil,
	// int num, boolean simulate) {
	// if (inv == null || fil == null)
	// return null;
	// ItemStack extracted = null;
	// int missing = num;
	// for (int i = 0; i < inv.getSlots(); i++) {
	// ItemStack slot = inv.getStackInSlot(i);
	// if (fil.match(slot)) {
	// ItemStack ex = inv.extractItem(i, missing, simulate);
	// if (ex != null) {
	// if (extracted == null)
	// extracted = ex.copy();
	// else {
	// if (!ItemHandlerHelper.canItemStacksStack(extracted, ex))
	// continue;
	// }
	// missing -= ex.stackSize;
	// if (missing == 0)
	// return ItemHandlerHelper.copyStackWithSize(slot, num);
	// }
	// }
	// }
	// return null;
	// }

	public static void clear(IFluidHandler inv) {
		if (inv == null)
			return;
		for (IFluidTankProperties p : inv.getTankProperties()) {
			p.canDrain();
			inv.drain(Integer.MAX_VALUE, true);
		}
	}

}
