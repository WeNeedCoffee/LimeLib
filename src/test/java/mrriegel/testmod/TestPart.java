package mrriegel.testmod;

import java.util.LinkedList;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.datapart.DataPartWorker;
import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.helper.WorldHelper;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Lists;

public class TestPart extends DataPartWorker {

	private LinkedList<BlockPos> posList = Lists.newLinkedList();
	private boolean started = false;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		posList = Lists.newLinkedList(Utils.getBlockPosList(NBTHelper.getList(compound, "poss",Long.class)));
		started = compound.getBoolean("started");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setList(compound, "poss", Utils.getLongList(posList));
		compound.setBoolean("started", started);
		return super.writeToNBT(compound);
	}

	@Override
	public void updateClient(World world) {
		super.updateClient(world);
		if (world.getTotalWorldTime() % 7 == 0)
			LimeLib.proxy.renderParticle(new CommonParticle(getX() + .5 + (world.rand.nextDouble() - .5), getY() + 1.9, getZ() + .5 + (world.rand.nextDouble() - .5), (world.rand.nextDouble() - .5) / 8, .1, (world.rand.nextDouble() - .5) / 8).setTexture(ParticleHelper.squareParticle));
	}

	@Override
	protected boolean workDone(World world, Side side) {
		return posList.isEmpty() && started && false;
	}

	@Override
	protected boolean canWork(World world, Side side) {
		return !true;
	}

	@Override
	protected void work(World world, Side side) {
		if (side.isServer()) {
			if (!started) {
				started = true;
				posList = Lists.newLinkedList(WorldHelper.getChunk(world, getPos()));
			}
			if (!posList.isEmpty()) {
				BlockPos pos = posList.poll();
				while (world.isAirBlock(pos) && !posList.isEmpty())
					pos = posList.poll();
				BlockHelper.breakBlock(world, pos, world.getBlockState(pos), null, false, 0, true, true);
			}
		}

	}
}
