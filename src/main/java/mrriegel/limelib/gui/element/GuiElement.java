package mrriegel.limelib.gui.element;

import net.minecraft.client.Minecraft;
import mrriegel.limelib.gui.GuiDrawer;

public abstract class GuiElement {
	protected GuiDrawer drawer;
	public int x, y, width, height;
	protected boolean visible;
	protected MCPanel parent;
	protected Minecraft mc;

	public GuiElement(int x, int y, int width, int height, GuiDrawer drawer) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.drawer = drawer;
		visible = true;
		mc = Minecraft.getMinecraft();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	protected int getOffsetX() {
		int foo = 0;
		MCPanel pan = parent;
		while (pan != null) {
			foo += pan.x;
			pan = pan.parent;
		}
		return foo;
	}

	protected int getOffsetY() {
		int foo = 0;
		MCPanel pan = parent;
		while (pan != null) {
			foo += pan.y;
			pan = pan.parent;
		}
		return foo;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		int x = this.x + drawer.guiLeft + getOffsetX();
		int y = this.y + drawer.guiTop + getOffsetY();
		return mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
	}

	public abstract void drawForeground(int mouseX, int mouseY);

	public abstract void drawBackground(int mouseX, int mouseY);

	public void init() {
	}

	public void onUpdate() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuiElement other = (GuiElement) obj;
		if (height != other.height)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (visible != other.visible)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GuiElement [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", visible=" + visible + "]";
	}
}
