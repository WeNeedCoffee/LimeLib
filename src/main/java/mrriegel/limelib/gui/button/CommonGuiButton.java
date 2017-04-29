package mrriegel.limelib.gui.button;

import java.awt.Color;
import java.util.List;

import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.element.ITooltip;
import mrriegel.limelib.helper.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

public class CommonGuiButton extends GuiButtonExt implements ITooltip {

	protected List<String> strings;
	protected Minecraft mc;
	protected GuiDrawer drawer;
	protected int frameColor, buttonColor, overlayColor;
	protected Design design;
	protected ItemStack stack;

	public static enum Design {
		NORMAL, SIMPLE, NONE;
	}

	public CommonGuiButton(int id, int xPos, int yPos, int width, int height, String displayString) {
		super(id, xPos, yPos, width, height, displayString);
		mc = Minecraft.getMinecraft();
		drawer = new GuiDrawer(0, 0, 0, 0, zLevel);
		frameColor = Color.BLACK.getRGB();
		buttonColor = Color.DARK_GRAY.getRGB();
		overlayColor = Integer.MAX_VALUE;
		design = Design.NORMAL;
		stack = ItemStack.EMPTY;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int k = this.getHoverState(this.hovered);
			if (design == Design.NORMAL)
				GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
			else if (design == Design.SIMPLE) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				drawer.drawFrame(xPosition, yPosition, width - 1, height - 1, 1, frameColor);
				drawer.drawColoredRectangle(xPosition + 1, yPosition + 1, width - 2, height - 2, hovered && !Mouse.isButtonDown(0) ? ColorHelper.brighter(buttonColor, 0.10) : buttonColor);
			} else if (design == Design.NONE)
				;//NO-OP
			if (overlayColor != Integer.MAX_VALUE)
				drawRect(xPosition + 0, yPosition + 0, xPosition + width - 0, yPosition + height - 0, ColorHelper.getRGB(overlayColor, 140 + (k == 2 ? 60 : 0)));
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
			int strWidth = mc.fontRenderer.getStringWidth(buttonText);
			int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
			if (strWidth > width - 6 && strWidth > ellipsisWidth)
				buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
			int xS = this.xPosition + this.width / 2;
			this.drawCenteredString(mc.fontRenderer, buttonText, xS, this.yPosition + (this.height - 8) / 2, color);
			if (!stack.isEmpty()) {
				int yp = Math.max(yPosition, yPosition + (Math.max(height - 16, 0) / 2));
				int xp = Math.max(xPosition, xPosition + (Math.max(width - 16, 0) / 2));
				//				xp=xPosition+1;
				drawer.drawItemStack(stack, xp, yp);
			}
		}
	}

	@Override
	public void drawTooltip(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		ScaledResolution sr = new ScaledResolution(mc);
		if (strings != null)
			GuiUtils.drawHoveringText(strings, mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRenderer);
		GlStateManager.popMatrix();
	}

	public CommonGuiButton setTooltip(List<String> lines) {
		strings = lines;
		return this;
	}

	public CommonGuiButton setTooltip(String string) {
		strings = Lists.newArrayList(string);
		return this;
	}

	public CommonGuiButton setFrameColor(int frameColor) {
		this.frameColor = frameColor;
		return this;
	}

	public CommonGuiButton setButtonColor(int buttonColor) {
		this.buttonColor = buttonColor;
		return this;
	}

	public CommonGuiButton setOverlayColor(int overlayColor) {
		this.overlayColor = overlayColor;
		return this;
	}

	public CommonGuiButton setDesign(Design design) {
		this.design = design;
		return this;
	}

	public CommonGuiButton setStack(ItemStack stack) {
		this.stack = stack;
		return this;
	}

}
