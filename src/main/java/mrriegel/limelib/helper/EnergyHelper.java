package mrriegel.limelib.helper;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import cofh.api.energy.IEnergyHandler;

public class EnergyHelper {

	public enum Energy {
		RF("RF"), FORGE("FU"), TESLA("Tesla");

		public String unit;

		private Energy(String unit) {
			this.unit = unit;
		}
	}

	public static Energy isEnergyInterface(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null)
			return null;
		if (tile instanceof IEnergyHandler) {
			return Energy.RF;
		} else if (tile.hasCapability(CapabilityEnergy.ENERGY, null)) {
			return Energy.FORGE;
		} else if (Loader.isModLoaded("tesla") && tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)) {
			return Energy.TESLA;
		}
		return null;
	}

	public static long getEnergy(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null)
			return 0;
		if (tile instanceof IEnergyHandler) {
			try {
				return ((IEnergyHandler) tile).getEnergyStored(null);

			} catch (Exception e) {
				return 0;
			}
		} else if (tile.hasCapability(CapabilityEnergy.ENERGY, null)) {
			return tile.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
		} else if (Loader.isModLoaded("tesla") && tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)) {
			return tile.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null).getStoredPower();
		}
		return 0;
	}

	public static long getMaxEnergy(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null)
			return 0;
		if (tile instanceof IEnergyHandler) {
			try {
				return ((IEnergyHandler) tile).getMaxEnergyStored(null);

			} catch (Exception e) {
				return 0;
			}
		} else if (tile.hasCapability(CapabilityEnergy.ENERGY, null)) {
			return tile.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
		} else if (Loader.isModLoaded("tesla") && tile.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)) {
			return tile.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null).getCapacity();
		}
		return 0;
	}

}
