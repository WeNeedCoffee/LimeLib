package mrriegel.limelib.gui;

import java.io.IOException;
import java.util.List;

import mrriegel.limelib.gui.element.GuiElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

import com.google.common.collect.Lists;

public class GuiScreenBase extends GuiScreen implements IGuiBase {
	public int xSize;
	public int ySize;
	public int guiLeft;
	public int guiTop;

	protected List<GuiElement> elementList = Lists.<GuiElement> newArrayList();

	public GuiScreenBase(int xSize, int ySize) {
		super();
		this.xSize = xSize;
		this.ySize = ySize;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawGuiBackgroundLayer(partialTicks, mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (GuiElement e : elementList) {
			e.drawBackground(mc, mouseX, mouseY);
		}
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		drawGuiForegroundLayer(mouseX, mouseY);
		GlStateManager.popMatrix();
		for (GuiElement e : elementList) {
			e.drawForeground(mc, mouseX, mouseY);
		}

	}

	@Override
	public void drawGuiForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGuiBackgroundLayer(float partialTicks, int mouseX,
			int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initGui() {
		super.initGui();
		elementList=Lists.newArrayList();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiRight() {
		return guiLeft + xSize;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public int getGuiBottom() {
		return guiTop + ySize;
	}

	@Override
	public int getXSize() {
		return xSize;
	}

	@Override
	public int getYSize() {
		return ySize;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (GuiElement e : elementList) {
			if (e.mousePressed(mc, mouseX, mouseY)) {
				elementClicked(e, mouseButton);
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		for (GuiElement e : elementList) {
			if (e.mouseReleased(mc, mouseX, mouseY)) {
				elementReleased(e, state);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		for (GuiElement e : elementList) {
			e.handleMouseInput();
		}
	}

	protected void elementReleased(GuiElement e, int state) {
		
	}

	protected void elementClicked(GuiElement element, int mouseButton)
			throws IOException {
	}
	
	protected void elementMouseInput(GuiElement element, int mouseButton)
			throws IOException {
	}
	


	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
