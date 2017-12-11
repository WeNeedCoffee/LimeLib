package mrriegel.limelib.inworld;

import java.util.Random;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.helper.RegistryHelper;
import mrriegel.limelib.tile.CommonTile;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockBorder extends CommonBlock {

	public BlockBorder() {
		super(Material.ROCK, "border");
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setSoundType(SoundType.STONE);
		disableStats();
	}

	public int quantityDropped(Random random) {
		return 0;
	}

	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public void registerBlock() {
		RegistryHelper.register(this);
		GameRegistry.registerTileEntity(TileBorder.class, getUnlocalizedName());
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	public static class TileBorder extends CommonTile {
		TileCompressor tile;

		public TileBorder(TileCompressor tile) {
			this.tile = tile;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			TileEntity t = tile.getWorld().getTileEntity(tile.getPos().offset(facing.getOpposite()));
			if (t != null)
				return t.hasCapability(capability, facing);
			return false;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			TileEntity t = tile.getWorld().getTileEntity(tile.getPos().offset(facing.getOpposite()));
			if (t != null)
				return t.getCapability(capability, facing);
			return null;
		}
	}

}
