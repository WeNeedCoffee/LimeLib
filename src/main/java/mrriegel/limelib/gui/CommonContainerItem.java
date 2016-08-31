package mrriegel.limelib.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class CommonContainerItem extends CommonContainer {

	ItemStack stack;

	public CommonContainerItem(InventoryPlayer invPlayer, int num) {
		super(invPlayer, InvEntry.of("inv", new InventoryBasic(null, false, num)));
		stack = invPlayer.getCurrentItem();
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		readFromStack();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return stack.isItemEqual(playerIn.inventory.getCurrentItem());
	}

	@Override
	protected void inventoryChanged() {
		writeToStack();
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		inventoryChanged();
	}

	protected void writeToStack() {
		IInventory inv = invs.get("inv");
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			if (inv.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				inv.getStackInSlot(i).writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		stack.getTagCompound().setTag("Items", nbttaglist);
		invPlayer.mainInventory[invPlayer.currentItem] = stack;
	}

	protected void readFromStack() {
		NBTTagCompound compound = stack.getTagCompound().getCompoundTag("items");
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		IInventory inv = new InventoryBasic(null, false, invs.get("inv").getSizeInventory());
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			if (j >= 0 && j < inv.getSizeInventory()) {
				inv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
		invs.put("inv", inv);
	}

}
