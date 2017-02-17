package mrriegel.limelib.datapart;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public abstract class DataPartWorker extends DataPart {

	protected abstract boolean workDone(World world, Side side);

	protected abstract boolean canWork(World world, Side side);

	protected abstract void work(World world, Side side);

	@Override
	public void updateServer(World world) {
		if (canWork(world, Side.SERVER))
			work(world, Side.SERVER);
		if (workDone(world, Side.SERVER)) {
			DataPartRegistry reg = DataPartRegistry.get(world);
			if (reg != null)
				reg.removeDataPart(pos);
		}
	}

	@Override
	public void updateClient(World world) {
		if (canWork(world, Side.CLIENT))
			work(world, Side.CLIENT);
		if (workDone(world, Side.CLIENT)) {
			DataPartRegistry reg = DataPartRegistry.get(world);
			if (reg != null)
				reg.removeDataPart(pos);
		}
	}
}
