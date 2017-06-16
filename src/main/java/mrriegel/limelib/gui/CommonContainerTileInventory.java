package mrriegel.limelib.gui;

import org.apache.commons.lang3.tuple.Pair;

import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

public abstract class CommonContainerTileInventory<T extends CommonTileInventory> extends CommonContainer {

	@SuppressWarnings("unchecked")
	public CommonContainerTileInventory(InventoryPlayer invPlayer, T tile) {
		super(invPlayer, Pair.<String, IInventory>of("tile", tile));
	}

	@SuppressWarnings("unchecked")
	public T getTile() {
		return (T) invs.get("tile");
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (getTile() != null)
			getTile().sync();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return getTile() != null && getTile().isUsable(playerIn);
	}

}
