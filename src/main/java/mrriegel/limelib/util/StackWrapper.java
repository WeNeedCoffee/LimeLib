package mrriegel.limelib.util;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

import com.google.common.collect.Lists;

public class StackWrapper {
	ItemStack stack;
	int size;

	public StackWrapper(ItemStack stack, int size) {
		super();
		if (stack==null)
			throw new NullPointerException();
		this.stack = stack.copy();
		this.size = size;
	}

	private StackWrapper() {
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound c = compound.getCompoundTag("stack");
		stack = ItemStack.loadItemStackFromNBT(c);
		size = compound.getInteger("size");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag("stack", c);
		compound.setInteger("size", size);
		return compound;
	}

	@Override
	public String toString() {
		return "[" + size + "x" + stack.getItem().getUnlocalizedName() + "@" + stack.getItemDamage() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StackWrapper))
			return false;
		StackWrapper o = (StackWrapper) obj;
		return o.stack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(o.stack, stack);
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack stack) {
		if (stack==null)
			throw new NullPointerException();
		this.stack = stack.copy();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public StackWrapper copy() {
		return new StackWrapper(stack.copy(), size);
	}

	public static StackWrapper loadStackWrapperFromNBT(NBTTagCompound nbt) {
		StackWrapper wrap = new StackWrapper();
		wrap.readFromNBT(nbt);
		return wrap.getStack()!=null ? wrap : null;
	}

	public static List<ItemStack> toStackList(List<StackWrapper> list) {
		List<ItemStack> lis = Lists.newArrayList();
		for (StackWrapper s : list) {
			if (s == null || s.getStack()==null)
				continue;
			final int maxstacksize = s.getStack().getMaxStackSize();
			int stacks = s.size / maxstacksize + (s.size % maxstacksize != 0 ? 1 : 0);
			for (int i = 0; i < stacks; i++) {
				ItemStack toAdd = s.getStack().copy();
				toAdd.stackSize=s.size == maxstacksize ? maxstacksize : (i < stacks - 1 ? maxstacksize : s.size % maxstacksize);
				lis.add(toAdd);
			}
		}
		return lis;
	}

	public static List<ItemStack> toStackList(StackWrapper wrap) {
		return toStackList(Collections.singletonList(wrap));
	}

	public static List<StackWrapper> toWrapperList(List<ItemStack> list) {
		List<StackWrapper> lis = Lists.newArrayList();
		for (ItemStack s : list) {
			if (s==null)
				continue;
			boolean added = false;
			for (int i = 0; i < lis.size(); i++) {
				ItemStack stack = lis.get(i).getStack().copy();
				if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
					lis.get(i).setSize(lis.get(i).getSize() + s.stackSize);
					added = true;
					break;
				}
			}
			if (!added)
				lis.add(new StackWrapper(s, s.stackSize));
		}
		return lis;
	}

}