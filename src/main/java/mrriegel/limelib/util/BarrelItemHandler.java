package mrriegel.limelib.util;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class BarrelItemHandler implements IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {

	protected final int maxItems;
	protected int amount;
	protected ItemStack stack;
	protected boolean locked;

	public BarrelItemHandler(int maxItems) {
		this.maxItems = MathHelper.clamp(maxItems, 0, 2109876543);
		this.stack = ItemStack.EMPTY.copy();
		this.amount = 0;
		this.locked = false;
	}

	public int getMaxItems() {
		return maxItems;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	private int canInsert() {
		return Math.max(maxItems - amount, 0);
	}

	@Override
	public int getSlots() {
		if (stack.isEmpty() || amount == 0)
			return 1;
		int slots = amount / stack.getMaxStackSize();
		if (amount % stack.getMaxStackSize() != 0)
			slots++;
		return slots;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot >= getSlots())
			throw new IndexOutOfBoundsException();
		if (stack.isEmpty())
			return stack.copy();
		int num = -1;
		if (slot == getSlots() - 1 || amount > stack.getMaxStackSize())
			num = MathHelper.clamp(amount % stack.getMaxStackSize(), 0, stack.getMaxStackSize());
		else
			num = MathHelper.clamp(amount, 0, stack.getMaxStackSize());
		return ItemHandlerHelper.copyStackWithSize(stack, num);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		stack = stack.copy();
		if (stack.isEmpty() || !ItemHandlerHelper.canItemStacksStack(stack, this.stack))
			return stack;
		int dos = Math.min(stack.getCount(), canInsert());
		if (!simulate)
			amount += dos;
		stack.shrink(dos);
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		int ex = MathHelper.clamp(amount, 0, this.amount);
		if (!simulate)
			this.amount -= ex;
		if (this.amount == 0 && !locked)
			stack = ItemStack.EMPTY.copy();
		return ItemHandlerHelper.copyStackWithSize(stack, ex);
	}

	@Override
	public int getSlotLimit(int slot) {
		return stack.isEmpty() ? 64 : stack.getMaxStackSize();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (this.stack.isEmpty() || stack.isEmpty()) {
			this.stack = stack.copy();
			this.amount = stack.getCount();
		} else if (ItemHandlerHelper.canItemStacksStack(stack, this.stack)) {
			insertItem(slot, stack, false);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.set(nbt, "amount", amount);
		NBTHelper.set(nbt, "locked", locked);
		NBTHelper.set(nbt, "stack", stack);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		amount = NBTHelper.get(nbt, "amount", Integer.class);
		locked = NBTHelper.get(nbt, "locked", Boolean.class);
		stack = NBTHelper.get(nbt, "stack", ItemStack.class);
	}

}
