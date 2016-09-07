package mrriegel.limelib.gui;

import mrriegel.limelib.LimeLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiDrawer {

	public static final ResourceLocation COMMON_TEXTURES = new ResourceLocation(LimeLib.MODID + ":textures/gui/base.png");

	public int guiLeft, guiTop, xSize, ySize;
	public float zLevel = 0;
	private Minecraft mc;

	public GuiDrawer(int guiLeft, int guiTop, int xSize, int ySize, float zLevel) {
		super();
		this.guiLeft = guiLeft;
		this.guiTop = guiTop;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zLevel = zLevel;
		mc = Minecraft.getMinecraft();
	}

	public void drawSlot(int x, int y) {
		drawSizedSlot(x, y, 18);
	}

	public void drawSizedSlot(int x, int y, int size) {
		drawRectangle(x, y, size, size);
	}

	public void drawPlayerSlots(int x, int y) {
		drawSlots(x, y + 58, 9, 1);
		drawSlots(x, y, 9, 3);
	}

	public void drawSlots(int x, int y, int width, int height) {
		for (int k = 0; k < height; ++k)
			for (int i = 0; i < width; ++i)
				drawSlot(x + i * 18, y + k * 18);
	}

	public void drawScrollbar(int x, int y, int length, float percent, Direction dir) {
		int width = dir.isHorizontal() ? length : 10;
		int height = dir.isHorizontal() ? 10 : length;
		drawRectangle(x, y, width, height);
	}

	public void drawTextfield(int x, int y, int width) {
		drawRectangle(x, y, width, 12);
	}

	public void drawTextfield(GuiTextField textfield) {
		if (!textfield.getEnableBackgroundDrawing())
			drawTextfield(textfield.xPosition - guiLeft - 2, textfield.yPosition - guiTop - 2, textfield.width + 9);
	}

	public void drawRectangle(int x, int y, int width, int height) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 0, width, height, 18, 18, 1, zLevel);
	}

	public void drawBackgroundTexture(int x, int y, int width, int height) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 18, width, height, 18, 18, 4, zLevel);
	}

	public void drawBackgroundTexture(int x, int y) {
		drawBackgroundTexture(x, y, xSize, ySize);
	}

	public void drawBackgroundTexture() {
		drawBackgroundTexture(0, 0);
	}

	public void drawProgressArrow(int x, int y, float percent, Direction d) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		int totalLength = 22;
		int currentLength = (int) (totalLength * percent);
		switch (d) {
		case DOWN:
			GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 93, 0, 15, 22, zLevel);
			GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 108, 0, 16, currentLength, zLevel);
			break;
		case LEFT:
			GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 40, 0, 22, 15, zLevel);
			GuiUtils.drawTexturedModalRect(x + guiLeft + (totalLength - currentLength), y + guiTop, 40 + (totalLength - currentLength), 15, currentLength, 16, zLevel);
			break;
		case RIGHT:
			GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 18, 0, 22, 15, zLevel);
			GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 18, 15, currentLength, 16, zLevel);
			break;
		case UP:
			GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 78, 0, 15, 22, zLevel);
			GuiUtils.drawTexturedModalRect(x + guiLeft - 1, y + guiTop + (totalLength - currentLength), 62, 0 + (totalLength - currentLength), 16, currentLength, zLevel);
			break;
		}

	}

	public enum Direction {
		UP, RIGHT, DOWN, LEFT;

		public boolean isHorizontal() {
			return this == RIGHT || this == LEFT;
		}
	}

}
