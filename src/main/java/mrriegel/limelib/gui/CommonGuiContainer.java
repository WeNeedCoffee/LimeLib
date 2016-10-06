package mrriegel.limelib.gui;

import java.io.IOException;
import java.util.List;

import mrriegel.limelib.gui.component.MCPanel;
import mrriegel.limelib.gui.element.GuiElement;
import mrriegel.limelib.gui.element.ITooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

public class CommonGuiContainer extends GuiContainer {

	protected GuiDrawer drawer;
	protected MCPanel panel;
	protected List<GuiElement> elementList = Lists.newArrayList();

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
		for (GuiElement e : elementList)
			if (e.isMouseOver(mouseX, mouseY))
				e.drawTooltip(mouseX - guiLeft, mouseY - guiTop);
		for (GuiButton e : buttonList)
			if (e instanceof ITooltip)
				if (e.isMouseOver())
					((ITooltip) e).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (panel != null)
			panel.drawBackground(mouseX, mouseY);
		for (GuiElement e : elementList)
			e.draw(mouseX, mouseY);
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
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
		for (GuiElement e : elementList)
			if (e.isMouseOver(mouseX, mouseY)) {
				e.onClick(mouseButton);
				if (mouseButton == 0)
					elementClicked(e);
			}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		for (GuiElement e : elementList)
			if (e.isMouseOver(mouseX, mouseY))
				e.onRelease(state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;
		for (GuiElement e : elementList)
			if (e.isMouseOver(mouseX, mouseY))
				e.onScrolled(Mouse.getEventDWheel());
	}

	protected void elementClicked(GuiElement element) {
	}

	protected void onUpdate() {
	}

}
