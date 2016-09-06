package mrriegel.testmod;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.google.common.collect.Lists;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.tile.CommonTile;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TestTile extends CommonTileInventory {

	public TestTile() {
		super(10);
	}

	@Override
	public boolean openGUI(EntityPlayerMP player) {
		player.openGui(TestMod.mod, 0, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void handleMessage(EntityPlayerMP player, NBTTagCompound nbt) {
		super.handleMessage(player, nbt);
		System.out.println("isOre: " + BlockHelper.isOre(worldObj, pos.up()) + " ");

	}

	<T> T T(T T) {
		return T;
	}
}
