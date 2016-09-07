package mrriegel.testmod;

import java.util.List;
import java.util.UUID;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.collect.Lists;

public class TestTile extends CommonTileInventory implements ITickable {

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
		int range = 12;
		System.out.println(UUID.randomUUID());
		if (!InvHelper.hasItemHandler(worldObj.getTileEntity(pos.up()), EnumFacing.DOWN))
			return;
		List<BlockPos> lis = Lists.newArrayList();
		for (int y = pos.getY() - 1; y > 0; y--)
			for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
					if (BlockHelper.isOre(worldObj, new BlockPos(x, y, z)))
						lis.add(new BlockPos(x, y, z));
		for (BlockPos p : lis) {
			for (ItemStack s : BlockHelper.breakBlockWithFortune(worldObj, p, 0, player, false, false))
				if (ItemHandlerHelper.insertItem(InvHelper.getItemHandler(worldObj.getTileEntity(pos.up()), EnumFacing.DOWN), s.copy(), false) != null)
					return;
		}
	}

	List<BlockPos> lis = null;

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		int range = 9;
		if (lis == null) {
			lis = Lists.newArrayList();
			for (int y = pos.getY() - 1; y > 0; y--)
				for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
					for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
						lis.add(new BlockPos(x, y, z));

		}
		EntityPlayer player = worldObj.playerEntities.isEmpty() ? null : worldObj.playerEntities.get(0);
		if (worldObj.getTotalWorldTime() % 1 == 0 && worldObj.isBlockPowered(pos) && player != null) {
			for (BlockPos p : lis)
				if (worldObj.getTileEntity(p) == null && BlockHelper.isBlockBreakable(worldObj, p)) {
					List<ItemStack> drops = BlockHelper.breakBlockWithFortune(worldObj, p, 0, null, false, false);
					for (ItemStack drop : drops)
						if (drop != null)
							ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(player.inventory), drop.copy(), false);
					break;
				}
		}
	}
}
