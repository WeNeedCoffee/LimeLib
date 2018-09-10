package mrriegel.limelib.farm;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapaPE {

	@CapabilityInject(PseudoEntityRegistry.class)
	public static Capability<PseudoEntityRegistry> PSEUDOENTITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(PseudoEntityRegistry.class, new IStorage<PseudoEntityRegistry>() {
			@Override
			public NBTBase writeNBT(Capability<PseudoEntityRegistry> capability, PseudoEntityRegistry instance, EnumFacing side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<PseudoEntityRegistry> capability, PseudoEntityRegistry instance, EnumFacing side, NBTBase nbt) {
				if (nbt instanceof NBTTagCompound) {
					instance.deserializeNBT((NBTTagCompound) nbt);
				}
			}
		}, PseudoEntityRegistry::new);
	}

	public static class CapaProvider implements ICapabilitySerializable<NBTTagCompound> {

		PseudoEntityRegistry instance = PSEUDOENTITY.getDefaultInstance();

		public CapaProvider(World world) {
			instance.world = world;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == PSEUDOENTITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			return hasCapability(capability, facing) ? PSEUDOENTITY.cast(instance) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) PSEUDOENTITY.getStorage().writeNBT(PSEUDOENTITY, instance, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			PSEUDOENTITY.getStorage().readNBT(PSEUDOENTITY, instance, null, nbt);
		}
	}

}
