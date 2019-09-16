package kdp.limelib.gui;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.opengl.GL11;

import kdp.limelib.LimeLib;
import kdp.limelib.helper.ColorHelper;

public class GuiDrawer {

    public static final ResourceLocation COMMON_TEXTURES = new ResourceLocation(
            LimeLib.MOD_ID + ":textures/gui/base.png");
    public static final ResourceLocation BARRIER_TEXTURES = new ResourceLocation("textures/item/barrier.png");

    public int guiLeft, guiTop, xSize, ySize;
    public float zLevel = 0;
    private static Minecraft mc = Minecraft.getInstance();

    public GuiDrawer(int guiLeft, int guiTop, int xSize, int ySize) {
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.xSize = xSize;
        this.ySize = ySize;
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

    /*public void drawScrollbar(int x, int y, int length, float percent, Direction dir) {
        int width = dir.isHorizontal() ? length : 10;
        int height = dir.isHorizontal() ? 10 : length;
        drawFramedRectangle(x, y, width, height);
        if (!dir.isHorizontal())
            new GuiButtonExt(0, x + guiLeft + 1, y + guiTop + 1 + (int) (percent * (length - 10)), 8, 8, "")
                    .drawButton(mc, getMouseX(), getMouseY(), mc.getTickLength());
        else
            new GuiButtonExt(0, x + guiLeft + 1 + (int) (percent * (length - 10)), y + guiTop + 1, 8, 8, "")
                    .drawButton(mc, getMouseX(), getMouseY(), mc.getTickLength());
    }*/

    public void drawTextfield(int x, int y, int width) {
        drawFramedRectangle(x, y, width, 12);
    }

    public void drawTextfield(TextFieldWidget textfield) {
        //if (!textfield.getEnableBackgroundDrawing())
        drawTextfield(textfield.x - guiLeft - 2, textfield.y - guiTop - 2, textfield.getWidth() + 9);
    }

    public void drawFramedRectangle(int x, int y, int width, int height) {
        bindTexture();
        GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 0, width, height, 18, 18, 1, zLevel);
    }

    public void drawBackgroundTexture(int x, int y, int width, int height) {
        bindTexture();
        GuiUtils.drawContinuousTexturedBox(x + guiLeft, y + guiTop, 0, 18, width, height, 18, 18, 4, zLevel);
    }

    public void drawBackgroundTexture(int x, int y) {
        drawBackgroundTexture(x, y, xSize, ySize);
    }

    public void drawBackgroundTexture() {
        drawBackgroundTexture(0, 0);
    }

    public void drawColoredRectangle(int x, int y, int width, int height, int color) {
        GuiUtils.drawGradientRect((int) zLevel, x + guiLeft, y + guiTop, x + width + guiLeft, y + height + guiTop,
                color, color);
    }

    public void drawFrame(int x, int y, int width, int height, int frame, int color) {
        drawColoredRectangle(x, y, width, frame, color);
        drawColoredRectangle(x, y + 1, frame, height, color);
        drawColoredRectangle(x + 1, y + height - (frame - 1), width, frame, color);
        drawColoredRectangle(x + width - (frame - 1), y, frame, height, color);
    }

    @Deprecated
    public void drawEnergyBarV(int x, int y, int height, float percent) {
        drawProgressbar(x, y, 8, height, 0xFF0000, percent, false, true);
    }

    @Deprecated
    public void drawEnergyBarH(int x, int y, int width, float percent) {
        drawProgressbar(x, y, width, 8, 0xFF0000, percent, true, true);
    }

    public void drawProgressbar(int x, int y, int width, int height, int color, float percent, boolean horizontal,
            boolean dotted) {
        final int midColorOn = 0xFF000000 | color;
        final int outColorOn = ColorHelper.darker(midColorOn, .15);
        final int midColorOff = ColorHelper.darker(midColorOn, .4);
        final int outColorOff = ColorHelper.darker(outColorOn, .4);
        final int midColorOnDotted = dotted ? ColorHelper.darker(midColorOn, .1) : 0;
        final int outColorOnDotted = dotted ? ColorHelper.darker(outColorOn, .1) : 0;
        final int midColorOffDotted = dotted ? ColorHelper.darker(midColorOff, .1) : 0;
        final int outColorOffDotted = dotted ? ColorHelper.darker(outColorOff, .1) : 0;
        int tX = x + guiLeft;
        int tY = y + guiTop;
        if (horizontal) {
            final boolean even = height % 2 == 0;
            final int upperHeight = even ? height / 2 : height / 2 + 1;
            final int lowerHeight = even ? height / 2 : height / 2;
            GuiUtils.drawGradientRect(0, tX, tY, tX + width, tY + upperHeight, outColorOff, midColorOff);
            GuiUtils.drawGradientRect(0, tX, tY + lowerHeight, tX + width, tY + lowerHeight + upperHeight, midColorOff,
                    outColorOff);
            int filledWidth = Math.round(width * percent);
            GuiUtils.drawGradientRect(0, tX, tY, tX + filledWidth, tY + upperHeight, outColorOn, midColorOn);
            GuiUtils.drawGradientRect(0, tX, tY + lowerHeight, tX + filledWidth, tY + lowerHeight + upperHeight,
                    midColorOn, outColorOn);
            if (dotted) {
                for (int i = tX + 1; i < tX + width; i += 2) {
                    GuiUtils.drawGradientRect(0, i, tY, i + 1, tY + upperHeight, outColorOffDotted, midColorOffDotted);
                    GuiUtils.drawGradientRect(0, i, tY + lowerHeight, i + 1, tY + lowerHeight + upperHeight,
                            midColorOffDotted, outColorOffDotted);
                }
                for (int i = tX + 1; i < tX + filledWidth; i += 2) {
                    GuiUtils.drawGradientRect(0, i, tY, i + 1, tY + upperHeight, outColorOnDotted, midColorOnDotted);
                    GuiUtils.drawGradientRect(0, i, tY + lowerHeight, i + 1, tY + lowerHeight + upperHeight,
                            midColorOnDotted, outColorOnDotted);
                }
            }
        } else {
            final boolean even = width % 2 == 0;
            final int leftWidth = even ? width / 2 : width / 2 + 1;
            final int rightWidth = even ? width / 2 : width / 2;
            percent = 1f - percent;
            drawGradientRect2(0, tX, tY, tX + leftWidth, tY + height, outColorOff, midColorOff);
            drawGradientRect2(0, tX + leftWidth, tY, tX + leftWidth + rightWidth, tY + height, midColorOff,
                    outColorOff);
            int filledHeight = Math.round(height * percent);
            drawGradientRect2(0, tX, tY + filledHeight, tX + leftWidth, tY + height, outColorOn, midColorOn);
            drawGradientRect2(0, tX + leftWidth, tY + filledHeight, tX + leftWidth + rightWidth, tY + height,
                    midColorOn, outColorOn);
            if (dotted) {
                for (int i = tY + height - 1; i >= tY; i -= 2) {
                    drawGradientRect2(0, tX, i, tX + leftWidth, i + 1, outColorOffDotted, midColorOffDotted);
                    drawGradientRect2(0, tX + leftWidth, i, tX + leftWidth + rightWidth, i + 1, midColorOffDotted,
                            outColorOffDotted);
                }
                for (int i = tY + height - 1; i >= tY + filledHeight; i -= 2) {
                    drawGradientRect2(0, tX, i, tX + leftWidth, i + 1, outColorOnDotted, midColorOnDotted);
                    drawGradientRect2(0, tX + leftWidth, i, tX + leftWidth + rightWidth, i + 1, midColorOnDotted,
                            outColorOnDotted);
                }
            }
        }
    }

    public static void drawGradientRect2(int zLevel, int left, int top, int right, int bottom, int startColor,
            int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager
                .blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(left, bottom, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    public void drawFluidRect(int x, int y, int width, int height, FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return;
        }
        TextureAtlasSprite icon = mc.getTextureMap().getSprite(fluid.getFluid().getAttributes().getStillTexture());
        if (icon == null) {
            return;
        }
        x += guiLeft;
        y += guiTop;
        int renderAmount = Math.max(height, 1);
        int posY = y + height - renderAmount;

        mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        int color = fluid.getFluid().getAttributes().getColor(fluid);
        GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

        GlStateManager.enableBlend();
        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                int drawWidth = Math.min(width - i, 16);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = x + i;
                int drawY = posY + j;

                double minU = icon.getMinU();
                double maxU = icon.getMaxU();
                double minV = icon.getMinV();
                double maxV = icon.getMaxV();

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder tes = tessellator.getBuffer();
                tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                tes.pos(drawX, drawY + drawHeight, 0).tex(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                tes.pos(drawX + drawWidth, drawY + drawHeight, 0)
                        .tex(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F)
                        .endVertex();
                tes.pos(drawX + drawWidth, drawY, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
                tes.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
                tessellator.draw();
            }
        }
        GlStateManager.disableBlend();
    }

    public void drawItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + guiLeft, y + guiTop);
        GlStateManager.popMatrix();
    }

    public void drawProgressArrow(int x, int y, float percent, Direction d) {
        bindTexture();
        int totalLength = 22;
        int currentLength = (int) (totalLength * percent);
        switch (d) {
        case DOWN:
            GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 93, 0, 15, 22, zLevel);
            GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 108, 0, 16, currentLength, zLevel);
            break;
        case LEFT:
            GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 40, 0, 22, 15, zLevel);
            GuiUtils.drawTexturedModalRect(x + guiLeft + (totalLength - currentLength), y + guiTop,
                    40 + (totalLength - currentLength), 15, currentLength, 16, zLevel);
            break;
        case RIGHT:
            GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 18, 0, 22, 15, zLevel);
            GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 18, 15, currentLength, 16, zLevel);
            break;
        case UP:
            GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 78, 0, 15, 22, zLevel);
            GuiUtils.drawTexturedModalRect(x + guiLeft - 1, y + guiTop + (totalLength - currentLength), 62,
                    0 + (totalLength - currentLength), 16, currentLength, zLevel);
            break;
        }
    }

    public void drawFlame(int x, int y, float percent) {
        bindTexture();
        GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop, 24, 31, 13, 13, zLevel);
        int totalHeight = 13;
        int currentHeight = (int) (totalHeight * percent);
        GuiUtils.drawTexturedModalRect(x + guiLeft, y + guiTop + (totalHeight - currentHeight), 37,
                31 + (totalHeight - currentHeight), 13, 13, zLevel);
    }

    public void drawStopSign(int x, int y) {
        mc.getTextureManager().bindTexture(BARRIER_TEXTURES);
        GlStateManager.clearColor(1F, 1F, 1F, 1F);
        GlStateManager.translated(x + guiLeft, y + guiTop, 0);
        GlStateManager.scaled(1 / 16d, 1 / 16d, 1 / 16d);
        GuiUtils.drawTexturedModalRect(0, 0, 0, 0, 256, 256, 0);
        GlStateManager.scaled(16, 16, 16);
        GlStateManager.translated(-(x + guiLeft), -(y + guiTop), 0);
    }

    private void bindTexture() {
        mc.getTextureManager().bindTexture(COMMON_TEXTURES);
        GlStateManager.clearColor(1F, 1F, 1F, 1F);
    }

    /*public static int getMouseX() {
        return mc.mouseHelper.getMouseX() * mc.mainWindow.getScaledWidth() / mc.mainWindow.getWidth();
    }

    public static int getMouseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1;
    }*/

    /*public static void renderToolTip(ItemStack stack, int x, int y) {
        List<String> list = getTooltip(stack);
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        GuiUtils.drawHoveringText(list,
                x,
                y,
                mc.mainWindow.getScaledWidth(),
                mc.mainWindow.getScaledHeight(),
                -1,
                (font == null ? mc.fontRenderer : font));
    }*/

    public static void renderToolTip(List<String> list, int x, int y) {
        GuiUtils.drawHoveringText(list, x, y, mc.mainWindow.getScaledWidth(), mc.mainWindow.getScaledHeight(), -1,
                mc.fontRenderer);
    }

    /*public static List<ITextComponent> getTooltip(ItemStack stack) {
        List<ITextComponent> list = stack.getTooltip(mc.player,
                mc.gameSettings.advancedItemTooltips ?
                        ITooltipFlag.TooltipFlags.ADVANCED :
                        ITooltipFlag.TooltipFlags.NORMAL);
        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().color + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }
        return list;
    }*/

    /*public static void openGui(Screen screen) {
        if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT)) {
            FMLCommonHandler.instance().showGuiScreen(screen);
        }
    }*/

    /*private static FontRenderer uni, notUni;

    public static FontRenderer getFontRenderer(boolean unicode) {
        if (unicode) {
            if (uni == null)
                return uni = new FontRenderer(mc.gameSettings,
                        new ResourceLocation("textures/font/ascii.png"),
                        mc.re,
                        true);
            else
                return uni;
        } else {
            if (notUni == null)
                return notUni = new FontRenderer(mc.gameSettings,
                        new ResourceLocation("textures/font/ascii.png"),
                        mc.renderEngine,
                        false);
            else
                return notUni;
        }
    }*/

    public enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public boolean isHorizontal() {
            return this == RIGHT || this == LEFT;
        }
    }
}
