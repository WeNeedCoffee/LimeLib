package mrriegel.limelib.helper;

import java.awt.Point;

import org.lwjgl.input.Mouse;

import mrriegel.limelib.util.StackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

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
		if (mouseOver && isMouseOver(x, y, 16, 16)) {
//			System.out.println("mouseobe");
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int j1 = x;
			int k1 = y;
			GlStateManager.colorMask(true, true, true, false);
			drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433,0);
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
//		System.out.println(i1+"  "+mc.displayWidth);
		final int l1 = j1 - Mouse.getY() * j1 / mc.displayHeight - 1;
		return new Point(k1, l1);
	}

	public static boolean isMouseOver(int x, int y, int w, int h) {
		// x = x - guiLeft;
		// y= y - guiTop;
		Point m=getMousePosition();
		return x >= m.x - 1 && x < m.x + w + 1
				&& y >= m.y - 1 && y < m.y + h + 1;
	}

	public static void drawGradientRect(int left, int top, int right, int bottom,
			int startColor, int endColor, int zLevel) {
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
}
