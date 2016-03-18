package mrriegel.limelib.gui;

import java.util.Arrays;

import mrriegel.limelib.util.StackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

public class StackSlot extends Gui {
	StackWrapper stack;
	int x, y;
	GuiScreen gui;
	boolean smallFont;
	Minecraft mc = Minecraft.getMinecraft();

	public StackSlot(StackWrapper stack, int x, int y, GuiScreen gui,
			boolean smallFont) {
		super();
		this.stack = stack;
		this.x = x;
		this.y = y;
		this.gui = gui;
		this.smallFont = smallFont;
	}

	void drawSlot(int mx, int my) {
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack.getStack(), x, y);
		int size = stack.getSize();
		String amount = size < 1000 ? String.valueOf(size)
				: size < 1000000 ? size / 1000 + "K" : size / 1000000 + "M";
		if (smallFont) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(.5f, .5f, .5f);
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj,
					stack.getStack(), x * 2 + 16, y * 2 + 16, amount);
			GlStateManager.popMatrix();
		} else
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj,
					stack.getStack(), x, y, amount);
		if (this.isMouseOverSlot(mx, my)) {
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

	void drawTooltip(int mx, int my) {
		if (this.isMouseOverSlot(mx, my)) {
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				gui.renderToolTip(stack, mx, my);
			else
				drawHoveringText(
						Arrays.asList(new String[] { "Amount: "
								+ String.valueOf(size) }), mx, my);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}
	}

	private boolean isMouseOverSlot(int mouseX, int mouseY) {
		return isPointInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
	}
}
