package mrriegel.limelib.helper;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.gen.feature.WorldGenMinable;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

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

	public static boolean spawnInRange(Entity entity, World world, BlockPos pos, int range) {
		List<BlockPos> lis = Lists.newArrayList();
		for (int x = pos.getX() - range; x <= pos.getX() + range; x++)
			for (int y = pos.getY() - range; y <= pos.getY() + range; y++)
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; z++)
					lis.add(new BlockPos(x, y, z));
		List<BlockPos> end = Lists.newArrayList();
		for (BlockPos b : lis)
			if (canCreatureTypeSpawnAtLocation(entity, SpawnPlacementType.ON_GROUND, world, b))
				end.add(b);
		if (end.isEmpty())
			return false;
		Collections.shuffle(end);
		for (BlockPos fin : end) {
			entity.posX = fin.getX() + .5D;
			entity.posY = fin.getY() + .1D;
			entity.posZ = fin.getZ() + .5D;
			boolean spawned = entity.worldObj.spawnEntityInWorld(entity);
			entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
			if (spawned)
				return true;
		}
		return false;
	}

	public static boolean canCreatureTypeSpawnAtLocation(Entity entity, EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
		if (!worldIn.getWorldBorder().contains(pos)) {
			return false;
		} else {
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER) {
				return iblockstate.getMaterial().isLiquid() && worldIn.getBlockState(pos.down()).getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).isNormalCube();
			} else {
				BlockPos blockpos = pos.down();
				IBlockState state = worldIn.getBlockState(blockpos);

				if (!state.getBlock().canCreatureSpawn(state, worldIn, blockpos, spawnPlacementTypeIn)) {
					return false;
				} else {
					Block block = worldIn.getBlockState(blockpos).getBlock();
					boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
					boolean upFree = true;
					for (int i = 1; i <= MathHelper.floor_double(entity.height) && upFree; i++)
						if (!WorldEntitySpawner.isValidEmptySpawnBlock(worldIn.getBlockState(pos.up(i))))
							upFree = false;
					return flag && upFree && WorldEntitySpawner.isValidEmptySpawnBlock(iblockstate);
				}
			}
		}
	}
}
