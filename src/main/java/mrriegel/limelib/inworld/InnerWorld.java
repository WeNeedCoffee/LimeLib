package mrriegel.limelib.inworld;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.DimensionType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class InnerWorld extends WorldServer {
	Object2ObjectMap<BlockPos, IBlockState> blockMap = new Object2ObjectOpenHashMap<>();
	Object2ObjectMap<BlockPos, TileEntity> tileMap = new Object2ObjectOpenHashMap<>();
	private final World world;
	private final int size;
	private final TileCompressor tile;

	private IBlockState border;

	public InnerWorld(World world, int size, TileCompressor tile) {
		super(world.getMinecraftServer(), null, world.getWorldInfo(), dim(), world.getMinecraftServer().profiler);
		Field f = ReflectionHelper.findField(World.class, "field_73011_w", "provider");
		try {
			f.set(this, world.provider);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		DimensionManager.unregisterDimension(a);
		DimensionManager.setWorld(a, null, world.getMinecraftServer());
		this.blockMap.defaultReturnValue(Blocks.AIR.getDefaultState());
		this.world = world;
		this.mapStorage = new MapStorage(null);
		this.perWorldStorage = new MapStorage(null);
		this.villageCollection = new VillageCollection(this);
		this.lootTable = new LootTableManager(null);
		this.size = size;
		this.tile = tile;

	}

	static Integer a = null;

	static int dim() {
		if (a == null)
			do {
				a = new Random().nextInt(200) - 100;
			} while (DimensionManager.isDimensionRegistered(a));
		DimensionManager.registerDimension(a, DimensionType.OVERWORLD);
		return a;
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return new ChunkProviderServer(this, new AnvilChunkLoader(null, null) {

			@Override
			public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
			}

			@Override
			public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
			}

			@Override
			public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
				return null;
			}

			@Override
			public boolean isChunkGeneratedAt(int x, int z) {
				return false;
			}

			@Override
			public void flush() {
			}
		}, new IChunkGenerator() {

			@Override
			public void recreateStructures(Chunk chunkIn, int x, int z) {
			}

			@Override
			public void populate(int x, int z) {
			}

			@Override
			public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
				return false;
			}

			@Override
			public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
				return Collections.emptyList();
			}

			@Override
			public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
				return null;
			}

			@Override
			public boolean generateStructures(Chunk chunkIn, int x, int z) {
				return false;
			}

			@Override
			public Chunk generateChunk(int x, int z) {
				return null;
			}
		});
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return true;
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		if (inCube(pos))
			return tileMap.get(pos);
		if (inBorder(pos))
			return new BlockBorder.TileBorder(tile);
		return null;
	}

	@Override
	public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
		if (!inCube(pos))
			return;
		pos = pos.toImmutable();
		if (tileEntityIn == null) {
			tileMap.put(pos, null);
			return;
		}
		TileEntity t = null;
		try {
			t = ConstructorUtils.invokeConstructor(tileEntityIn.getClass());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
		if (t != null) {
			t.readFromNBT(tileEntityIn.writeToNBT(new NBTTagCompound()));
			t.setWorld(this);
			t.setPos(pos);
		}
		tileMap.put(pos, t);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		if (inCube(pos))
			return blockMap.get(pos);
		if (inBorder(pos))
			return TheMod.border.getDefaultState();
		return Blocks.BEDROCK.getDefaultState();

	}

	@Override
	public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
		if (!inCube(pos))
			return false;
		pos = pos.toImmutable();
		blockMap.put(pos, newState);
		setTileEntity(pos, newState.getBlock().createTileEntity(this, newState));
		return true;
	}

	@Override
	public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
	}

	@Override
	public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
		return new Chunk(this, chunkX, chunkZ);
	}

	@Override
	public LootTableManager getLootTableManager() {
		return world.getLootTableManager();
	}

	private boolean inCube(BlockPos pos) {
		return pos.getX() >= 0 && pos.getX() <= size && pos.getY() >= 0 && pos.getY() <= size && pos.getZ() >= 0 && pos.getZ() <= size;
	}

	private boolean inBorder(BlockPos pos) {
		if (pos.getY() == -1 || pos.getY() == size + 1)
			return pos.getX() >= 0 && pos.getX() <= size && pos.getZ() >= 0 && pos.getZ() <= size;
		if (pos.getX() == -1 || pos.getX() == size + 1)
			return pos.getY() >= 0 && pos.getY() <= size && pos.getZ() >= 0 && pos.getZ() <= size;
		if (pos.getZ() == -1 || pos.getZ() == size + 1)
			return pos.getX() >= 0 && pos.getX() <= size && pos.getY() >= 0 && pos.getY() <= size;
		return false;
	}

	private boolean outside(BlockPos pos) {
		return !inCube(pos) && !inBorder(pos);
	}

	public int getSize() {
		return size;
	}
}