package mrriegel.limelib.gui.element;

import java.util.Arrays;

import mrriegel.limelib.helper.GuiHelper;
import mrriegel.limelib.util.StackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.input.Keyboard;

public class GuiSlot extends GuiElement {

	public StackWrapper stack;
	private boolean smallFont, toolTip;
	public boolean square;
	public boolean number;

	public GuiSlot(int id, int x, int y, StackWrapper stack, boolean smallFont,
			boolean toolTip, boolean square, boolean number) {
		super(id, x, y);
		this.stack = stack;
		this.smallFont = smallFont;
		this.toolTip = toolTip;
		this.square = square;
		this.number = number;
	}

	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack.getStack(), x, y);
		this.hovered = mouseX >= this.x && mouseY >= this.y
				&& mouseX < this.x + 16 && mouseY < this.y + 16;
		int size = stack.getSize();
		String amount = size < 1000 ? String.valueOf(size)
				: size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
		if (number) {
			if (smallFont) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(.5f, .5f, .5f);
				mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj,
						stack.getStack(), x * 2 + 16, y * 2 + 16, amount);
				GlStateManager.popMatrix();
			} else
				mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj,
						stack.getStack(), x, y, amount);
		}
		RenderHelper.disableStandardItemLighting();
		if (square && hovered) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int j1 = x;
			int k1 = y;
			GlStateManager.colorMask(true, true, true, false);
			drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}

	@Override
	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
		if (this.hovered && this.toolTip) {
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				GuiHelper.renderToolTip(stack.getStack(), mouseX, mouseY);
			else
				GuiHelper.drawHoveringText(
						Arrays.asList(new String[] { "Amount: "
								+ String.valueOf(stack.getSize()) }), mouseX,
						mouseY, mc.fontRendererObj);
			GlStateManager.popMatrix();

		}
	}

	@Override
	public boolean mouseReleased(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}
}
