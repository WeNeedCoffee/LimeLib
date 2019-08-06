package kdp.limelib.gui;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

import kdp.limelib.helper.ColorHelper;

public class GenericButton extends GuiButtonExt {

    protected Minecraft mc;
    protected GuiDrawer drawer;
    protected int frameColor = Color.BLACK.getRGB(), buttonColor = Color.DARK_GRAY
            .getRGB(), overlayColor = Integer.MAX_VALUE;
    protected Design design = Design.NORMAL;

    public GenericButton(int x, int y, int width, int height, String text, IPressable onPress) {
        super(x, y, width, height, text, onPress);
        mc = Minecraft.getInstance();
        drawer = new GuiDrawer(0, 0, 0, 0);
    }

    @Override
    public void onPress() {
        if (onPress != null) {
            onPress.onPress(this);
        }
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered);
            if (design == Design.NORMAL) {
                GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION,
                        this.x,
                        this.y,
                        0,
                        46 + k * 20,
                        this.width,
                        this.height,
                        200,
                        20,
                        2,
                        3,
                        2,
                        2,
                        this.blitOffset);
            } else if (design == Design.SIMPLE) {
                drawer.drawFrame(x, y, width - 1, height - 1, 1, frameColor);
                drawer.drawColoredRectangle(x + 1,
                        y + 1,
                        width - 2,
                        height - 2,
                        active ?
                                isHovered && !mc.mouseHelper.isLeftDown() ?
                                        ColorHelper.brighter(buttonColor, 0.10) :
                                        buttonColor :
                                ColorHelper.darker(buttonColor, 0.10));
            } else if (design == Design.NONE) {
                ;//NO-OP
            }
            if (overlayColor != Integer.MAX_VALUE) {
                fill(x + 0,
                        y + 0,
                        x + width - 0,
                        y + height - 0,
                        ColorHelper.getRGB(overlayColor, 140 + (k == 2 ? 60 : 0)));
            }
            this.renderBg(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColor != 0) {
                color = packedFGColor;
            } else if (!this.active) {
                color = 10526880;
            } else if (this.isHovered) {
                color = 16777120;
            }

            String buttonText = this.getMessage();
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            this.drawCenteredString(mc.fontRenderer,
                    buttonText,
                    this.x + this.width / 2,
                    this.y + (this.height - 8) / 2,
                    color);
        }
    }

    public GenericButton setFrameColor(int frameColor) {
        this.frameColor = frameColor;
        return this;
    }

    public GenericButton setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
        return this;
    }

    public GenericButton setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    public GenericButton setDesign(Design design) {
        this.design = design;
        return this;
    }

    public enum Design {
        NORMAL, SIMPLE, NONE;
    }
}
