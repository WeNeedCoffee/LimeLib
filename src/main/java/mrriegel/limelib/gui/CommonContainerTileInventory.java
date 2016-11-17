package mrriegel.limelib.gui;

import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import org.apache.commons.lang3.tuple.Pair;

public abstract class CommonContainerTileInventory<T extends CommonTileInventory> extends CommonContainer {

	public CommonContainerTileInventory(InventoryPlayer invPlayer, T tile) {
		super(invPlayer, Pair.<String, IInventory> of("tile", tile));
	}

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
		return getTile() != null && getTile().isUseableByPlayer(playerIn);
	}

}
