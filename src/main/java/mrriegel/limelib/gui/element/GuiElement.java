package mrriegel.limelib.gui.element;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

public abstract class GuiElement {
	int id, x, y;
	public boolean visible;
	protected boolean hovered;
	protected GuiScreen parent;
	public Minecraft mc;

	public GuiElement(int id, int x, int y, GuiScreen parent) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.visible = true;
		this.parent = parent;
		mc = Minecraft.getMinecraft();
	}

	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
	};

	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
	};

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
		return false;
	};

	public boolean mouseReleased(Minecraft mc, int mouseX, int mouseY) {
		return false;
	};

	public void handleMouseInput() {
	};

	public boolean isMouseOver() {
		return this.hovered;
	}

	public void renderToolTip(ItemStack stack, int mouseX, int mouseY) {
		List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + (String) list.get(i));
			} else {
				list.set(i, TextFormatting.GRAY + (String) list.get(i));
			}
		}
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		GuiUtils.drawHoveringText(list, x, y, parent.width, parent.height, -1, (font == null ? mc.fontRendererObj : font));
	}

}
