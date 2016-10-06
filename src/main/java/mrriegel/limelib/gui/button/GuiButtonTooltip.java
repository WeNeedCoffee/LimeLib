package mrriegel.limelib.gui.button;

import java.util.List;

import mrriegel.limelib.gui.element.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiButtonTooltip extends GuiButtonColor implements ITooltip {
	protected List<String> strings;

	public GuiButtonTooltip(int id, int xPos, int yPos, int width, int height, String displayString, EnumDyeColor color, List<String> strings) {
		super(id, xPos, yPos, width, height, displayString, color);
		this.strings = strings;
	}

	@Override
	public void drawTooltip(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		if (strings != null)
			GuiUtils.drawHoveringText(strings, mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, Minecraft.getMinecraft().fontRendererObj);
		GlStateManager.popMatrix();
	}

	public void setTooltip(List<String> lines) {
		strings = lines;
	}

}
