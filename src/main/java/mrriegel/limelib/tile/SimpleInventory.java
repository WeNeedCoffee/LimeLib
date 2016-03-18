package mrriegel.limelib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;

public class SimpleInventory implements IInventory{
	protected ItemStack[] stacks;
	protected int stackLimit;

	public SimpleInventory(int size, int stackLimit) {
		super();
		stacks = new ItemStack[size];
		this.stackLimit = stackLimit;
	}

	public SimpleInventory(int size) {
		this(size, 64);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.stacks[index] != null) {
			if (this.stacks[index].stackSize <= count) {
				ItemStack itemstack1 = this.stacks[index];
				this.stacks[index] = null;
				return itemstack1;
			} else {
				ItemStack itemstack = this.stacks[index].splitStack(count);

				if (this.stacks[index].stackSize == 0) {
					this.stacks[index] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (this.stacks[index] != null) {
			ItemStack itemstack = this.stacks[index];
			this.stacks[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stacks[index] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		stacks = new ItemStack[getSizeInventory()];
	}

	@Override
	public void markDirty() {
	}

}
