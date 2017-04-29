package mrriegel.limelib.gui.button;

import java.awt.Color;
import java.util.List;

import mrriegel.limelib.gui.GuiDrawer;

public class GuiButtonSimple extends CommonGuiButton {

	public GuiButtonSimple(int id, int xPos, int yPos, int width, int height, String displayString, List<String> strings) {
		this(id, xPos, yPos, width, height, displayString, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB(), strings);
	}

	public GuiButtonSimple(int id, int xPos, int yPos, int width, int height, String displayString, int frameColor, int buttonColor, List<String> strings) {
		super(id, xPos, yPos, width, height, displayString);
		this.drawer = new GuiDrawer(0, 0, 0, 0, zLevel);
		this.frameColor = frameColor;
		this.buttonColor = buttonColor;
		this.strings = strings;
		this.design = Design.SIMPLE;
	}

}
