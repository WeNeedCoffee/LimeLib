package mrriegel.limelib.gui.button;

import java.awt.Color;
import java.util.List;

import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.input.Mouse;

public class GuiButtonSimple extends GuiButtonTooltip {

	GuiDrawer drawer;
	int frameColor, buttonColor;

	public GuiButtonSimple(int id, int xPos, int yPos, int width, int height, String displayString, List<String> strings) {
		this(id, xPos, yPos, width, height, displayString, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB(), strings);
	}

	public GuiButtonSimple(int id, int xPos, int yPos, int width, int height, String displayString, int frameColor, int buttonColor, List<String> strings) {
		super(id, xPos, yPos, width, height, displayString, null, strings);
		this.drawer = new GuiDrawer(0, 0, 0, 0, zLevel);
		this.frameColor = frameColor;
		this.buttonColor = buttonColor;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = mc.fontRenderer;
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			drawer.drawFrame(xPosition, yPosition, width - 1, height - 1, 1, frameColor);
			drawer.drawColoredRectangle(xPosition + 1, yPosition + 1, width - 2, height - 2, hovered && !Mouse.isButtonDown(0) ? ColorHelper.brighter(buttonColor, 0.10) : buttonColor);
			this.mouseDragged(mc, mouseX, mouseY);
			double brightness = .33 * ColorHelper.getRed(buttonColor) + .5 * ColorHelper.getGreen(buttonColor) + .16 * ColorHelper.getBlue(buttonColor);
			brightness = 0;
			int stringColor = brightness < 127 ? 0xE0E0E0 : 0x1F1F1F;
			if (!enabled)
				stringColor = ColorHelper.darker(stringColor, 0.2);
			this.drawCenteredString(fontrenderer, hovered ? displayString : fontrenderer.trimStringToWidth(displayString, width - 4), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, stringColor);
		}
	}

}
