package mrriegel.limelib.gui;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class CommonContainerTile extends CommonContainer {

	public CommonContainerTile(InventoryPlayer invPlayer, CommonTileInventory tile) {
		super(invPlayer, InvEntry.of("tile", tile));
	}

	protected CommonTileInventory getTile() {
		return (CommonTileInventory) invs.get("tile");
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		getTile().sync();
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return getTile().isUseableByPlayer(playerIn);
	}

}
