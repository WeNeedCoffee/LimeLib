package mrriegel.limelib.inworld;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.ColorHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//@EventBusSubscriber
//@Mod(modid = "inworld", name = "inWorld", version = "1.0.0")
public class TheMod {

	@Instance("inworld")
	public static TheMod mod;

	public static CommonBlock border = new BlockBorder();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CommonBlock compressor = new CommonBlockContainer<TileCompressor>(Material.IRON, "compi") {

			@Override
			protected Class<? extends TileCompressor> getTile() {
				return TileCompressor.class;
			}

			{
				setCreativeTab(CreativeTabs.REDSTONE);
			}
		};
		compressor.registerBlock();
		border.registerBlock();
		//		int a = 4 / 0;
	}

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		for (TileEntity t : player.world.loadedTileEntityList) {
			if (!(t instanceof TileCompressor))
				continue;
			TileCompressor tc = (TileCompressor) t;
			if (tc.boxSize <= 0)
				continue;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			float width = (float) (Math.sin(System.currentTimeMillis() / 190d) + 2f) * 2f;
			GlStateManager.glLineWidth(width);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos blockpos = t.getPos();

			double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
			double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
			double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
			int color = ColorHelper.getRainbow(25);
			color = 0;
			RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(0, 0, 0, tc.boxSize, tc.boxSize, tc.boxSize).offset(blockpos).offset(tc.offset).grow(-0.02).offset(-d3, -d4, -d5), ColorHelper.getRed(color) / 255f, ColorHelper.getGreen(color) / 255f, ColorHelper.getBlue(color) / 255f, 0.9F);

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
