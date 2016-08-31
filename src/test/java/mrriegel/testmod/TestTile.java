package mrriegel.testmod;

import net.minecraft.entity.player.EntityPlayer;
import mrriegel.limelib.tile.CommonTileInventory;

public class TestTile extends CommonTileInventory {

	public TestTile() {
		super(10);
	}

	@Override
	public boolean openGUI(EntityPlayer player) {
		player.openGui(TestMod.mod, 0, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

}
