package mrriegel.testmod;

import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TestTile extends CommonTileInventory {

	public TestTile() {
		super(10);
	}

	@Override
	public boolean openGUI(EntityPlayer player) {
		player.openGui(TestMod.mod, 0, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		super.handleMessage(player, nbt);
		for (ItemStack i : StackHelper.split(new ItemStack(Blocks.COAL_BLOCK, 44), 12))
			System.out.print(i + " ");
		System.out.println();
	}
}
