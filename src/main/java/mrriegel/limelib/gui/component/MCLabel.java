package mrriegel.limelib.gui.component;

import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.element.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiUtils;

import com.google.common.collect.Lists;

public class MCLabel extends GuiComponent implements ITooltip {

	public String text;
	public int color;

	public MCLabel(int x, int y, String text, int color, GuiDrawer drawer) {
		super(x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(text), Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, drawer);
		this.text = text;
		this.color = color;
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		mc.fontRenderer.drawString(text, x + getOffsetX(), y + getOffsetY(), color);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY) {
		drawer.drawFramedRectangle(x + getOffsetX() - 2, y + getOffsetY() - 2, mc.fontRenderer.getStringWidth(text) + 4, mc.fontRenderer.FONT_HEIGHT + 2);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		width = mc.fontRenderer.getStringWidth(text);
	}

	@Override
	public void drawTooltip(int mouseX, int mouseY) {
		ScaledResolution sr = new ScaledResolution(mc);
		GuiUtils.drawHoveringText(Lists.newArrayList("Neuer", "Lahm", "Hummels", "Martinez", "Alaba", "Alonso", "Sanches", "Thiago", "Ribery", "Lewandowski", "Müller"), mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRenderer);
	}
}
