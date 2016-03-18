package mrriegel.limelib.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiBase extends GuiScreen {
	public int xSize = 176;
	public int ySize = 166;
	public int guiLeft;
	public int guiTop;

	public GuiBase(int xSize, int ySize) {
		super();
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}
}
