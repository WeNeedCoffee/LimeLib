package mrriegel.limelib.gui;

import mrriegel.limelib.util.ItemInvWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.tuple.Pair;

public abstract class CommonContainerItem extends CommonContainer {

	protected ItemStack stack;

	public CommonContainerItem(InventoryPlayer invPlayer, int num) {
		super(invPlayer, Pair.<String, IInventory> of("inv", new InventoryBasic(null, false, num)));
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
		ItemInvWrapper w = new ItemInvWrapper(stack, inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++)
			w.setStackInSlot(i, inv.getStackInSlot(i));
		invPlayer.mainInventory[invPlayer.currentItem] = stack;
	}

	protected void readFromStack() {
		IInventory inv = new InventoryBasic(null, false, invs.get("inv").getSizeInventory());
		ItemInvWrapper w = new ItemInvWrapper(stack, inv.getSizeInventory());
		for (int i = 0; i < inv.getSizeInventory(); i++)
			inv.setInventorySlotContents(i, w.getStackInSlot(i));
		invs.put("inv", inv);
	}

}
