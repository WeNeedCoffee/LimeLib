package mrriegel.limelib.gui;

import net.minecraft.client.Minecraft;

public class GuiLabel {
	public String string;
	public int x, y, id, color;
	public boolean visible;
	protected boolean hovered;
	protected boolean shadow;

	public GuiLabel(int id, int x, int y, String string, int color,
			boolean shadow) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.color = color;
		this.string = string;
		this.visible = true;
		this.shadow = shadow;
	}

	public void drawString(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		string = string.trim();
		this.hovered = mouseX >= this.x && mouseY >= this.y
				&& mouseX < this.x + mc.fontRendererObj.getStringWidth(string)
				&& mouseY < this.y + mc.fontRendererObj.FONT_HEIGHT;
		mc.fontRendererObj.drawString(string, x, y, color
				+ (hovered ? 0x222200 : 0), shadow);
	}

	public boolean isMouseOver() {
		return this.hovered;
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}
}
