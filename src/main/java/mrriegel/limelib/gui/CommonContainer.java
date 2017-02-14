package mrriegel.limelib.gui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import mrriegel.limelib.gui.slot.SlotGhost;
import mrriegel.limelib.gui.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class CommonContainer extends Container {

	protected InventoryPlayer invPlayer;
	protected Map<String, IInventory> invs;

	public CommonContainer(InventoryPlayer invPlayer, Pair<String, IInventory>... invs) {
		this.invPlayer = invPlayer;
		this.invs = Maps.newHashMap();
		if (invs != null)
			for (Pair<String, IInventory> e : invs) {
				if (e != null)
					this.invs.put(e.getLeft(), e.getRight());
			}
		modifyInvs();
		initSlots();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public EntityPlayer getPlayer() {
		return invPlayer.player;
	}

	protected abstract void initSlots();

	protected void modifyInvs() {
	}

	protected abstract List<Area> allowedSlots(ItemStack stack, IInventory inv, int index);

	protected Area getAreaForEntireInv(IInventory inv) {
		return getAreaForInv(inv, 0, inv.getSizeInventory());
	}

	protected Area getAreaForInv(IInventory inv, int start, int total) {
		List<Integer> l = Lists.newArrayList();
		for (Slot s : inventorySlots)
			if (s.inventory == inv && s.getSlotIndex() >= start && s.getSlotIndex() < total + start)
				l.add(s.getSlotIndex());
		if (l.isEmpty())
			return null;
		Collections.sort(l);
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

	protected void initSlots(IInventory inv, int x, int y, int width, int height, int startIndex, Class<? extends Slot> clazz) {
		if (inv == null)
			return;
		for (int k = 0; k < height; ++k) {
			for (int i = 0; i < width; ++i) {
				int id = i + k * width + startIndex;
				if (id >= inv.getSizeInventory())
					break;
				if (clazz == Slot.class) {
					this.addSlotToContainer(new Slot(inv, id, x + i * 18, y + k * 18) {
						@Override
						public void onSlotChanged() {
							super.onSlotChanged();
							inventoryChanged();
						}
					});
				} else if (clazz == SlotGhost.class) {
					this.addSlotToContainer(new SlotGhost(inv, id, x + i * 18, y + k * 18) {
						@Override
						public void onSlotChanged() {
							super.onSlotChanged();
							inventoryChanged();
						}
					});
				} else if (clazz == SlotOutput.class) {
					this.addSlotToContainer(new SlotOutput(inv, id, x + i * 18, y + k * 18) {
						@Override
						public void onSlotChanged() {
							super.onSlotChanged();
							inventoryChanged();
						}
					});
				}
			}
		}
	}

	protected void initSlots(IInventory inv, int x, int y, int width, int height, int startIndex) {
		initSlots(inv, x, y, width, height, startIndex, Slot.class);
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

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (playerIn.world.isRemote)
			return ItemStack.EMPTY;
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			List<Area> ar = allowedSlots(itemstack1, slot.inventory, slot.getSlotIndex());
			if (ar == null)
				return ItemStack.EMPTY;
			ar.removeAll(Collections.singleton(null));
			boolean merged = false;
			for (Area p : ar) {
				//				if (slot.inventory == p.inv)
				//					continue;
				Slot minSlot = getSlotFromInventory(p.inv, p.min);
				// while (minSlot == null && p.min < p.inv.getSizeInventory())
				// minSlot = getSlotFromInventory(p.inv, ++p.min);
				Slot maxSlot = getSlotFromInventory(p.inv, p.max);
				// while (maxSlot == null && p.max > 0)
				// minSlot = getSlotFromInventory(p.inv, --p.max);
				if (minSlot == null || maxSlot == null)
					continue;
				if (hasGhost(p)) {
					for (int i = p.min; i <= p.max; i++)
						if (!getSlotFromInventory(p.inv, i).getHasStack() && getSlotFromInventory(p.inv, i) instanceof SlotGhost) {
							getSlotFromInventory(p.inv, i).putStack(ItemHandlerHelper.copyStackWithSize(itemstack1, 1));
							detectAndSendChanges();
							return ItemStack.EMPTY;
						}
				}
				if (this.mergeItemStack(itemstack1, minSlot.slotNumber, maxSlot.slotNumber + 1, false)) {
					merged = true;
					break;
				}

			}
			if (!merged)
				return ItemStack.EMPTY;
			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, itemstack1);
			detectAndSendChanges();
		}

		return itemstack;
	}

	/**
	 * @author lumien
	 */
	@Override
	public boolean mergeItemStack(ItemStack stack, int startindex, int endindex, boolean reverse) {
		boolean flag1 = false;
		int k = startindex;

		if (reverse) {
			k = endindex - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (!reverse && k < endindex || reverse && k >= startindex)) {
				slot = this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1) && slot.isItemValid(stack)) {
					int l = itemstack1.getCount() + stack.getCount();

					if (l <= stack.getMaxStackSize() && l <= slot.getSlotStackLimit()) {
						stack.setCount(0);
						itemstack1.setCount(l);
						slot.onSlotChanged();
						flag1 = true;
					} else if (itemstack1.getCount() < stack.getMaxStackSize() && itemstack1.getCount() < slot.getSlotStackLimit()) {
						stack.shrink(stack.getMaxStackSize() - itemstack1.getCount());
						itemstack1.setCount(stack.getMaxStackSize());
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if (reverse) {
					--k;
				} else {
					++k;
				}
			}
		}

		if (stack.getCount() > 0) {
			if (reverse) {
				k = endindex - 1;
			} else {
				k = startindex;
			}

			while (!reverse && k < endindex || reverse && k >= startindex) {
				slot = this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1.isEmpty() && slot.isItemValid(stack)) {
					if (1 < stack.getCount()) {
						ItemStack copy = stack.copy();
						copy.setCount(1);
						slot.putStack(copy);

						stack.shrink(1);
						flag1 = true;
						break;
					} else {
						slot.putStack(stack.copy());
						slot.onSlotChanged();
						stack.setCount(0);
						flag1 = true;
						break;
					}
				}

				if (reverse) {
					--k;
				} else {
					++k;
				}
			}
		}
		return flag1;
	}

	private final boolean hasGhost(Area area) {
		for (int i = area.min; i <= area.max; i++)
			if (getSlotFromInventory(area.inv, i) instanceof SlotGhost)
				return true;
		return false;
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
