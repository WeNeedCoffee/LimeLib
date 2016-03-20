package mrriegel.limelib.gui.element;

import mrriegel.limelib.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

public class GuiSmallButton extends GuiButton {

	int color = 0xFFFFFF;

	public GuiSmallButton(int buttonId, int x, int y, int widthIn,
			String buttonText, int color) {
		super(buttonId, x, y, widthIn, 14, buttonText);
		this.color = color;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = mc.fontRendererObj;
			mc.getTextureManager().bindTexture(GuiHelper.icons);
			GuiHelper.color(color);
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
			int j = 0xC2C2c2;

			if (!this.enabled) {
				j = 0x575757;
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
