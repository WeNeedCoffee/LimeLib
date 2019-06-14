package kdp.limelib.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class GenericBlock extends Block {

    protected boolean hasTile = false;
    protected BlockItem blockItem;

    public GenericBlock(Properties properties, String name) {
        super(properties);
        setRegistryName(name);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return hasTile;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return super.createTileEntity(state, world);
    }
}
