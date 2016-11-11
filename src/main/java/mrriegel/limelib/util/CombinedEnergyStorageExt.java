package mrriegel.limelib.util;

import net.minecraftforge.energy.IEnergyStorage;

public class CombinedEnergyStorageExt implements IEnergyStorage {

	EnergyStorageExt[] storages;

	public CombinedEnergyStorageExt(EnergyStorageExt... storages) {
		//		super(getCapacity(storages), getReceive(storages), getExtract(storages));
		this.storages = storages;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = 0;
		int rest = maxReceive;
		for (EnergyStorageExt e : storages) {
			if (received == maxReceive || rest == 0)
				break;
			int r = e.receiveEnergy(rest, simulate);
			received += r;
			rest -= r;
		}
		return received;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int extracted = 0;
		int rest = maxExtract;
		for (EnergyStorageExt e : storages) {
			if (extracted == maxExtract || rest == 0)
				break;
			int r = e.extractEnergy(rest, simulate);
			extracted += r;
			rest -= r;
		}
		return extracted;
	}

	@Override
	public int getEnergyStored() {
		int i = 0;
		for (EnergyStorageExt e : storages)
			i += e.getEnergyStored();
		return i;
	}

	@Override
	public int getMaxEnergyStored() {
		int i = 0;
		for (EnergyStorageExt e : storages)
			i += e.getMaxEnergyStored();
		return i;
	}

	//	@Override
	public void setEnergyStored(int energy) {
		throw new UnsupportedOperationException();
	}

	//	@Override
	public void modifyEnergyStored(int energy) {
		throw new UnsupportedOperationException();
	}

	private static int getCapacity(EnergyStorageExt... storages) {
		int i = 0;
		for (EnergyStorageExt e : storages)
			i += e.getMaxEnergyStored();
		return i;
	}

	private static int getReceive(EnergyStorageExt... storages) {
		int i = 0;
		for (EnergyStorageExt e : storages)
			i += e.getMaxReceive();
		return i;
	}

	private static int getExtract(EnergyStorageExt... storages) {
		int i = 0;
		for (EnergyStorageExt e : storages)
			i += e.getMaxExtract();
		return i;
	}

	@Override
	public boolean canExtract() {
		return getExtract(storages) > 0;
	}

	@Override
	public boolean canReceive() {
		return getReceive(storages) > 0;
	}

}
