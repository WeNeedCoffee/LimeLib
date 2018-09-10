package mrriegel.limelib.farm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class PseudoEntityRegistry implements INBTSerializable<NBTTagCompound> {

	public static final ResourceLocation LOCATION = new ResourceLocation(LimeLib.MODID + ":pe");
	public static final BiMap<String, Class<? extends PseudoEntity>> PARTS = HashBiMap.create();
	private static int ID = 0;
	private static final String idKey = "iD&&:Id";

	private Int2ObjectOpenHashMap<PseudoEntity> entMap = new Int2ObjectOpenHashMap<>();
	public World world;

	public static void register(String name, Class<? extends PseudoEntity> clazz) {
		Validate.isTrue(!PARTS.containsKey(name) && !PARTS.containsValue(clazz), "already registered");
		Validate.isTrue(Stream.of(clazz.getConstructors()).anyMatch(c -> c.getParameterCount() == 0), "empty constructor required");
		PARTS.put(name, clazz);
	}

	public PseudoEntity getEntity(int id) {
		return entMap.get(id);
	}

	public boolean addEntity(PseudoEntity ent) {
		if (!PARTS.inverse().containsKey(ent.getClass())) {
			LimeLib.log.error(ent.getClass() + " not registered.");
			return false;
		}
		Validate.isTrue(world == ent.world, "worlds not equal (" + world + " " + ent.world + ")");
		ent.id = ID++;
		entMap.put(ent.id, ent);
		//sync
		return true;
	}

	public void removeEntity(PseudoEntity ent) {
		if (entMap.containsKey(ent.id)) {
			ent.onDeath();
			entMap.remove(ent.id);
			//sync
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		List<NBTTagCompound> nbts = Lists.newArrayList();
		for (PseudoEntity ent : entMap.values()) {
			NBTTagCompound n = ent.serializeNBT();
			n.setString(idKey, PARTS.inverse().get(ent.getClass()));
		}
		return NBTHelper.setList(new NBTTagCompound(), "nbts", nbts);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		entMap.clear();
		for (NBTTagCompound n : NBTHelper.getList(nbt, "nbts", NBTTagCompound.class)) {
			createEnt(n);
		}
	}

	public void createEnt(NBTTagCompound n) {
		try {
			Class<?> clazz = PARTS.get(n.getString(idKey));
			if (clazz != null && PseudoEntity.class.isAssignableFrom(clazz)) {
				PseudoEntity ent = (PseudoEntity) ConstructorUtils.invokeConstructor(clazz, world);
				if (ent != null) {
					ent.deserializeNBT(n);
					addEntity(ent);
					return;
				}
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
		}
		LimeLib.log.error("Failed to create pseudoentity " + n.getString("id"));
	}

}
