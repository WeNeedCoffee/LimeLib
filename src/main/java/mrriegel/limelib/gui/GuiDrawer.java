package mrriegel.limelib.gui;

import java.util.List;

import mrriegel.limelib.LimeLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
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
		drawFramedRectangle(x, y, size, size);
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
		drawFramedRectangle(x, y, width, height);
	}

	public void drawTextfield(int x, int y, int width) {
		drawFramedRectangle(x, y, width, 12);
	}

	public void drawTextfield(GuiTextField textfield) {
		if (!textfield.getEnableBackgroundDrawing())
			drawTextfield(textfield.xPosition - guiLeft - 2, textfield.yPosition - guiTop - 2, textfield.width + 9);
	}

	public void drawFramedRectangle(int x, int y, int width, int height) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GlStateManager.color(1F, 1F, 1F, 1F);
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

	public void drawColoredRectangle(int x, int y, int width, int height, int color) {
		GuiUtils.drawGradientRect((int) zLevel, x + guiLeft, y + guiTop, x + width + guiLeft, y + height + guiTop, color, color);
	}

	public void drawFrame(int x, int y, int width, int height, int frame, int color) {
		drawColoredRectangle(x, y, width, frame, color);
		drawColoredRectangle(x, y + 1, frame, height, color);
		drawColoredRectangle(x + 1, y + height - (frame - 1), width, frame, color);
		drawColoredRectangle(x + width - (frame - 1), y, frame, height, color);
	}

	public void drawEnergyBarV(int x, int y, int height, float percent) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GlStateManager.color(1F, 1F, 1F, 1F);
		for (int i = 0; i < height + 1; i++)
			if (i % 2 == 0)
				GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop + i, 0, 36, 8, 1, zLevel);
			else
				GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop + i, 0, 37, 8, 1, zLevel);
		for (int i = 0; i < (height + 1) * (1f - percent); i++)
			if (i % 2 == 0)
				GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop + i, 0, 38, 8, 1, zLevel);
			else
				GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop + i, 0, 39, 8, 1, zLevel);
	}

	public void drawEnergyBarH(int x, int y, int width, float percent) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GlStateManager.color(1F, 1F, 1F, 1F);
		for (int i = 0; i < width + 1; i++)
			if (i % 2 == 0)
				GuiUtils.drawTexturedModalRect(x + guiLeft + i, y + guiTop, 8, 36, 1, 8, zLevel);
			else
				GuiUtils.drawTexturedModalRect(x + guiLeft + i, y + guiTop, 9, 36, 1, 8, zLevel);
		for (int i = 0; i < (width + 1) * (percent); i++)
			if (i % 2 == 0)
				GuiUtils.drawTexturedModalRect(x + guiLeft + i, y + guiTop, 10, 36, 1, 8, zLevel);
			else
				GuiUtils.drawTexturedModalRect(x + guiLeft + i, y + guiTop, 11, 36, 1, 8, zLevel);
	}

	@Deprecated
	public void drawFluidRect(int x, int y, int width, int height, FluidStack fluid) {
		GlStateManager.pushMatrix();
		TextureAtlasSprite fluidIcon = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill().toString());
		if (fluidIcon == null)
			return;
		int color = fluid.getFluid().getColor(fluid);
		float a = ((color >> 24) & 0xFF) / 255.0F;
		float r = ((color >> 16) & 0xFF) / 255.0F;
		float g = ((color >> 8) & 0xFF) / 255.0F;
		float b = ((color >> 0) & 0xFF) / 255.0F;
		GlStateManager.color(r, g, b, a);
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		// GlStateManager.disableLighting();
		// GlStateManager.disableDepth();
		x += guiLeft;
		y += guiTop;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		// System.out.println(String.format("%f",Float.MAX_VALUE));
		// System.out.println(String.format("%d",Long.MAX_VALUE));
		// System.out.println(String.format("%f %f %f %f",
		// fluidIcon.getMinU(),fluidIcon.getMaxU(),fluidIcon.getMinV(),fluidIcon.getMaxV()));
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, this.zLevel).tex(fluidIcon.getMinU(), fluidIcon.getMaxV()).endVertex();
		vertexbuffer.pos(x + width, y + height, this.zLevel).tex(fluidIcon.getMaxU(), fluidIcon.getMaxV()).endVertex();
		vertexbuffer.pos(x + width, y + 0, this.zLevel).tex(fluidIcon.getMaxU(), fluidIcon.getMinV()).endVertex();
		vertexbuffer.pos(x + 0, y + 0, this.zLevel).tex(fluidIcon.getMinU(), fluidIcon.getMinV()).endVertex();
		tessellator.draw();
		// GuiUtils.drawTexturedModalRect(x+guiLeft, y+guiTop,
		// (int)fluidIcon.getMinU(), (int)fluidIcon.getMinV(), width, height,
		// zLevel);
		// GlStateManager.enableLighting();
		// GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	public void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			} else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		ScaledResolution sr = new ScaledResolution(mc);
		GuiUtils.drawHoveringText(list, x, y, sr.getScaledWidth(), sr.getScaledHeight(), -1, (font == null ? mc.fontRendererObj : font));
	}

	public void drawProgressArrow(int x, int y, float percent, Direction d) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GlStateManager.color(1F, 1F, 1F, 1F);
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

	public void drawStopSign(int x, int y) {
		mc.getTextureManager().bindTexture(COMMON_TEXTURES);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 12, 36, 12, 12, zLevel);
	}

	public enum Direction {
		UP, RIGHT, DOWN, LEFT;

		public boolean isHorizontal() {
			return this == RIGHT || this == LEFT;
		}
	}

}
