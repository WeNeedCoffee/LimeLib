package mrriegel.limelib.helper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import com.google.common.base.Predicate;

public class WorldHelper {

	public static void addOreSpawn(IBlockState state, World world, int veinPerChunk, int size, int chunkX, int chunkZ, int minY, int maxY) {
		addOreSpawn(state, world, veinPerChunk, size, chunkX, chunkZ, minY, maxY, BlockMatcher.forBlock(Blocks.STONE));
	}

	public static void addOreSpawn(IBlockState state, World world, int veinPerChunk, int size, int chunkX, int chunkZ, int minY, int maxY, Predicate<IBlockState> predicate) {
		for (int i = 0; i < veinPerChunk; i++) {
			int diffBtwnMinMaxY = maxY - minY;
			int x = (chunkX << 4) + world.rand.nextInt(16);
			int y = minY + world.rand.nextInt(diffBtwnMinMaxY);
			int z = (chunkZ << 4) + world.rand.nextInt(16);
			new WorldGenMinable(state, 5 + world.rand.nextInt(3), predicate).generate(world, world.rand, new BlockPos(x, y, z));
		}
	}

	public static double getDistance(BlockPos a, BlockPos b) {
		return Math.sqrt(a.distanceSq(b));
	}
}
