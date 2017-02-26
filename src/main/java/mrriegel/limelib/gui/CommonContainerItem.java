package mrriegel.limelib.gui;

import java.util.List;

import mrriegel.limelib.helper.NBTStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public abstract class CommonContainerItem extends CommonContainer {

	protected ItemStack stack;

	public CommonContainerItem(InventoryPlayer invPlayer, int num) {
		super(invPlayer, Pair.<String, IInventory> of("inv", new InventoryBasic(null, false, num)));
		setStack(getPlayer());
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		readFromStack();
	}

	protected void setStack(EntityPlayer player) {
		stack = player.inventory.getCurrentItem();
	}
	
	protected IInventory getItemInventory(){
		return invs.get("inv");
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

	public void writeToStack() {
		IInventory inv = getItemInventory();
		List<ItemStack> stacks = Lists.newArrayList();
		for (int i = 0; i < inv.getSizeInventory(); i++)
			stacks.add(i, inv.getStackInSlot(i));
		NBTStackHelper.setItemStackList(stack, "items", stacks);
	}

	public void readFromStack() {
		List<ItemStack> stacks = NBTStackHelper.getItemStackList(stack, "items");
		IInventory inv = getItemInventory();
		inv.clear();
		for (int i = 0; i < Math.min(inv.getSizeInventory(), stacks.size()); i++)
			inv.setInventorySlotContents(i, stacks.get(i));
		invs.put("inv", inv);
		detectAndSendChanges();
	}

}
