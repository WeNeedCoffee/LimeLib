package mrriegel.testmod;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import mrriegel.limelib.tile.CommonTileInventory;
import mrriegel.limelib.tile.IDataKeeper;
import mrriegel.limelib.tile.IHUDProvider;
import mrriegel.limelib.tile.IOwneable;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class TestTile extends CommonTileInventory implements ITickable, IDataKeeper, IOwneable, IHUDProvider {

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
			k = NBTHelper.get(nbt, "k", Integer.class);
		if (!InvHelper.hasItemHandler(world.getTileEntity(pos.up()), EnumFacing.DOWN))
			return;
		List<BlockPos> lis = Lists.newArrayList();
		for (int y = pos.getY() - 1; y > 0; y--)
			for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
					if (!BlockHelper.isOre(world, new BlockPos(x, y, z)))
						lis.add(new BlockPos(x, y, z));
		//		for (BlockPos p : lis) {
		//			for (ItemStack s : BlockHelper.breakBlockWithFortune(world, p, 3, player, false, false,true))
		//				if (ItemHandlerHelper.insertItemStacked(InvHelper.getItemHandler(world.getTileEntity(pos.up()), EnumFacing.DOWN), s.copy(), false) != null)
		//					return;
		//		}
	}

	List<BlockPos> lis = null;

	@Override
	public void update() {
		if (world.getTotalWorldTime() % 22 == 0) {
			k = new Random().nextInt(10) + (onClient() ? 0 : 10);
		}
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
							//							List<ItemStack> drops = BlockHelper.breakBlockWithFortune(world, p, 0, null, false, false,true);
							//							drops.clear();
							//							for (ItemStack drop : drops)
							//								if (drop != null)
							//									ItemHandlerHelper.insertItemStacked(new PlayerMainInvWrapper(player.inventory), drop.copy(), false);
							break whil;
						} else {
							//							for (Vec3d vec : ParticleHelper.getVecsForLine(p, pos, .6))
							//								LimeLib.proxy.renderParticle(new CommonParticle(vec.xCoord, vec.yCoord, vec.zCoord).setMaxAge2(1));
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
		NBTHelper.getSafe(compound, "k", Integer.class).ifPresent(i -> k = i);
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.set(compound, "k", k);
		return super.writeToNBT(compound);
	}

	@Override
	public void writeToStack(ItemStack stack) {
		NBTStackHelper.set(stack, "k", k);
	}

	@Override
	public void readFromStack(ItemStack stack) {
		k = NBTStackHelper.get(stack, "k", Integer.class);
	}

	@Override
	public String getOwner() {
		return null;
	}

	@Override
	public boolean canAccess(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean showData(boolean sneak, EnumFacing facing) {
		return true;
	}

	@Override
	public List<String> getData(boolean sneak, EnumFacing facing) {
		if (!sneak)
			return Lists.newArrayList(TextFormatting.DARK_GREEN + "" + facing, //
					TextFormatting.GOLD.toString() + "Topic: ", //
					"   -Thermodynamik", //
					"   -" + TextFormatting.ITALIC + "Van Gogh", //
					"   -Tierwesen", //
					"   -Kanada");
		else
			return Lists.newArrayList("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor");
	}

	@Override
	public int getBackgroundColor(boolean sneak, EnumFacing facing) {
		if (!sneak)
			return 0x66850000;
		else
			return 0xFF000099;
		//		return IDataSupplier.super.getBackgroundColor(sneak, facing);
	}

	@Override
	public boolean center(boolean sneak, EnumFacing facing) {
		return sneak;
	}
}
