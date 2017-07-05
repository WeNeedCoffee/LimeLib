package mrriegel.limelib.helper;

import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Sets;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import mrriegel.limelib.LimeLib;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

public class EnergyHelper {

	@Optional.InterfaceList(value = { @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"), @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"), @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla") })
	@Deprecated
	//TODO remove
	public class ItemEnergyWrapper implements IEnergyStorage, ITeslaHolder, ITeslaConsumer, ITeslaProducer {
		ItemStack stack;

		public ItemEnergyWrapper(ItemStack stack) {
			this.stack = stack;
			Validate.isTrue(LimeLib.fluxLoaded && stack.getItem() instanceof IEnergyContainerItem);
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return ((IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return ((IEnergyContainerItem) stack.getItem()).extractEnergy(stack, maxExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			return ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack);
		}

		@Override
		public int getMaxEnergyStored() {
			return ((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack);
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return true;
		}

		@Override
		public long takePower(long power, boolean simulated) {
			return extractEnergy((int) (power % Integer.MAX_VALUE), simulated);
		}

		@Override
		public long givePower(long power, boolean simulated) {
			return receiveEnergy((int) (power % Integer.MAX_VALUE), simulated);
		}

		@Override
		public long getStoredPower() {
			return getEnergyStored();
		}

		@Override
		public long getCapacity() {
			return getMaxEnergyStored();
		}

	}

	public enum Energy {
		RF("RF"), //
		FORGE("FU"), //
		TESLA("Tesla");

		public String unit;

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

	public static long getEnergy(ICapabilityProvider container, EnumFacing side) {
		if (container == null)
			return 0;
		if (container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).getEnergyStored();
		if (LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getStoredPower();
		if (LimeLib.fluxLoaded && container instanceof IEnergyHandler)
			return ((IEnergyHandler) container).getEnergyStored(side);
		if (LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).getEnergyStored((ItemStack) container);
		return 0;
	}

	public static long getMaxEnergy(ICapabilityProvider container, EnumFacing side) {
		if (container == null)
			return 0;
		if (container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).getMaxEnergyStored();
		if (LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getCapacity();
		if (LimeLib.fluxLoaded && container instanceof IEnergyHandler)
			return ((IEnergyHandler) container).getMaxEnergyStored(side);
		if (LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).getMaxEnergyStored((ItemStack) container);
		return 0;
	}

	public static long receiveEnergy(ICapabilityProvider container, EnumFacing side, int maxReceive, boolean simulate) {
		if (container == null)
			return 0;
		if (container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).receiveEnergy(maxReceive, simulate);
		if (LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side).givePower(maxReceive, simulate);
		if (LimeLib.fluxLoaded && container instanceof IEnergyReceiver)
			return ((IEnergyReceiver) container).receiveEnergy(side, maxReceive, simulate);
		if (LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).receiveEnergy((ItemStack) container, maxReceive, simulate);
		return 0;
	}

	public static long extractEnergy(ICapabilityProvider container, EnumFacing side, int maxExtract, boolean simulate) {
		if (container == null)
			return 0;
		if (container.hasCapability(CapabilityEnergy.ENERGY, side))
			return container.getCapability(CapabilityEnergy.ENERGY, side).extractEnergy(maxExtract, simulate);
		if (LimeLib.teslaLoaded && container.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side))
			return container.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side).takePower(maxExtract, simulate);
		if (LimeLib.fluxLoaded && container instanceof IEnergyProvider)
			return ((IEnergyProvider) container).extractEnergy(side, maxExtract, simulate);
		if (LimeLib.fluxLoaded && container instanceof ItemStack && ((ItemStack) container).getItem() instanceof IEnergyContainerItem)
			return ((IEnergyContainerItem) ((ItemStack) container).getItem()).extractEnergy((ItemStack) container, maxExtract, simulate);
		return 0;
	}

}
