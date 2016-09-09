package mrriegel.limelib.util;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.NBTStackHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;

/** thats bad */
@Deprecated
public abstract class DataWrapper implements INBTSerializable<NBTTagCompound> {
	public final String capaName;
	protected NBTTagCompound nbt;

	public DataWrapper(ItemStack stack, String name) {
		this.capaName = name;
		if (load(name, stack) == null)
			save(name, stack, new NBTTagCompound());
		nbt = load(name, stack);
	}

	public DataWrapper(Entity entity, String name) {
		this.capaName = name;
		if (load(name, entity) == null)
			save(name, entity, new NBTTagCompound());
		nbt = load(name, entity);
	}

	public DataWrapper(TileEntity tile, String name) {
		this.capaName = name;
		if (load(name, tile) == null) {
			save(name, tile, new NBTTagCompound());
		}
		nbt = load(name, tile);
	}

	public abstract DataWrapper get(NBTTagCompound nbt);

	public final void markDirty() {

	}

	public static void save(String name, Object o, NBTTagCompound nbt) {
		if (o instanceof ItemStack) {
			NBTStackHelper.setTag((ItemStack) o, name, nbt);
		} else if (o instanceof Entity) {
			NBTHelper.setTag(((Entity) o).getEntityData(), name, nbt);
		} else if (o instanceof TileEntity) {
			TileEntity t = (TileEntity) o;
			NBTTagCompound nbt2 = t.writeToNBT(new NBTTagCompound());
			NBTHelper.setTag(nbt2, name, new NBTTagCompound());
			t.readFromNBT(nbt2);
			t.markDirty();
		}
	}

	public static NBTTagCompound load(String name, Object o) {
		if (o instanceof ItemStack) {
			return NBTStackHelper.getTag((ItemStack) o, name);
		} else if (o instanceof Entity) {
			return NBTHelper.getTag(((Entity) o).getEntityData(), name);
		} else if (o instanceof TileEntity) {
			TileEntity t = (TileEntity) o;
			return NBTHelper.getTag(t.serializeNBT(), name);
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

}
