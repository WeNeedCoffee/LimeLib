package mrriegel.limelib.helper;

import java.util.Set;

import com.google.common.collect.Sets;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import mrriegel.limelib.LimeLib;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyHelper {

	public enum Energy {
		RF("RF"), //
		FORGE("FE"), //
		TESLA("Tesla");

		String unit;

		private Energy(String unit) {
			this.unit = unit;
		}
	}

	public static Energy isEnergyContainer(ICapabilityProvider container, EnumFacing side, Energy... energys) {
		if (container == null)
			return null;
		Set<Energy> set = Sets.newHashSet(energys);
		if (set.contains(Energy.FORGE) && container.hasCapability(CapabilityEnergy.ENERGY, side))
			return Energy.FORGE;
		if (set.contains(Energy.TESLA) && LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side))
			return Energy.TESLA;
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof IEnergyHandler)
			return Energy.RF;
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return Energy.RF;
		return null;
	}

	public static Energy isEnergyContainer(ICapabilityProvider container, EnumFacing side) {
		return isEnergyContainer(container, side, Energy.values());
	}

	public static long getEnergy(ICapabilityProvider container, EnumFacing side, Energy... energys) {
		if (container == null)
			return 0;
		Set<Energy> set = Sets.newHashSet(energys);
		if (set.contains(Energy.FORGE) && container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).getEnergyStored();
		if (set.contains(Energy.TESLA) && LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getStoredPower();
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof IEnergyHandler)
			return ((IEnergyHandler) container).getEnergyStored(side);
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).getEnergyStored((ItemStack) container);
		return 0;
	}

	public static long getEnergy(ICapabilityProvider container, EnumFacing side) {
		return getEnergy(container, side, Energy.values());
	}

	public static long getMaxEnergy(ICapabilityProvider container, EnumFacing side, Energy... energys) {
		if (container == null)
			return 0;
		Set<Energy> set = Sets.newHashSet(energys);
		if (set.contains(Energy.FORGE) && container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).getMaxEnergyStored();
		if (set.contains(Energy.TESLA) && LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getCapacity();
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof IEnergyHandler)
			return ((IEnergyHandler) container).getMaxEnergyStored(side);
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).getMaxEnergyStored((ItemStack) container);
		return 0;
	}

	public static long getMaxEnergy(ICapabilityProvider container, EnumFacing side) {
		return getMaxEnergy(container, side, Energy.values());
	}

	public static long receiveEnergy(ICapabilityProvider container, EnumFacing side, int maxReceive, boolean simulate, Energy... energys) {
		if (container == null)
			return 0;
		Set<Energy> set = Sets.newHashSet(energys);
		if (set.contains(Energy.FORGE) && container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).receiveEnergy(maxReceive, simulate);
		if (set.contains(Energy.TESLA) && LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side).givePower(maxReceive, simulate);
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof IEnergyReceiver)
			return ((IEnergyReceiver) container).receiveEnergy(side, maxReceive, simulate);
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).receiveEnergy((ItemStack) container, maxReceive, simulate);
		return 0;
	}

	public static long receiveEnergy(ICapabilityProvider container, EnumFacing side, int maxReceive, boolean simulate) {
		return receiveEnergy(container, side, maxReceive, simulate, Energy.values());
	}

	public static long extractEnergy(ICapabilityProvider container, EnumFacing side, int maxExtract, boolean simulate, Energy... energys) {
		if (container == null)
			return 0;
		Set<Energy> set = Sets.newHashSet(energys);
		if (set.contains(Energy.FORGE) && container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).extractEnergy(maxExtract, simulate);
		if (set.contains(Energy.TESLA) && LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side).takePower(maxExtract, simulate);
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof IEnergyProvider)
			return ((IEnergyProvider) container).extractEnergy(side, maxExtract, simulate);
		if (set.contains(Energy.RF) && LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).extractEnergy((ItemStack) container, maxExtract, simulate);
		return 0;
	}

	public static long extractEnergy(ICapabilityProvider container, EnumFacing side, int maxExtract, boolean simulate) {
		return extractEnergy(container, side, maxExtract, simulate, Energy.values());
	}

}
