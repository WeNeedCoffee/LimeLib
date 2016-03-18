package mrriegel.limelib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IChatComponent;

public abstract class BasicTEInventory extends BasicTE implements IInventory {
	protected ItemStack[] stacks;
	protected int stackLimit;

	public BasicTEInventory(int size, int stackLimit) {
		super();
		stacks = new ItemStack[size];
		this.stackLimit = stackLimit;
	}

	public BasicTEInventory(int size) {
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
		return this.worldObj.getTileEntity(this.pos) != this ? false : player
				.getDistanceSq((double) this.pos.getX() + 0.5D,
						(double) this.pos.getY() + 0.5D,
						(double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public abstract boolean isItemValidForSlot(int index, ItemStack stack);

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
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.stackLimit = compound.getInteger("limit");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("limit", stackLimit);
	}

	@Override
	public void readSyncableDataFromNBT(NBTTagCompound compound) {
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.stacks = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");
			if (j >= 0 && j < this.stacks.length) {
				this.stacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound compound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < this.stacks.length; ++i) {
			if (this.stacks[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.stacks[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		compound.setTag("Items", nbttaglist);
	}

}
