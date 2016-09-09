package mrriegel.limelib.gui;

import java.io.IOException;

import mrriegel.limelib.gui.element.GuiElement;
import mrriegel.limelib.gui.element.IClickable;
import mrriegel.limelib.gui.element.IScrollable;
import mrriegel.limelib.gui.element.ITooltip;
import mrriegel.limelib.gui.element.MCPanel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class CommonGuiContainer extends GuiContainer implements IGui {

	protected GuiDrawer drawer;
	protected MCPanel panel;

	public CommonGuiContainer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (panel != null) {
			panel.drawForeground(mouseX, mouseY);
			if (panel instanceof ITooltip && panel.isMouseOver(mouseX, mouseY))
				((ITooltip) panel).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
		}
		for (GuiButton e : buttonList)
			if (e instanceof ITooltip)
				if (e.isMouseOver())
					((ITooltip) e).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (panel != null)
			panel.drawBackground(mouseX, mouseY);
	}

	@Override
	public void initGui() {
		super.initGui();
		drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize, zLevel);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize, zLevel);
		onUpdate();
		if (panel != null)
			panel.onUpdate();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (panel != null && panel.isMouseOver(mouseX, mouseY)) {
			if (panel instanceof IClickable)
				((IClickable) panel).onClick(mouseButton);
			for (GuiElement e : panel.getElements())
				if (e instanceof IClickable && e.isMouseOver(mouseX, mouseY))
					((IClickable) e).onClick(mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (panel != null && panel.isMouseOver(mouseX, mouseY)) {
			if (panel instanceof IClickable)
				((IClickable) panel).onRelease(state);
			for (GuiElement e : panel.getElements())
				if (e instanceof IClickable && e.isMouseOver(mouseX, mouseY))
					((IClickable) e).onRelease(state);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		if (panel != null && panel.isMouseOver(mouseX, mouseY)) {
			if (panel instanceof IScrollable)
				((IScrollable) panel).onScrolled(Mouse.getEventDWheel());
			for (GuiElement e : panel.getElements())
				if (e instanceof IScrollable && e.isMouseOver(mouseX, mouseY))
					((IScrollable) e).onScrolled(Mouse.getEventDWheel());
		}

	}

	protected void onUpdate() {
	}

}
