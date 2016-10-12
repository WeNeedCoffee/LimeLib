package mrriegel.limelib.gui;

import java.io.IOException;
import java.util.List;

import mrriegel.limelib.gui.component.MCPanel;
import mrriegel.limelib.gui.element.GuiElement;
import mrriegel.limelib.gui.element.ITooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

public class CommonGuiScreen extends GuiScreen {

	protected int xSize = 176;
	protected int ySize = 166;
	protected int guiLeft;
	protected int guiTop;

	protected GuiDrawer drawer;
	protected MCPanel panel;
	protected List<GuiElement> elementList = Lists.newArrayList();

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (panel != null) {
			panel.drawForeground(mouseX, mouseY);
			if (panel instanceof ITooltip && panel.isMouseOver(mouseX, mouseY))
				((ITooltip) panel).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
		}
		for (GuiElement e : elementList)
			if (e.isMouseOver(mouseX, mouseY) && e instanceof ITooltip)
				((ITooltip) e).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
		for (GuiButton e : buttonList)
			if (e instanceof ITooltip)
				if (e.isMouseOver())
					((ITooltip) e).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (panel != null)
			panel.drawBackground(mouseX, mouseY);
		for (GuiElement e : elementList)
			e.draw(mouseX, mouseY);
	}

	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		int i = this.guiLeft;
		int j = this.guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize, zLevel);
		elementList.clear();
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
		int mouseX = GuiDrawer.getMouseX();
		int mouseY = GuiDrawer.getMouseY();
		for (GuiElement e : elementList)
			if (e.isMouseOver(mouseX, mouseY))
				e.onScrolled(Mouse.getEventDWheel());
	}

	protected void elementClicked(GuiElement element) {
	}

	protected void onUpdate() {
	}

}
