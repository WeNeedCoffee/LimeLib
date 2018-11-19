package mrriegel.limelib.farm;

import org.lwjgl.opengl.GL11;

import mrriegel.limelib.farm.TileFarm.Farmer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

//@Mod(modid = "farmmod", name = "Farmer", version = "1.0.0")
//@EventBusSubscriber
public class FarmMod {

	@Instance("farmmod")
	public static FarmMod INSTANCE;

	public final SimpleNetworkWrapper snw = new SimpleNetworkWrapper("farm");

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		snw.registerMessage(MessageToC.class, MessageToC.class, 0, Side.CLIENT);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.world != null) {
			for (TileEntity t : mc.world.loadedTileEntityList) {
				if (t instanceof TileFarm) {
					TileFarm tf = (TileFarm) t;
					for (Farmer ent : tf.farmers) {
						//						System.out.println(ent.posX+" "+ent.lastX);
						double x = (ent.lastX + (ent.posX - ent.lastX) * event.getPartialTicks()) - TileEntityRendererDispatcher.staticPlayerX;
						double y = (ent.lastY + (ent.posY - ent.lastY) * event.getPartialTicks()) - TileEntityRendererDispatcher.staticPlayerY;
						double z = (ent.lastZ + (ent.posZ - ent.lastZ) * event.getPartialTicks()) - TileEntityRendererDispatcher.staticPlayerZ;
						GlStateManager.pushMatrix();
						y += Math.sin((Minecraft.getMinecraft().world.getTotalWorldTime() + event.getPartialTicks()) / 7) * .05;
						GlStateManager.translate(x, y, z);
						GlStateManager.rotate(90, 1, 0, 0);
						double deg = 22.5;
						double angle = Math.sin((Minecraft.getMinecraft().world.getTotalWorldTime() + event.getPartialTicks()) / 3) * deg;
						angle += deg;
						GL11.glRotated(-angle, 0, 0, 1);
						RenderHelper.enableStandardItemLighting();
						Minecraft.getMinecraft().getRenderItem().renderItem(ent.s, ItemCameraTransforms.TransformType.FIXED);
						RenderHelper.disableStandardItemLighting();
						GlStateManager.popMatrix();
					}
				}
			}
		}
	}

	static Block block;

	@SubscribeEvent
	public static void register(Register event) {
		if (event.getGenericType() == Block.class) {
			block = new Block(Material.WOOD) {
				@Override
				public boolean hasTileEntity(IBlockState state) {
					return true;
				}

				@Override
				public TileEntity createTileEntity(World world, IBlockState state) {
					return new TileFarm();
				}
			};
			block.setRegistryName("farm");
			block.setUnlocalizedName(block.getRegistryName().toString());
			block.setHardness(3f);
			event.getRegistry().register(block);
			GameRegistry.registerTileEntity(TileFarm.class, block.getRegistryName());
		} else if (event.getGenericType() == Item.class) {
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()).setUnlocalizedName(block.getUnlocalizedName()));
		}

	}
}
