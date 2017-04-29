package mrriegel.limelib.gui.button;

import java.util.List;

import mrriegel.limelib.helper.ColorHelper;
import net.minecraft.item.EnumDyeColor;

public class GuiButtonTooltip extends CommonGuiButton {

	public GuiButtonTooltip(int id, int xPos, int yPos, int width, int height, String displayString, EnumDyeColor color, List<String> strings) {
		super(id, xPos, yPos, width, height, displayString);
		this.strings = strings;
		this.overlayColor = color != null ? ColorHelper.getRGB(color) : Integer.MAX_VALUE;
	}

}
