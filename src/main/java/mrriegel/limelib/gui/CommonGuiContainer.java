package mrriegel.limelib.gui;

import mrriegel.limelib.gui.element.IGuiElement;
import mrriegel.limelib.gui.element.ITooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class CommonGuiContainer extends GuiContainer {

	protected GuiDrawer drawer;

	public CommonGuiContainer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawer = new GuiDrawer(guiLeft, guiTop, xSize, ySize, zLevel);
		onUpdate();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void onUpdate() {
	}

}
