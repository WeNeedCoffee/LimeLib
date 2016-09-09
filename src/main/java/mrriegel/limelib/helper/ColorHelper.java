package mrriegel.limelib.helper;

import java.awt.Color;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;

public class ColorHelper {

	public static int getRGB(EnumDyeColor color) {
		return color.getMapColor().colorValue | 0xFF000000;
	}

	public static int getRGB(EnumDyeColor color, int alpha) {
		return getRGB(getRGB(color), alpha);
	}

	public static int getRGB(int color, int alpha) {
		alpha %= 255;
		Color c = new Color(color);
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
	}

	public static void glColor(int color) {
		GlStateManager.color(getRed(color) / 255f, getGreen(color) / 255f, getBlue(color) / 255f, getAlpha(color) / 255f);
	}

	public static int getRed(int color) {
		return ((0xFF000000 | color) >> 16) & 0xFF;
		// return new Color(color).getRed();
	}

	public static int getGreen(int color) {
		return ((0xFF000000 | color) >> 8) & 0xFF;
		// return new Color(color).getGreen();
	}

	public static int getBlue(int color) {
		return ((0xFF000000 | color) >> 0) & 0xFF;
		// return new Color(color).getBlue();
	}

	public static int getAlpha(int color) {
		return ((0xFF000000 | color) >> 24) & 0xFF;
		// return new Color(color).getAlpha();
	}

}
