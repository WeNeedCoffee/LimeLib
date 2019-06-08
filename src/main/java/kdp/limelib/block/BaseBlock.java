package kdp.limelib.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BaseBlock extends Block {

    protected boolean hasTile = false;

    public BaseBlock(Properties properties, String name) {
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
