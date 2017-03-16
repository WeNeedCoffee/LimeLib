package mrriegel.limelib.util;

import java.awt.Color;
import java.util.Iterator;
import java.util.stream.Collectors;

import mrriegel.limelib.Config;
import mrriegel.limelib.LimeLib;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.datapart.RenderRegistry;
import mrriegel.limelib.datapart.RenderRegistry.RenderDataPart;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.EnergyHelper;
import mrriegel.limelib.helper.EnergyHelper.Energy;
import mrriegel.limelib.helper.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientEventHandler {

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(ParticleHelper.roundParticle);
		event.getMap().registerSprite(ParticleHelper.sparkleParticle);
		event.getMap().registerSprite(ParticleHelper.squareParticle);
	}

	@SubscribeEvent
	public static void renderEnergy(Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (!Config.showEnergy || mc == null || mc.world == null || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || mc.objectMouseOver.getBlockPos() == null || mc.world.getTileEntity(mc.objectMouseOver.getBlockPos()) == null)
			return;
		BlockPos p = mc.objectMouseOver.getBlockPos();
		if (event.getType() == ElementType.TEXT && LimeLib.proxy.energyTiles().containsKey(p)) {
			Energy energyType;
			if ((energyType = EnergyHelper.isEnergyInterface(mc.world, p)) == null) {
				LimeLib.proxy.energyTiles().remove(p);
				return;
			}
			ScaledResolution sr = event.getResolution();
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GuiDrawer drawer = new GuiDrawer(0, 0, 0, 0, 0);
			long energy = LimeLib.proxy.energyTiles().get(p).getLeft(), max = LimeLib.proxy.energyTiles().get(p).getRight();
			String text = (!GuiScreen.isShiftKeyDown() ? Utils.formatNumber(energy) : energy) + "/" + (!GuiScreen.isShiftKeyDown() ? Utils.formatNumber(max) : max) + " " + energyType.unit;
			int lenght = 90/*mc.fontRenderer.getStringWidth(text)*/;
			mc.fontRenderer.drawString(text, (sr.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2, (sr.getScaledHeight() - 15 - mc.fontRenderer.FONT_HEIGHT) / 2, GuiScreen.isShiftKeyDown() ? 0xffff00 : 0x80ffff00, true);
			boolean before = mc.fontRenderer.getUnicodeFlag();
			mc.fontRenderer.setUnicodeFlag(true);
			String config = "Can be disabled in LimeLib config.";
			mc.fontRenderer.drawString(config, (sr.getScaledWidth() - mc.fontRenderer.getStringWidth(config)) / 2, (sr.getScaledHeight() + 40 - mc.fontRenderer.FONT_HEIGHT) / 2, 0x40ffff00, true);
			mc.fontRenderer.setUnicodeFlag(before);
			drawer.drawEnergyBarH((sr.getScaledWidth() - lenght) / 2, (sr.getScaledHeight() + 20 - 8) / 2, lenght, (float) ((double) energy / (double) max));
			drawer.drawFrame((sr.getScaledWidth() - lenght) / 2 - 1, (sr.getScaledHeight() + 20 - 8) / 2 - 1, lenght + 2, 9, 1, ColorHelper.darker(Color.RED.getRGB(), .8));
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void tick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.phase == Phase.END && mc.world != null && !mc.isGamePaused()) {
			DataPartRegistry reg = DataPartRegistry.get(mc.world);
			if (reg != null) {
				Iterator<DataPart> it = reg.getParts().stream().filter(p -> p != null && mc.world.isBlockLoaded(p.getPos())).collect(Collectors.toList()).iterator();
				while (it.hasNext()) {
					DataPart part = it.next();
					part.updateClient(mc.world);
					part.ticksExisted++;
				}
			}
		}
	}

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		DataPartRegistry reg = DataPartRegistry.get(Minecraft.getMinecraft().world);
		if (reg != null) {
			Iterator<DataPart> it = reg.getParts().stream().filter(p -> p != null).collect(Collectors.toList()).iterator();
			while (it.hasNext()) {
				DataPart p = it.next();
				@SuppressWarnings("rawtypes")
				RenderDataPart ren = RenderRegistry.map.get(p.getClass());
				if (ren != null)
					ren.render(p, p.getX() - TileEntityRendererDispatcher.staticPlayerX, p.getY() - TileEntityRendererDispatcher.staticPlayerY, p.getZ() - TileEntityRendererDispatcher.staticPlayerZ, event.getPartialTicks());
			}
		}
	}

	@SubscribeEvent
	public static void draw(DrawBlockHighlightEvent event) {
		DataPart part = DataPart.rayTrace(event.getPlayer());
		if (part != null && part.getHighlightBox() != null) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos blockpos = part.getPos();
			double d0 = TileEntityRendererDispatcher.staticPlayerX;
			double d1 = TileEntityRendererDispatcher.staticPlayerY;
			double d2 = TileEntityRendererDispatcher.staticPlayerZ;
			RenderGlobal.drawSelectionBoundingBox(part.getHighlightBox().offset(blockpos).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2), 0.0F, 0.0F, 0.0F, 0.4F);
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			event.setCanceled(true);
		}
	}

}
