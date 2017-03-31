package mrriegel.limelib.datapart;

import javax.annotation.Nullable;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DataPart {

	protected BlockPos pos;
	protected World world;
	public int ticksExisted;

	public void updateServer(World world) {
	}

	public void updateClient(World world) {
	}

	public void onAdded() {
	}

	public void onRemoved() {
	}

	public void onRightClicked(EntityPlayer player, EnumHand hand) {
	}

	public void onLeftClicked(EntityPlayer player, EnumHand hand) {
	}

	public boolean clientValid() {
		return true;
	}

	public AxisAlignedBB getHighlightBox() {
		return Block.FULL_BLOCK_AABB;
	}

	//	public List<String> getWailaTooltip() {
	//		List<String> lis = Lists.newArrayList();
	//		lis.add(TextFormatting.WHITE + getClass().getSimpleName());
	//		addTooltip(lis);
	//		lis.add(TextFormatting.BLUE.toString() + TextFormatting.ITALIC + Utils.getCurrentModID());
	//		return lis;
	//	}
	//
	//	protected void addTooltip(List<String> lis) {
	//	}

	public final void readDataFromNBT(NBTTagCompound compound) {
		pos = BlockPos.fromLong(NBTHelper.getLong(compound, "poS"));
		readFromNBT(compound);
	}

	public final NBTTagCompound writeDataToNBT(NBTTagCompound compound) {
		writeToNBT(compound);
		compound.setString("id", DataPartRegistry.PARTS.inverse().get(getClass()));
		compound.setLong("poS", pos.toLong());
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}

	protected final DataPartRegistry getRegistry() {
		DataPartRegistry d = DataPartRegistry.get(world);
		assert d != null;
		return d;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public final int getX() {
		return pos.getX();
	}

	public final int getY() {
		return pos.getY();
	}

	public final int getZ() {
		return pos.getZ();
	}

	public static @Nullable DataPart rayTrace(EntityPlayer player) {
		DataPartRegistry reg = DataPartRegistry.get(player.world);
		if (reg == null)
			return null;
		Vec3d eye = player.getPositionVector().addVector(0, player.eyeHeight, 0);
		Vec3d look = player.getLookVec().scale(.5);
		Vec3d vec = eye.add(look);
		DataPart part = null;
		while (vec.distanceTo(eye) < LimeLib.proxy.getReachDistance(player)) {
			BlockPos p = new BlockPos(vec);
			if (player.world.getBlockState(p).getCollisionBoundingBox(player.world, p)!=null)
				break;
			if ((part = reg.getDataPart(p)) != null)
				break;
			look = look.add(player.getLookVec().scale(.15));
			vec = eye.add(look);
		}
		return part;
	}
}
