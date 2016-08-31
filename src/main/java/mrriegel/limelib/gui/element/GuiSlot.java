package mrriegel.limelib.gui.element;

import mrriegel.limelib.util.StackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

public class GuiSlot extends GuiElement {

	public StackWrapper stack;
	private boolean smallFont, toolTip;
	public boolean square;
	public boolean number;

	public GuiSlot(GuiScreen parent, int id, int x, int y, StackWrapper stack, boolean smallFont, boolean toolTip, boolean square, boolean number) {
		super(id, x, y, parent);
		this.stack = stack;
		this.smallFont = smallFont;
		this.toolTip = toolTip;
		this.square = square;
		this.number = number;
	}

	@Override
	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack.getStack(), x, y);
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 16 && mouseY < this.y + 16;
		int size = stack.getSize();
		String amount = size < 1000 ? String.valueOf(size) : size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
		if (number) {
			if (smallFont) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(.5f, .5f, .5f);
				mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack.getStack(), x * 2 + 16, y * 2 + 16, amount);
				GlStateManager.popMatrix();
			} else
				mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack.getStack(), x, y, amount);
		}
		RenderHelper.disableStandardItemLighting();
		if (square && hovered) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int j1 = x;
			int k1 = y;
			GlStateManager.colorMask(true, true, true, false);
			GuiUtils.drawGradientRect(300, j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
		return this.visible && this.hovered;
	}

	@Override
	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
		if (this.hovered && this.toolTip) {
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				renderToolTip(stack.getStack(), mouseX, mouseY);
			else
				GuiUtils.drawHoveringText(Lists.newArrayList("Amount: " + String.valueOf(stack.getSize())), mouseX, mouseY, parent.width, parent.height, -1, mc.fontRendererObj);
			GlStateManager.popMatrix();

		}
	}

	@Override
	public boolean mouseReleased(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}
}
