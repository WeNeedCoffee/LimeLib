package kdp.limelib.block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import kdp.limelib.LimeLib;
import kdp.limelib.helper.RegistryHelper;
import kdp.limelib.tile.GenericTile;

public class GenericBlock extends Block {

    protected boolean hasTile = false;
    protected BlockItem blockItem;
    protected Class<? extends GenericTile> tileClass;

    public GenericBlock(Properties properties, String name) {
        super(properties);
        setRegistryName(name);
        blockItem = new BlockItem(this, new Item.Properties());
        blockItem.setRegistryName(name);
    }

    public BlockItem getBlockItem() {
        return blockItem;
    }

    public void register() {
        RegistryHelper.register(this);
        RegistryHelper.register(getBlockItem());
        if (LimeLib.DEV) {
            List<LinkedHashMap<String, Object>> pools = getPools();
            if (pools != null) {
                //boolean notAJar = ModList.get().getModContainerById(getRegistryName().getNamespace()).get().getMod()
                //        .getClass().getProtectionDomain().getCodeSource().getLocation() == null;
                //if (notAJar) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                LinkedHashMap<String, Object> json = new LinkedHashMap<>();
                json.put("type", "minecraft:block");
                json.put("pools", pools);
                File dir = new File("").toPath().resolve("../src/main/resources/data/" + getRegistryName()
                        .getNamespace() + "/loot_tables/blocks/").toFile();
                if (!dir.exists())
                    dir.mkdirs();
                try {
                    Files.write(new File(dir, getRegistryName().getPath() + ".json").toPath(),
                            gson.toJson(json).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //}
            }
        }

        if (tileClass != null && !Stream.of(tileClass.getConstructors()).anyMatch((c) -> c.getParameterCount() == 0))
            throw new IllegalStateException(tileClass + " needs a public default constructor.");
        //TODO RegistryHelper.register(tileClass);
    }

    @Nullable
    public List<LinkedHashMap<String, Object>> getPools() {
        ResourceLocation rl = Optional.ofNullable(getBlockItem()).map(Item::getRegistryName).orElse(null);
        if (rl == null) {
            return null;
        }
        LinkedHashMap<String, Object> pool = new LinkedHashMap<>();
        pool.put("rolls", 1);
        LinkedHashMap<String, Object> entry = new LinkedHashMap<>();
        entry.put("type", "minecraft:item");
        entry.put("name", rl.toString());
        pool.put("entries", Collections.singletonList(entry));
        pool.put("conditions",
                Collections.singletonList(Collections.singletonMap("condition", "minecraft:survives_explosion")));
        return Collections.singletonList(pool);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return tileClass != null;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (tileClass != null) {
            try {
                return tileClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            getTile(worldIn, pos).ifPresent(t -> {
                t.getDroppingItems()
                        .forEach(s -> InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), s));
                worldIn.updateComparatorOutputLevel(pos, this);
                worldIn.removeTileEntity(pos);
            });
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        getTile(worldIn, pos).ifPresent(t -> t.readFromStack(stack));
    }

    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
            BlockRayTraceResult hit) {
        Optional<GenericTile> tile = getTile(worldIn, pos);
        if (tile.isPresent()) {
            return tile.get().interact(player, handIn, hit);
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        GenericTile tile = Optional.ofNullable(builder.get(LootParameters.BLOCK_ENTITY))
                .filter(GenericTile.class::isInstance).map(GenericTile.class::cast).orElse(null);
        if (tile != null) {
            return tile.editDrops(drops, builder);
        }
        return drops;

    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        getTile(worldIn, pos).ifPresent(t -> t.neighborChanged(fromPos, isMoving));

    }

    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }

    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider) tileentity : null;
    }

    protected static Optional<GenericTile> getTile(World world, BlockPos pos) {
        return Optional.ofNullable(world.getTileEntity(pos)).filter(GenericTile.class::isInstance)
                .map(GenericTile.class::cast);
    }
}
