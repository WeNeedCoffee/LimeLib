package mrriegel.limelib.gui;

import mrriegel.limelib.gui.element.IGuiElement;
import mrriegel.limelib.gui.element.ITooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public abstract class CommonGuiScreen extends GuiScreen {

	protected int xSize = 176;
	protected int ySize = 166;
	protected int guiLeft;
	protected int guiTop;

	protected GuiDrawer drawer;

	public CommonGuiScreen() {
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
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (GuiButton e : buttonList) {
			if (e instanceof IGuiElement)
				((IGuiElement) e).drawForeground(mouseX - guiLeft, mouseY - guiTop);
			zLevel += 500;
			if (e instanceof ITooltip) {
				if (e.isMouseOver())
					((ITooltip) e).drawTooltip(mouseX - guiLeft, mouseY - guiTop);
			}
			zLevel -= 500;
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	protected void onUpdate() {
	}

}
