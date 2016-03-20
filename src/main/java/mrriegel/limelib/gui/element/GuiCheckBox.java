package mrriegel.limelib.gui.element;

import mrriegel.limelib.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiCheckBox extends Gui implements IGuiElement{
	public boolean check;
	public int x, y, id;
	public boolean visible;
	protected boolean hovered;

	public GuiCheckBox(int id, int x, int y, boolean check) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.visible = true;
		this.check = check;
	}

	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		mc.getTextureManager().bindTexture(GuiHelper.icons);
		this.hovered = mouseX >= this.x && mouseY >= this.y
				&& mouseX < this.x + 13 && mouseY < this.y + 13;
		this.drawTexturedModalRect(this.x, this.y, 0,
				42, 13, 13);
		if(check)
			this.drawTexturedModalRect(this.x, this.y, 13,
					42, 13, 13);
	}

	public boolean isMouseOver() {
		return this.hovered;
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible && this.hovered)
			check = !check;
		return this.visible && this.hovered;
	}

	@Override
	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
	}
}
