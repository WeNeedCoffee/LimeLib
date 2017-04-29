package mrriegel.limelib;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.ClientEventHandler;
import mrriegel.limelib.util.EventHandler;
import mrriegel.limelib.util.Serious;
import mrriegel.limelib.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LimeLib.MODID, name = LimeLib.NAME, version = LimeLib.VERSION)
public class LimeLib {

	@Instance(LimeLib.MODID)
	public static LimeLib INSTANCE;

	public static final String VERSION = "1.5.4";
	public static final String NAME = "LimeLib";
	public static final String MODID = "limelib";

	public static final Logger log = LogManager.getLogger(NAME);

	@SidedProxy(clientSide = "mrriegel.limelib.LimeClientProxy", serverSide = "mrriegel.limelib.LimeCommonProxy")
	public static LimeCommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.init(event.getSuggestedConfigurationFile());
		Utils.init();
		CapabilityDataPart.register();
		wailaLoaded = Loader.isModLoaded("waila");
		jeiLoaded = Loader.isModLoaded("jei");
		teslaLoaded = Loader.isModLoaded("tesla");
	}

	public static boolean wailaLoaded, jeiLoaded, teslaLoaded;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		MinecraftForge.EVENT_BUS.register(EventHandler.class);
		if (event.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		}
		Serious.init();
		MinecraftForge.EVENT_BUS.register(LimeLib.class);
	}

//	static Map<BlockPos, Double> countDown = Maps.newHashMap();
//
//	@SubscribeEvent
//	public static void render(RenderWorldLastEvent event) {
//		Minecraft mc = Minecraft.getMinecraft();
//		if (mc != null && mc.world != null && mc.player != null) {
//			for (BlockPos pos : countDown.keySet()) {
//				Double d = countDown.get(pos);
//				if (d == null || d <= 1.) {
//					countDown.remove(pos);
//					break;
//				}
//				countDown.put(pos, d.doubleValue() - .012);
//				//				pos=new BlockPos(199, 4, -846);
//				mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//				BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
//				IBlockState iblockstate = Blocks.FURNACE.getDefaultState();
//				GlStateManager.pushMatrix();
//				RenderHelper.disableStandardItemLighting();
//
//				Tessellator tessellator = Tessellator.getInstance();
//				VertexBuffer worldrenderer = tessellator.getBuffer();
//				worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
//
//				int i = pos.getX();
//				int j = pos.getY();
//				int k = pos.getZ();
//				double x = TileEntityRendererDispatcher.staticPlayerX;
//				double y = TileEntityRendererDispatcher.staticPlayerY;
//				double z = TileEntityRendererDispatcher.staticPlayerZ;
//				GlStateManager.translate(i - x, j - y, k - z);
//				GlStateManager.scale(d, d, d);
//				GlStateManager.translate((-(d - 1) / d) / 2, 0, (-(d - 1) / d) / 2);
//
//				worldrenderer.setTranslation(((-i)), (-j), ((-k)));
//				worldrenderer.color(1F, 1F, 1F, 1F);
//				IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(iblockstate);
//				blockrendererdispatcher.getBlockModelRenderer().renderModel(mc.world, ibakedmodel, iblockstate, pos, worldrenderer, true);
//
//				worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
//				tessellator.draw();
//				GlStateManager.scale(-d, -d, -d);
//				RenderHelper.enableStandardItemLighting();
//				GlStateManager.popMatrix();
//			}
//		}
//	}
//
//	@SubscribeEvent
//	public static void click(RightClickBlock event) {
//		if (event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Items.STICK && event.getFace().getAxis() != Axis.Y) {
//			BlockPos hit = event.getPos();
//
//			Set<BlockPos> it = Sets.newHashSet(BlockPos.getAllInBox(hit.up().offset(event.getFace().rotateY()), hit.down().offset(event.getFace().rotateYCCW()).offset(event.getFace().getOpposite(), 2)));
//			BlockPos fur = event.getPos().down().offset(event.getFace().getOpposite());
//			it.remove(fur.up());
//			boolean valid = event.getWorld().isAirBlock(fur.up());
//			for (BlockPos p : it) {
//				valid &= event.getWorld().getBlockState(p).getBlock() == Blocks.COBBLESTONE;
//			}
//			if (valid) {
//				countDown.put(fur, 3.);
//				for (BlockPos p : it) {
//					event.getWorld().setBlockState(p, Blocks.AIR.getDefaultState());
//				}
//				event.getWorld().setBlockState(fur, Blocks.FURNACE.getDefaultState());
//			}
//		}
//	}
}
