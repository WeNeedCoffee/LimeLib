package mrriegel.limelib.helper;

import java.awt.Point;
import java.util.List;

import mrriegel.limelib.gui.IGuiBase;
import mrriegel.limelib.util.StackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Mouse;

public class GuiHelper {
	private static final Minecraft mc = Minecraft.getMinecraft();

	public static void drawItemStack(ItemStack stack, int x, int y,
			boolean mouseOver) {
		if (stack == null)
			return;
		drawStackWrapper(new StackWrapper(stack, stack.stackSize), x, y,
				mouseOver, false);
	}

	public static void drawStackWrapper(StackWrapper stackWrapper, int x,
			int y, boolean mouseOver, boolean smallFont) {
		if (stackWrapper.getStack() == null)
			return;
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stackWrapper.getStack(),
				x, y);
		int size = stackWrapper.getSize();
		String amount = size == 1 ? "" : size < 1000 ? String.valueOf(size)
				: size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
		if (smallFont) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(.5f, .5f, .5f);
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj,
					stackWrapper.getStack(), x * 2 + 16, y * 2 + 16, amount);
			GlStateManager.popMatrix();
		} else
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj,
					stackWrapper.getStack(), x, y, amount);
		RenderHelper.disableStandardItemLighting();
		if (mouseOver && isPointInRegion(x, y, 16, 16)) {
			// System.out.println("mouseobe");
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int j1 = x;
			int k1 = y;
			GlStateManager.colorMask(true, true, true, false);
			drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433,
					-2130706433, 0);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}
	}

	public static Point getMousePosition() {
		final ScaledResolution scaledresolution = new ScaledResolution(mc);
		int i1 = scaledresolution.getScaledWidth();
		int j1 = scaledresolution.getScaledHeight();
		final int k1 = Mouse.getX() * i1 / mc.displayWidth;
		final int l1 = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;
		return new Point(k1, l1);
	}

	public static boolean isPointInRegion(int left, int top, int right,
			int bottom) {
		int pointX = getMousePosition().x;
		int pointY = getMousePosition().y;
		int i = ((IGuiBase) mc.currentScreen).getGuiLeft();
		int j = ((IGuiBase) mc.currentScreen).getGuiTop();
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= left - 1 && pointX < left + right + 1
				&& pointY >= top - 1 && pointY < top + bottom + 1;
	}

	public static void drawGradientRect(int left, int top, int right,
			int bottom, int startColor, int endColor, float zLevel) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) right, (double) top, (double) zLevel)
				.color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) top, (double) zLevel)
				.color(f1, f2, f3, f).endVertex();
		worldrenderer.pos((double) left, (double) bottom, (double) zLevel)
				.color(f5, f6, f7, f4).endVertex();
		worldrenderer.pos((double) right, (double) bottom, (double) zLevel)
				.color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static int getScreenWidth() {
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		return scaledresolution.getScaledWidth();
	}

	public static int getScreenHeight() {
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		return scaledresolution.getScaledHeight();
	}

	public static void drawHoveringText(List<String> textLines, int x, int y,
			FontRenderer font) {
		if (!textLines.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int i = 0;

			for (String s : textLines) {
				int j = font.getStringWidth(s);

				if (j > i) {
					i = j;
				}
			}

			int l1 = x + 12;
			int i2 = y - 12;
			int k = 8;

			if (textLines.size() > 1) {
				k += 2 + (textLines.size() - 1) * 10;
			}

			if (l1 + i > getScreenWidth()) {
				l1 -= 28 + i;
			}

			if (i2 + k + 6 > getScreenHeight()) {
				i2 = getScreenHeight() - k - 6;
			}

			float zLevel = 300.0F;
			mc.getRenderItem().zLevel = 300.0F;
			int l = -267386864;
			drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l, zLevel);
			drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l,
					zLevel);
			drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l,
					zLevel);
			drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l, zLevel);
			drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l,
					zLevel);
			int i1 = 1347420415;
			int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
			drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1,
					i1, j1, zLevel);
			drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3,
					i2 + k + 3 - 1, i1, j1, zLevel);
			drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1,
					zLevel);
			drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1,
					j1, zLevel);

			for (int k1 = 0; k1 < textLines.size(); ++k1) {
				String s1 = (String) textLines.get(k1);
				font.drawStringWithShadow(s1, (float) l1, (float) i2, -1);

				if (k1 == 0) {
					i2 += 2;
				}

				i2 += 10;
			}

			mc.getRenderItem().zLevel = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
		}
	}

	public static void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(mc.thePlayer,
				mc.gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i,
						stack.getRarity().rarityColor + (String) list.get(i));
			} else {
				list.set(i, EnumChatFormatting.GRAY + (String) list.get(i));
			}
		}

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		drawHoveringText(list, x, y, (font == null ? mc.fontRendererObj : font));
	}
}
