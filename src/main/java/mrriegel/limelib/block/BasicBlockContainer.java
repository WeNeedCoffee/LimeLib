package mrriegel.limelib.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.lang3.text.WordUtils;

public abstract class BasicBlockContainer extends BasicBlock implements
		ITileEntityProvider {

	public BasicBlockContainer(Material material, String name, String modid) {
		super(material, name, modid);
		this.isBlockContainer = true;
		GameRegistry
				.registerTileEntity(
						createNewTileEntity(
								FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? Minecraft
										.getMinecraft().theWorld
										: FMLCommonHandler.instance()
												.getMinecraftServerInstance()
												.getEntityWorld(), 0)
								.getClass(),
						"tile" + WordUtils.capitalize(name));
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.removeTileEntity(pos);
	}

	@Override
	public boolean onBlockEventReceived(World worldIn, BlockPos pos,
			IBlockState state, int eventID, int eventParam) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(
				eventID, eventParam);
	}
}
