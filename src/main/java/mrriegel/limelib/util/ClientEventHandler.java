package mrriegel.limelib.util;

import java.awt.Color;
import java.util.Map;

import mrriegel.limelib.Config;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.EnergyHelper;
import mrriegel.limelib.helper.EnergyHelper.Energy;
import mrriegel.limelib.helper.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

public class ClientEventHandler {

	public static Map<BlockPos, Pair<Long, Long>> energyTiles = Maps.newHashMap();

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
		if (event.getType() == ElementType.TEXT && energyTiles.containsKey(p)) {
			Energy energyType;
			if ((energyType = EnergyHelper.isEnergyInterface(mc.world, p)) == null) {
				energyTiles.remove(p);
				return;
			}
			ScaledResolution sr = event.getResolution();
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GuiDrawer drawer = new GuiDrawer(0, 0, 0, 0, 0);
			long energy = energyTiles.get(p).getLeft(), max = energyTiles.get(p).getRight();
			String text = (!GuiScreen.isShiftKeyDown() ? Utils.formatNumber(energy) : energy) + "/" + (!GuiScreen.isShiftKeyDown() ? Utils.formatNumber(max) : max) + " " + energyType.unit;
			int lenght = 90/*mc.fontRendererObj.getStringWidth(text)*/;
			mc.fontRendererObj.drawString(text, (sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(text)) / 2, (sr.getScaledHeight() - 15 - mc.fontRendererObj.FONT_HEIGHT) / 2, GuiScreen.isShiftKeyDown() ? 0xffff00 : 0x80ffff00, true);
			boolean before = mc.fontRendererObj.getUnicodeFlag();
			mc.fontRendererObj.setUnicodeFlag(true);
			String config = "Can be disabled in LimeLib config.";
			mc.fontRendererObj.drawString(config, (sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(config)) / 2, (sr.getScaledHeight() + 40 - mc.fontRendererObj.FONT_HEIGHT) / 2, 0x40ffff00, true);
			mc.fontRendererObj.setUnicodeFlag(before);
			drawer.drawEnergyBarH((sr.getScaledWidth() - lenght) / 2, (sr.getScaledHeight() + 20 - 8) / 2, lenght, (float) ((double) energy / (double) max));
			drawer.drawFrame((sr.getScaledWidth() - lenght) / 2 - 1, (sr.getScaledHeight() + 20 - 8) / 2 - 1, lenght + 2, 9, 1, ColorHelper.darker(Color.RED.getRGB(), .8));
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}
