package mrriegel.testmod;

import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TestPart extends DataPart {

	public int v = 3;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		v = NBTHelper.getInt(compound, "v");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setInt(compound, "v", v);
		return super.writeToNBT(compound);
	}

	@Override
	public void updateServer(World world) {
		if (world.getTotalWorldTime() % 20 == 0) {
			BlockPos pos = getPos().south(4);
			while (world.isAirBlock((pos = pos.down())))
				;
			BlockHelper.breakBlockWithFortune(world, pos, 0, Utils.getFakePlayer((WorldServer) world), false, true);
		}
	}

	@Override
	protected String firstName() {
		return "theo";
	}
}
