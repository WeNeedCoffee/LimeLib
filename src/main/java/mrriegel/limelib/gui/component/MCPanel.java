package mrriegel.limelib.gui.component;

import java.util.List;

import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.element.ITooltip;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class MCPanel extends GuiComponent {

	protected List<GuiComponent> children = Lists.newArrayList();

	public MCPanel(int x, int y, int width, int height, GuiDrawer drawer) {
		super(x, y, width, height, drawer);
		init();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		if (!visible)
			return;
		for (GuiComponent e : children)
			if (e.isVisible()) {
				e.drawForeground(mouseX, mouseY);
				if (e instanceof ITooltip && e.isMouseOver(mouseX, mouseY))
					((ITooltip) e).drawTooltip(mouseX - drawer.guiLeft, mouseY - drawer.guiTop);
			}
	}

	@Override
	public void drawBackground(int mouseX, int mouseY) {
		if (!visible)
			return;
		drawer.drawFramedRectangle(x + getOffsetX(), y + getOffsetY(), width, height);
		for (GuiComponent e : children)
			if (e.isVisible())
				e.drawBackground(mouseX, mouseY);

	}

	@Override
	public void onUpdate() {
		for (GuiComponent e : children)
			e.onUpdate();
	}

	public List<GuiComponent> getElements() {
		return ImmutableList.copyOf(children);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void add(GuiComponent element) {
		if (element == this)
			throw new IllegalArgumentException("adding panel's parent to itself");
		element.parent = this;
		children.add(element);
		if (!(element instanceof MCPanel))
			element.init();
	}

}
