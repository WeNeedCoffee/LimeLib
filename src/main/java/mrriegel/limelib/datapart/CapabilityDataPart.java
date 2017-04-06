package mrriegel.limelib.datapart;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityDataPart {

	@CapabilityInject(DataPartRegistry.class)
	public static Capability<DataPartRegistry> DATAPART = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(DataPartRegistry.class, new IStorage<DataPartRegistry>() {
			@Override
			public NBTBase writeNBT(Capability<DataPartRegistry> capability, DataPartRegistry instance, EnumFacing side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<DataPartRegistry> capability, DataPartRegistry instance, EnumFacing side, NBTBase nbt) {
				if (nbt instanceof NBTTagCompound) {
					instance.deserializeNBT((NBTTagCompound) nbt);
				}
			}
		}, new Callable<DataPartRegistry>() {
			@Override
			public DataPartRegistry call() throws Exception {
				return new DataPartRegistry();
			}
		});
	}

	public static class CapaProvider implements ICapabilitySerializable<NBTTagCompound> {

		DataPartRegistry instance = CapabilityDataPart.DATAPART.getDefaultInstance();

		public CapaProvider(World world) {
			instance.world = world;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityDataPart.DATAPART;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return CapabilityDataPart.DATAPART.cast(instance);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) CapabilityDataPart.DATAPART.getStorage().writeNBT(CapabilityDataPart.DATAPART, instance, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			CapabilityDataPart.DATAPART.readNBT(instance, null, nbt);
		}
	}
}
