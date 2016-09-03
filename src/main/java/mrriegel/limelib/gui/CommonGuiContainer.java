package mrriegel.limelib.gui;

import java.awt.Color;

import mrriegel.limelib.LimeLib;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public abstract class CommonGuiContainer extends GuiContainer {

	public static final ResourceLocation COMMON_TEXTURES = new ResourceLocation(LimeLib.MODID + ":textures/gui/base.png");
	protected boolean darkBackground;

	public CommonGuiContainer(Container inventorySlotsIn, boolean darkBackground) {
		super(inventorySlotsIn);
		this.darkBackground = darkBackground;
	}

	public CommonGuiContainer(Container inventorySlotsIn) {
		this(inventorySlotsIn, true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// drawBackgroundTexture(0, 0, 80, 80);
		// drawBackgroundTexture(90, 0, 80, 80);
		// drawBackgroundTexture(0, 95, 80, 80);
		// drawBackgroundTexture(90, 95, 80, 80);
		drawBackgroundTexture();
		// drawSlot(32, 13);
		drawPlayerSlots(19, 99);
		drawSlots(64, 19, 3, 3);
		int k = (int) System.currentTimeMillis();
		k /= 65;
		drawProgressArrow(148, 12, k, Direction.LEFT);
		drawSizedSlot(150, 35, 18);
		// drawTextfield(138, 67, 32);
		drawScrollbar(1, 89, 50, 0, Direction.DOWN);
	}

	protected void drawSlot(int x, int y) {
		drawSizedSlot(x, y, 18);
	}

	protected void drawSizedSlot(int x, int y, int size) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 0, size, size, 18, 18, 1, zLevel);
	}

	protected void drawPlayerSlots(int x, int y) {
		drawSlots(x, y + 58, 9, 1);
		drawSlots(x, y, 9, 3);
	}

	protected void drawSlots(int x, int y, int width, int height) {
		for (int k = 0; k < height; ++k) {
			for (int i = 0; i < width; ++i) {
				drawSlot(x + i * 18, y + k * 18);
			}
		}
	}

	protected void drawScrollbar(int x, int y, int length, int percent, Direction dir) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		int width = dir.isHorizontal() ? length : 10;
		int height = dir.isHorizontal() ? 10 : length;
		GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 0, width, height, 18, 18, 1, zLevel);
		
	}

	protected void drawTextfield(int x, int y, int width) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 0, width, 12, 18, 18, 1, zLevel);
	}

	protected void drawTextfield(GuiTextField textfield) {
		drawTextfield(textfield.xPosition - guiLeft - 2, textfield.yPosition - guiTop - 2, textfield.width + 9);
	}

	protected void drawBackgroundTexture(int x, int y, int width, int height) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 18, width, height, 18, 18, 4, zLevel);
	}

	protected void drawBackgroundTexture(int x, int y) {
		drawBackgroundTexture(x, y, xSize, ySize);
	}

	protected void drawBackgroundTexture() {
		drawBackgroundTexture(0, 0);
	}

	protected void drawProgressArrow(int x, int y, int percent, Direction d) {
		percent = Math.abs(percent);
		percent %= 100;
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		float pFactor = (percent / 100f);
		int totalLength = 22;
		int currentLength = (int) (totalLength * pFactor);
		switch (d) {
		case DOWN:
			drawTexturedModalRect(x + guiLeft, y + guiTop, 93, 0, 15, 22);
			drawTexturedModalRect(x + guiLeft, y + guiTop, 108, 0, 16, currentLength);
			break;
		case LEFT:
			drawTexturedModalRect(x + guiLeft, y + guiTop, 40, 0, 22, 15);
			drawTexturedModalRect(x + guiLeft + (totalLength - currentLength), y + guiTop, 40 + (totalLength - currentLength), 15, currentLength, 16);
			break;
		case RIGHT:
			drawTexturedModalRect(x + guiLeft, y + guiTop, 18, 0, 22, 15);
			drawTexturedModalRect(x + guiLeft, y + guiTop, 18, 15, currentLength, 16);
			break;
		case UP:
			drawTexturedModalRect(x + guiLeft, y + guiTop, 78, 0, 15, 22);
			drawTexturedModalRect(x + guiLeft - 1, y + guiTop + (totalLength - currentLength), 62, 0 + (totalLength - currentLength), 16, currentLength);
			break;
		}
	}

	@Override
	public void drawWorldBackground(int tint) {
		if (darkBackground)
			super.drawWorldBackground(tint);
	}

	public enum Direction {
		UP, RIGHT, DOWN, LEFT;

		public boolean isHorizontal() {
			return this == RIGHT || this == LEFT;
		}
	}

}
