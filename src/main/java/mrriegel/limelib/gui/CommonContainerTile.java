package mrriegel.limelib.gui;

import mrriegel.limelib.tile.CommonTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import org.apache.commons.lang3.tuple.Pair;

public abstract class CommonContainerTile<T extends CommonTile> extends CommonContainer {

	protected T tile;

	public CommonContainerTile(InventoryPlayer invPlayer, Pair<String, IInventory>[] invs, T tile) {
		super(invPlayer, invs);
		this.tile = tile;
	}

	public T getTile() {
		return tile;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (tile != null)
			tile.sync();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return tile != null && tile.isUseableByPlayer(playerIn);
	}

}
