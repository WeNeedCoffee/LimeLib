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
