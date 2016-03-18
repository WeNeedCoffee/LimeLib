package mrriegel.limelib.util;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class Utils {
	public static <E> boolean contains(List<E> list, E e,
			Comparator<? super E> c) {
		for (E a : list)
			if (c.compare(a, e) == 0)
				return true;
		return false;
	}

	public static void spawnItemStack(World worldIn, BlockPos pos,
			ItemStack stack) {
		if (!worldIn.isRemote
				&& worldIn.getGameRules().getBoolean("doTileDrops")
				&& !worldIn.restoringBlockSnapshots) {
			float f = 0.5F;
			double d0 = (double) (worldIn.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d1 = (double) (worldIn.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			double d2 = (double) (worldIn.rand.nextFloat() * f)
					+ (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(worldIn, (double) pos.getX()
					+ d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
					stack);
			entityitem.setDefaultPickupDelay();
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

}
