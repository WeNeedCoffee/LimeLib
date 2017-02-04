package mrriegel.testmod;

import java.util.Iterator;
import java.util.List;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.tile.CommonTileInventory;
import mrriegel.limelib.tile.IDataKeeper;
import mrriegel.limelib.tile.IOwneable;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
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
		player.openGui(TestMod.mod, 0, world, getPos().getX(), getPos().getY(), getPos().getZ());
		return true;
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt) {
		super.handleMessage(player, nbt);
		lis = null;
		sync();
		int range = 8;
		if (NBTHelper.hasTag(nbt, "k"))
			k = NBTHelper.getInt(nbt, "k");
		if (!InvHelper.hasItemHandler(world.getTileEntity(pos.up()), EnumFacing.DOWN))
			return;
		List<BlockPos> lis = Lists.newArrayList();
		for (int y = pos.getY() - 1; y > 0; y--)
			for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
					if (!BlockHelper.isOre(world, new BlockPos(x, y, z)))
						lis.add(new BlockPos(x, y, z));
		for (BlockPos p : lis) {
			for (ItemStack s : BlockHelper.breakBlockWithFortune(world, p, 3, player, false, false))
				if (ItemHandlerHelper.insertItem(InvHelper.getItemHandler(world.getTileEntity(pos.up()), EnumFacing.DOWN), s.copy(), false) != null)
					return;
		}
	}

	List<BlockPos> lis = null;

	@Override
	public void update() {
		if (world.isRemote)
			return;
		int range = 9;
		if (lis == null) {
			lis = Lists.newArrayList();
			for (int y = pos.getY() - 1; y > 0; y--)
				for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
					for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
						if (!world.isAirBlock(new BlockPos(x, y, z)))
							lis.add(new BlockPos(x, y, z));

		}
		EntityPlayer player = Utils.getRandomPlayer((WorldServer) world);
		if (world.getTotalWorldTime() % 1 == 0 && world.isBlockPowered(pos) && player != null) {
			for (int i = 0; i < 1; i++)
				try {
					Iterator<BlockPos> it = lis.listIterator();
					whil: while (it.hasNext()) {
						BlockPos p = it.next();
						if (world.getTileEntity(p) == null && BlockHelper.isBlockBreakable(world, p) && !BlockHelper.isOre(world, p)) {
							List<ItemStack> drops = BlockHelper.breakBlockWithFortune(world, p, 0, null, false, false);
							drops.clear();
							for (ItemStack drop : drops)
								if (drop != null)
									ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(player.inventory), drop.copy(), false);
							break whil;
						} else {
							for (Vec3d vec : ParticleHelper.getVecsForLine(p, pos, .6))
								ParticleHelper.renderParticle(new CommonParticle(vec.xCoord, vec.yCoord, vec.zCoord).setMaxAge2(1));
						}
						it.remove();
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
