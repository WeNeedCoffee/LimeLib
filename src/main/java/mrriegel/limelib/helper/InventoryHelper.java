package mrriegel.limelib.helper;

import java.util.List;

import mrriegel.limelib.util.FilterItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

import com.google.common.collect.Lists;

public class InventoryHelper {

	public static int insertWithLeftover(IInventory inv, ItemStack stack,
			EnumFacing side) {
		if (inv instanceof ISidedInventory && side != null) {
			ISidedInventory isidedinventory = (ISidedInventory) inv;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for (int k = 0; k < aint.length && stack != null
					&& stack.stackSize > 0; ++k) {
				stack = insertStack(inv, stack, aint[k], side);
			}
		} else {
			int i = getInventorySize(inv);

			for (int j = 0; j < i && stack != null && stack.stackSize > 0; ++j) {
				stack = insertStack(inv, stack, j, side);
			}
		}

		if (stack != null && stack.stackSize == 0) {
			stack = null;
		}
		int rest = stack == null ? 0 : stack.stackSize;
		inv.markDirty();
		return rest;

	}

	public static ItemStack insertStack(IInventory inv, ItemStack stack,
			int index, EnumFacing side) {
		ItemStack itemstack = inv.getStackInSlot(index);

		if (canInsertItemInSlot(inv, stack, index, side)) {
			boolean flag = false;

			if (itemstack == null) {
				int max = Math.min(stack.getMaxStackSize(),
						inv.getInventoryStackLimit());
				if (max >= stack.stackSize) {
					inv.setInventorySlotContents(index, stack);
					stack = null;
				} else {
					inv.setInventorySlotContents(index, stack.splitStack(max));
				}
				flag = true;
			} else if (itemstack.isItemEqual(stack)
					&& itemstack.stackSize <= itemstack.getMaxStackSize()
					&& ItemStack.areItemStackTagsEqual(itemstack, stack)) {
				int max = Math.min(stack.getMaxStackSize(),
						inv.getInventoryStackLimit());
				if (max > itemstack.stackSize) {
					int i = max - itemstack.stackSize;
					int j = Math.min(stack.stackSize, i);
					stack.stackSize -= j;
					itemstack.stackSize += j;
					flag = j > 0;
				}
			}

			if (flag) {
				inv.markDirty();
			}
		}

		return stack;
	}

	private static boolean canInsertItemInSlot(IInventory inventoryIn,
			ItemStack stack, int index, EnumFacing side) {
		return !inventoryIn.isItemValidForSlot(index, stack) ? false
				: !(inventoryIn instanceof ISidedInventory)
						|| ((ISidedInventory) inventoryIn).canInsertItem(index,
								stack, side);
	}

	public static boolean canInsertComplete(IInventory inv, ItemStack stack,
			EnumFacing side) {
		return insertWithLeftover(copy(inv), stack, side) == 0;
	}

	public static int findEmptySlot(IInventory inv) {
		for (int i = 0; i < getInventorySize(inv); i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (is == null)
				return i;
			if (is.stackSize <= 0) {
				is = null;
				return i;
			}
		}
		return -1;
	}

	public static Integer[] getSlotsWith(IInventory inv, FilterItem f) {
		List<Integer> ar = Lists.newArrayList();
		for (int i = 0; i < getInventorySize(inv); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (f.match(stack)) {
				ar.add(i);
			}
		}
		return ar.toArray(new Integer[ar.size()]);
	}

	public static boolean decrStackSize(IInventory inv, int slot, int num) {
		ItemStack stack = inv.getStackInSlot(slot);
		if (stack == null || stack.stackSize < num)
			return false;
		if (stack.stackSize == num) {
			inv.setInventorySlotContents(slot, null);
		} else {
			stack.stackSize -= num;
			inv.setInventorySlotContents(slot, stack);
		}
		inv.markDirty();
		return true;
	}

	public static boolean incrStackSize(IInventory inv, int slot, int num) {
		ItemStack stack = inv.getStackInSlot(slot);
		if (stack == null
				|| stack.stackSize + num > Math.min(stack.getMaxStackSize(),
						inv.getInventoryStackLimit()))
			return false;
		stack.stackSize += num;
		inv.setInventorySlotContents(slot, stack);
		inv.markDirty();
		return true;
	}

	// public static boolean fillSlot(IInventory inv, int slot, ItemStack stack)
	// {
	// if (incrStackSize(inv, slot, stack.stackSize))
	// return true;
	// if (canFillSlot(inv, slot, stack)) {
	// inv.setInventorySlotContents(slot, stack);
	// return true;
	// }
	// return false;
	// }
	//
	// public static boolean canFillSlot(IInventory inv, int slot, ItemStack
	// stack) {
	// ItemStack s = inv.getStackInSlot(slot);
	// if (s == null)
	// return false;
	// if (!stack.isItemEqual(s) || !ItemStack.areItemStackTagsEqual(stack, s))
	// return false;
	// if (inv.getStackInSlot(slot).stackSize + stack.stackSize > Math.min(
	// stack.getMaxStackSize(), inv.getInventoryStackLimit()))
	// return false;
	// return true;
	// }
	private static int getInventorySize(IInventory inv) {
		if (inv instanceof InventoryPlayer)
			return inv.getSizeInventory() - 4;
		else
			return inv.getSizeInventory();
	}

	public static boolean IsEnoughPresent(IInventory inv, FilterItem f, int num) {
		int number = 0;
		for (int i : getSlotsWith(inv, f))
			number += inv.getStackInSlot(i).stackSize;
		return number >= num;
	}

	public static boolean consume(IInventory inv, FilterItem f, int num) {
		int rest = num;
		for (int i : getSlotsWith(inv, f)) {
			rest -= inv.getStackInSlot(i).stackSize;
			if (rest == 0) {
				inv.setInventorySlotContents(i, null);
				return true;
			} else if (rest < 0) {
				inv.setInventorySlotContents(
						i,
						StackHelper.resizeStack(inv.getStackInSlot(i),
								Math.abs(rest)));
				return true;
			} else
				inv.setInventorySlotContents(i, null);
		}
		return false;
	}

	public static IInventory copy(final IInventory inv) {
		IInventory res = new IInventory() {

			@Override
			public boolean hasCustomName() {
				return inv.hasCustomName();
			}

			@Override
			public String getName() {
				return inv.getName();
			}

			@Override
			public IChatComponent getDisplayName() {
				return inv.getDisplayName();
			}

			@Override
			public void setInventorySlotContents(int index, ItemStack stack) {
				inv.setInventorySlotContents(index, stack);
				markDirty();
			}

			@Override
			public void setField(int id, int value) {
				inv.setField(id, value);
			}

			@Override
			public ItemStack removeStackFromSlot(int index) {
				return inv.removeStackFromSlot(index);
			}

			@Override
			public void openInventory(EntityPlayer player) {
				inv.openInventory(player);

			}

			@Override
			public void markDirty() {
				inv.markDirty();
			}

			@Override
			public boolean isUseableByPlayer(EntityPlayer player) {
				return inv.isUseableByPlayer(player);
			}

			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack) {
				return inv.isItemValidForSlot(index, stack);
			}

			@Override
			public ItemStack getStackInSlot(int index) {
				return inv.getStackInSlot(index);
			}

			@Override
			public int getSizeInventory() {
				return inv.getSizeInventory();
			}

			@Override
			public int getInventoryStackLimit() {
				return inv.getInventoryStackLimit();
			}

			@Override
			public int getFieldCount() {
				return inv.getFieldCount();
			}

			@Override
			public int getField(int id) {
				return inv.getField(id);
			}

			@Override
			public ItemStack decrStackSize(int index, int count) {
				return inv.decrStackSize(index, count);
			}

			@Override
			public void closeInventory(EntityPlayer player) {
				inv.closeInventory(player);
			}

			@Override
			public void clear() {
				inv.clear();
			}
		};
		for (int i = 0; i < getInventorySize(inv); i++)
			res.setInventorySlotContents(i,
					inv.getStackInSlot(i) == null ? null : inv
							.getStackInSlot(i).copy());
		return res;
	}
}
