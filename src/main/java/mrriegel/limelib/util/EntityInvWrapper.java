package mrriegel.limelib.util;

import java.util.List;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

import com.google.common.collect.Lists;

@Deprecated
public class EntityInvWrapper extends InvWrapper {

	private final Entity entity;
	private static final String NAME = "entitywrapper";
	private String name;

	public EntityInvWrapper(Entity entity, int size, String name) {
		super(getInv(entity, size, name));
		this.entity = entity;
		this.name = name;
	}

	public EntityInvWrapper(Entity entity, int size) {
		this(entity, size, NAME);
	}

	private static IInventory getInv(Entity entity, int size, String name) {
		InventoryBasic inv = new InventoryBasic("null", false, size);
		List<ItemStack> lis = NBTHelper.getItemStackList(entity.getEntityData(), name);
		if (lis.size() < size) {
			List<ItemStack> l = Lists.newArrayList();
			for (int i = 0; i < size; i++)
				l.add(null);
			NBTHelper.setItemStackList(entity.getEntityData(), name, l);
			lis = Lists.newArrayList(l);
		}
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			inv.setInventorySlotContents(i, lis.get(i));
		}
		return inv;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public int getSlots() {
		return NBTHelper.getItemStackList(entity.getEntityData(), name).size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return NBTHelper.getItemStackList(entity.getEntityData(), name).get(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack s = super.insertItem(slot, stack, simulate);
		save();
		return s;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack s = super.extractItem(slot, amount, simulate);
		save();
		return s;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		super.setStackInSlot(slot, stack);
		save();
	}

	private void save() {
		List<ItemStack> l = Lists.newArrayList();
		for (int i = 0; i < getInv().getSizeInventory(); i++)
			l.add(getInv().getStackInSlot(i));
		NBTHelper.setItemStackList(this.entity.getEntityData(), name, l);
	}

}
