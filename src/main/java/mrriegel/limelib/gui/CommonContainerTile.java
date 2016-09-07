package mrriegel.limelib.gui;

import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public abstract class CommonContainerTile<T extends CommonTileInventory> extends CommonContainer {

	public CommonContainerTile(InventoryPlayer invPlayer, T tile) {
		super(invPlayer, InvEntry.of("tile", tile));
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
