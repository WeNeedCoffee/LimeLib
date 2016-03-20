package mrriegel.limelib.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class GuiElement extends Gui {
	int id, x, y;
	public boolean visible;
	protected boolean hovered;

	public GuiElement(int id, int x, int y) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.visible = true;
	}

	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
	};

	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
	};

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return false;
	};

	public boolean mouseReleased(Minecraft mc, int mouseX, int mouseY) {
		return false;
	};

	public void handleMouseInput() {
	};

	public boolean isMouseOver() {
		return this.hovered;
	}

}
