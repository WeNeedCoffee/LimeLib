package mrriegel.testmod;

import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
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
	public void handleMessage(EntityPlayerMP player, NBTTagCompound nbt) {
		super.handleMessage(player, nbt);
		ItemStack a=new ItemStack(Items.DIAMOND);
		a.setTagCompound(new NBTTagCompound());
		ItemStack b=new ItemStack(Items.DIAMOND);
		setint(a.getTagCompound(), 55);
		setint(b.getTagCompound(), 55);
		System.out.println(a.getTagCompound());
		System.out.println(b.getTagCompound());
		
	}
	
	void setint(NBTTagCompound k,int r){
		if(k==null)
			k=new NBTTagCompound();
		k.setInteger("no", r);
	}
}
