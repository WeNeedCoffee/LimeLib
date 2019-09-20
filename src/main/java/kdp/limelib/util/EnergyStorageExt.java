package kdp.limelib.util;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorageExt extends EnergyStorage {
    public EnergyStorageExt(int capacity) {
        super(capacity);
    }

    public EnergyStorageExt(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public EnergyStorageExt(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyStorageExt(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergyStored(int energy) {
        this.energy = MathHelper.clamp(energy, 0, capacity);
        onContentsChanged();
    }

    public void modifyEnergyStored(int energy) {
        this.energy += energy;
        this.energy = MathHelper.clamp(this.energy, 0, capacity);
        onContentsChanged();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!simulate) {
            onContentsChanged();
        }
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!simulate) {
            onContentsChanged();
        }
        return super.extractEnergy(maxExtract, simulate);
    }

    protected void onContentsChanged() {

    }

}
