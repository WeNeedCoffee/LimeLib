package mrriegel.limelib.gui.element;

import mrriegel.limelib.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiCheckBox extends GuiElement {

	public boolean check;

	public GuiCheckBox(GuiScreen parent, int id, int x, int y, boolean check) {
		super(id, x, y, parent);
		this.check = check;
	}

	@Override
	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		mc.getTextureManager().bindTexture(GuiHelper.icons);
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 13 && mouseY < this.y + 13;
		GuiUtils.drawTexturedModalRect(this.x, this.y, 0, 42, 13, 13, 300);
		if (check)
			GuiUtils.drawTexturedModalRect(this.x, this.y, 13, 42, 13, 13, 300);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
		if (mouseButton != 0)
			return false;
		if (this.visible && this.hovered)
			check = !check;
		return this.visible && this.hovered;
	}

	@Override
	public boolean mouseReleased(Minecraft mc, int mouseX, int mouseY) {
		return this.visible && this.hovered;
	}
}
