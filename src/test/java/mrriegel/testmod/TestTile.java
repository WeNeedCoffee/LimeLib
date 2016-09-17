package mrriegel.testmod;

import java.util.List;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.tile.CommonTileInventory;
import mrriegel.limelib.tile.IDataKeeper;
import mrriegel.limelib.tile.IOwneable;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.collect.Lists;

public class TestTile extends CommonTileInventory implements ITickable, IDataKeeper, IOwneable {

	public int k;

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
		lis = null;
		sync();
		int range = 8;
		if (NBTHelper.hasTag(nbt, "k"))
			k = NBTHelper.getInt(nbt, "k");
		if (!InvHelper.hasItemHandler(worldObj.getTileEntity(pos.up()), EnumFacing.DOWN))
			return;
		List<BlockPos> lis = Lists.newArrayList();
		for (int y = pos.getY() - 1; y > 0; y--)
			for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
					if (!BlockHelper.isOre(worldObj, new BlockPos(x, y, z)))
						lis.add(new BlockPos(x, y, z));
		for (BlockPos p : lis) {
			for (ItemStack s : BlockHelper.breakBlockWithFortune(worldObj, p, 3, player, false, false))
				if (ItemHandlerHelper.insertItem(InvHelper.getItemHandler(worldObj.getTileEntity(pos.up()), EnumFacing.DOWN), s.copy(), false) != null)
					return;
		}
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		System.out.println("onData");
	};

	List<BlockPos> lis = null;

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		int range = 12;
		if (lis == null) {
			lis = Lists.newArrayList();
			for (int y = pos.getY() - 1; y > 0; y--)
				for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
					for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
						lis.add(new BlockPos(x, y, z));

		}
		EntityPlayer player = Utils.getRandomPlayer(worldObj);
		if (worldObj.getTotalWorldTime() % 1 == 0 && worldObj.isBlockPowered(pos) && player != null) {
			for (int i = 0; i < 5; i++)
				try {
					for (BlockPos p : lis) {
						if (worldObj.getTileEntity(p) == null && BlockHelper.isBlockBreakable(worldObj, p) && !BlockHelper.isOre(worldObj, p)) {
							List<ItemStack> drops = BlockHelper.breakBlockWithFortune(worldObj, p, 0, null, false, false);
							drops.clear();
							for (ItemStack drop : drops)
								if (drop != null)
									ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(player.inventory), drop.copy(), false);
							break;
						}
						lis.remove(p);
					}
				} catch (Exception e) {
					// System.out.println(e.getClass());
				}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		k = NBTHelper.getInt(compound, "k");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setInt(compound, "k", k);
		return super.writeToNBT(compound);
	}

	@Override
	public void writeToStack(ItemStack stack) {
		NBTStackHelper.setInt(stack, "k", k);
	}

	@Override
	public void readFromStack(ItemStack stack) {
		k = NBTStackHelper.getInt(stack, "k");
	}

	@Override
	public String getOwner() {
		return null;
	}

	@Override
	public boolean canAccess(String name) {
		return true;
	}

}
