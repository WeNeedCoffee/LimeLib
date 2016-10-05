package mrriegel.limelib.gui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import mrriegel.limelib.gui.slot.SlotGhost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class CommonContainer extends Container {

	protected InventoryPlayer invPlayer;
	protected Map<String, IInventory> invs;

	public CommonContainer(InventoryPlayer invPlayer, InvEntry... invs) {
		this.invPlayer = invPlayer;
		this.invs = Maps.newHashMap();
		if (invs != null)
			for (InvEntry e : invs) {
				if (e != null)
					this.invs.put(e.getName(), e.getInv());
			}
		initSlots();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (Thread.currentThread().getStackTrace()[2].getClassName().equals("net.minecraft.entity.player.EntityPlayerMP"))
			onUpdate();
	}

	protected abstract void initSlots();

	protected abstract List<Area> allowedSlots(ItemStack stack, IInventory inv, int index);

	protected Area getAreaforEntire(IInventory inv) {
		List<Integer> l = Lists.newArrayList();
		for (Slot s : inventorySlots)
			if (s.inventory == inv)
				l.add(s.getSlotIndex());
		Collections.sort(l);
		if (l.isEmpty())
			return null;
		return new Area(inv, l.get(0), l.get(l.size() - 1));
	}

	protected List<Slot> getSlotsFor(IInventory inv) {
		List<Slot> slots = Lists.newArrayList();
		for (Slot s : inventorySlots)
			if (s.inventory == inv)
				slots.add(s);
		return slots;
	}

	protected void initPlayerSlots(int x, int y) {
		initSlots(invPlayer, x, y + 58, 9, 1, 0);
		initSlots(invPlayer, x, y, 9, 3, 9);
	}

	protected void initSlots(IInventory inv, int x, int y, int width, int height, int startIndex) {
		if (inv == null)
			return;
		for (int k = 0; k < height; ++k) {
			for (int i = 0; i < width; ++i) {
				int id = i + k * width + startIndex;
				if (id >= inv.getSizeInventory())
					break;
				this.addSlotToContainer(new Slot(inv, id, x + i * 18, y + k * 18) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						inventoryChanged();
					}
				});
			}
		}
	}

	protected void initSlots(IInventory inv, int x, int y, int width, int height) {
		initSlots(inv, x, y, width, height, 0);
	}

	protected void initSlots(String name, int x, int y, int width, int height, int startIndex) {
		initSlots(invs.get(name), x, y, width, height, startIndex);
	}

	protected void initSlots(String name, int x, int y, int width, int height) {
		initSlots(name, x, y, width, height, 0);
	}

	protected void inventoryChanged() {
	}

	protected void onUpdate() {
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);
		if (playerIn.worldObj.isRemote)
			return null;
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			List<Area> ar = allowedSlots(itemstack1, slot.inventory, slot.getSlotIndex());
			if (ar == null)
				return null;
			ar.removeAll(Collections.singleton(null));
			boolean merged = false;
			for (Area p : ar) {
				Slot minSlot = getSlotFromInventory(p.inv, p.min);
				// while (minSlot == null && p.min < p.inv.getSizeInventory())
				// minSlot = getSlotFromInventory(p.inv, ++p.min);
				Slot maxSlot = getSlotFromInventory(p.inv, p.max);
				// while (maxSlot == null && p.max > 0)
				// minSlot = getSlotFromInventory(p.inv, --p.max);
				if (minSlot == null || maxSlot == null)
					return null;
				if (ghosts(p)) {
					for (int i = p.min; i <= p.max; i++)
						if (!getSlotFromInventory(p.inv, i).getHasStack()) {
							getSlotFromInventory(p.inv, i).putStack(itemstack1);
							return null;
						}
				} else {
					if (this.mergeItemStack(itemstack1, minSlot.slotNumber, maxSlot.slotNumber + 1, !(slot.inventory instanceof InventoryPlayer))) {
						merged = true;
						break;
					}
				}
			}
			if (!merged)
				return null;
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	private final boolean ghosts(Area area) {
		for (int i = area.min; i <= area.max; i++)
			if (!(getSlotFromInventory(area.inv, i) instanceof SlotGhost))
				return false;
		return true;
	}

	public static class InvEntry {
		final String name;
		final IInventory inv;

		public InvEntry(String name, IInventory inv) {
			super();
			this.name = name;
			this.inv = inv;
		}

		public String getName() {
			return name;
		}

		public IInventory getInv() {
			return inv;
		}

		public static InvEntry of(String name, IInventory inv) {
			return new InvEntry(name, inv);
		}

	}

	protected static class Area {
		IInventory inv;
		int min, max;

		public Area(IInventory inv, int min, int max) {
			super();
			this.inv = inv;
			this.min = min;
			this.max = max;
		}
	}

}
