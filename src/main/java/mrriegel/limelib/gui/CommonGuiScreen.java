package mrriegel.limelib.gui;

import java.io.IOException;

import mrriegel.limelib.gui.element.GuiElement;
import mrriegel.limelib.gui.element.IClickable;
import mrriegel.limelib.gui.element.IScrollable;
import mrriegel.limelib.gui.element.ITooltip;
import mrriegel.limelib.gui.element.MCPanel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class CommonGuiScreen extends GuiScreen {

	protected int xSize = 176;
	protected int ySize = 166;
	protected int guiLeft;
	protected int guiTop;

	protected GuiDrawer drawer;
	protected MCPanel panel;

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

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (panel != null)
			panel.drawBackground(mouseX, mouseY);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize, zLevel);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize, zLevel);
		onUpdate();
		if (panel != null)
			panel.onUpdate();
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(i, j, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
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
