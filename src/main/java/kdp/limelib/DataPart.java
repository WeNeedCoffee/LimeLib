package kdp.limelib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DataPart extends ForgeRegistryEntry<DataPart> {

	private static final AxisAlignedBB FULL = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	protected BlockPos pos;
	protected World world;
	public int ticksExisted;

	public void update() {

	}

	public void onAdded() {

	}

	public void onRemoved() {

	}

	public boolean onRightClicked(EntityPlayer player, EnumHand hand) {
		return false;
	}

	public boolean onLeftClicked(EntityPlayer player, EnumHand hand) {
		return false;
	}

	public boolean clientValid() {
		return true;
	}

	public AxisAlignedBB getHighlightBox() {
		return FULL;
	}

	public final void readDataFromNBT(NBTTagCompound compound) {
		pos = BlockPos.fromLong(compound.getLong("poS"));
		readFromNBT(compound);
	}

	public final NBTTagCompound writeDataToNBT(NBTTagCompound compound) {
		writeToNBT(compound);
		//TODO compound.setString("id", DataPartRegistry.PARTS.inverse().get(getClass()));
		compound.setLong("poS", pos.toLong());
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}
	
	protected final WorldAddition getWorldAddition() {
		return WorldAddition.getWorldAddition(world);
	}
}
