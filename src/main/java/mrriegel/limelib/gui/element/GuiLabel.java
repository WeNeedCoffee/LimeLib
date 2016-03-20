package mrriegel.limelib.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLabel extends GuiElement {

	public String text;
	public int color;
	protected boolean shadow;

	public GuiLabel(int id, int x, int y, String text, int color, boolean shadow) {
		super(id, x, y);
		this.text = text;
		this.color = color;
		this.shadow = shadow;
	}

	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		text = text.trim();
		this.hovered = mouseX >= this.x && mouseY >= this.y
				&& mouseX < this.x + mc.fontRendererObj.getStringWidth(text)
				&& mouseY < this.y + mc.fontRendererObj.FONT_HEIGHT;
		mc.fontRendererObj.drawString(text, x, y, color
				+ (hovered ? 0x222200 : 0), shadow);
		GlStateManager.color(1f, 1f, 1f, 1f);
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}

	@Override
	public boolean mouseReleased(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}
}
