package mrriegel.limelib.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldHelper {
	public static void spawnItemStack(World worldIn, BlockPos pos,
			ItemStack stack) {
		if (!worldIn.isRemote
				&& worldIn.getGameRules().getBoolean("doTileDrops")
				&& !worldIn.restoringBlockSnapshots) {
			float f = 0.5F;
			double d0 = worldIn.rand.nextFloat() * f
					+ (1.0F - f) * 0.5D;
			double d1 = worldIn.rand.nextFloat() * f
					+ (1.0F - f) * 0.5D;
			double d2 = worldIn.rand.nextFloat() * f
					+ (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(worldIn, pos.getX()
					+ d0, pos.getY() + d1, pos.getZ() + d2,
					stack);
			entityitem.setDefaultPickupDelay();
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

	public static List<BlockPos> getNeighbors(BlockPos pos) {
		List<BlockPos> lis = new ArrayList<BlockPos>();
		lis.add(pos.up());
		lis.add(pos.down());
		lis.add(pos.east());
		lis.add(pos.west());
		lis.add(pos.north());
		lis.add(pos.south());
		return lis;
	}
}
