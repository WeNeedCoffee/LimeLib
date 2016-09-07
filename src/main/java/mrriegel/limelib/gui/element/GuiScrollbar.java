package mrriegel.limelib.gui.element;

import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.GuiDrawer.Direction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiScrollbar extends GuiButtonExt {

	public int percent;
	protected Direction dir;
	protected int mouseX, mouseY;

	public GuiScrollbar(int id, int xPos, int yPos, int length, Direction dir) {
		super(id, xPos, yPos, dir.isHorizontal() ? length : 10, dir.isHorizontal() ? 10 : length, "");
		this.dir = dir;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int k = this.getHoverState(this.hovered);
			boolean horizontal = dir.isHorizontal();
			GuiUtils.drawContinuousTexturedBox(GuiDrawer.COMMON_TEXTURES, this.xPosition, this.yPosition, 0, 0, this.width, this.height, 18, 18, 1, this.zLevel);
			this.mouseDragged(mc, mouseX, mouseY);
			int x = xPosition + 1;
			int y = yPosition + 1;
			// TODO UNFINISHED
			GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.xPosition + 1, this.yPosition + 1, 0, 46 + 20, 8, 8, 200, 20, 4, this.zLevel);
		}
	}
}
