package mrriegel.limelib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

public class GuiSmallButton extends GuiButton {

	int color = 0xFFFFFF;

	private ResourceLocation texture = new ResourceLocation(
			"limelib:textures/gui/icons.png");

	public GuiSmallButton(int buttonId, int x, int y, int widthIn,
			String buttonText, int color) {
		super(buttonId, x, y, widthIn, 14, buttonText);
		this.color = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(texture);
			GlStateManager.color((color >> 16 & 255) / 255.0F,
					(color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
					&& mouseX < this.xPosition + this.width
					&& mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0,
					0 + i * 14, this.width, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if (packedFGColour != 0) {
				j = packedFGColour;
			} else if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j -= 0x222200;
			}

			this.drawCenteredString(fontrenderer, this.displayString,
					this.xPosition + this.width / 2, this.yPosition
							+ (this.height - 9) / 2, j);
		}
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		int i = 1;

		if (!this.enabled) {
			i = 0;
		} else if (mouseOver && Mouse.isButtonDown(0)) {
			i = 2;
		}

		return i;
	}

}
