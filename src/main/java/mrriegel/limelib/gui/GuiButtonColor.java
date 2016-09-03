package mrriegel.limelib.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiButtonColor extends GuiButtonExt {

	EnumDyeColor color;

	public GuiButtonColor(int id, int xPos, int yPos, String displayString, EnumDyeColor color) {
		super(id, xPos, yPos, displayString);
		this.color = color;
	}

	public GuiButtonColor(int id, int xPos, int yPos, int width, int height, String displayString, EnumDyeColor color) {
		super(id, xPos, yPos, width, height, displayString);
		this.color = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible && color != null) {
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int k = this.getHoverState(this.hovered);
			boolean flag = k == 2;
			if (k == 2)
				k = 1;
			GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
			this.mouseDragged(mc, mouseX, mouseY);
			int color = 14737632;

			if (packedFGColour != 0) {
				color = packedFGColour;
			} else if (!this.enabled) {
				color = 10526880;
			} else if (this.hovered) {
				color = 16777120;
			}

			String buttonText = this.displayString;
			int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
			int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");

			if (strWidth > width - 6 && strWidth > ellipsisWidth)
				buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

			Color c = new Color(this.color.getMapColor().colorValue);
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 140 + (flag ? 60 : 0));
			drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, c.getRGB());
			this.drawCenteredString(mc.fontRendererObj, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
		} else
			super.drawButton(mc, mouseX, mouseY);
	}
}
