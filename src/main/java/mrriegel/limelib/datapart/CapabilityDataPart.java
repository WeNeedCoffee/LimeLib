package mrriegel.limelib.datapart;

import java.util.List;
import java.util.concurrent.Callable;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.google.common.collect.Lists;

public class CapabilityDataPart {

	@CapabilityInject(DataPartRegistry.class)
	public static Capability<DataPartRegistry> DATAPART = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(DataPartRegistry.class, new IStorage<DataPartRegistry>() {
			@Override
			public NBTBase writeNBT(Capability<DataPartRegistry> capability, DataPartRegistry instance, EnumFacing side) {
				List<NBTTagCompound> nbts = Lists.newArrayList();
				for (DataPart entry : instance.partMap.values())
					nbts.add(entry.writeDataToNBT(new NBTTagCompound()));
				return NBTHelper.setTagList(new NBTTagCompound(), "nbts", nbts);
			}

			@Override
			public void readNBT(Capability<DataPartRegistry> capability, DataPartRegistry instance, EnumFacing side, NBTBase nbt) {
				if (nbt instanceof NBTTagCompound) {
					instance.partMap.clear();
					List<NBTTagCompound> nbts = NBTHelper.getTagList((NBTTagCompound) nbt, "nbts");
					for (NBTTagCompound n : nbts) {
						try {
							Class<?> clazz = Class.forName(n.getString("class"));
							if (clazz != null && DataPart.class.isAssignableFrom(clazz)) {
								DataPart part = (DataPart) ConstructorUtils.invokeConstructor(clazz);
								if (part != null) {
									part.readDataFromNBT(n);
									if (DimensionManager.isDimensionRegistered(part.pos.getDimension()))
										instance.partMap.put(part.pos, part);
									else
										LimeLib.log.error("Dimension " + part.pos.getDimension() + " is not registered.");
								}
							}
						} catch (ReflectiveOperationException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, new Callable<DataPartRegistry>() {
			@Override
			public DataPartRegistry call() throws Exception {
				return new DataPartRegistry();
			}
		});
	}
}
