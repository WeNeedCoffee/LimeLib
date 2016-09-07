package mrriegel.testmod;

import java.util.List;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

public class TestContainer extends CommonContainerTile {

	public TestContainer(InventoryPlayer invPlayer, CommonTileInventory tile) {
		super(invPlayer, tile);
	}

	@Override
	protected void initSlots() {
		initPlayerSlots(20, 100);
		initSlots(getTile(), 65, 20, 3, 3, 0);
	}

	@Override
	protected List<Area> allowedSlots(ItemStack stack, IInventory inv, int index) {
		List<Area> lis = Lists.newArrayList();
		IInventory inv2 = inv instanceof InventoryPlayer ? getTile() : invPlayer;
		Area x = getAreaforEntire(inv2);
		if (x != null)
			lis.add(x);
		// lis.add(new Area(inv2, 0, inv2.getSizeInventory()-1));
		return lis;
	}

}
