package mrriegel.limelib.gui.element;

import java.util.Arrays;
import java.util.List;

import mrriegel.limelib.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class GuiToolTip extends Gui implements IGuiElement {
	public List<String> text;
	public ItemStack stack;
	public int x, y, id, width, height;
	public boolean visible;

	public GuiToolTip(int id, int x, int y, int width, int height,
			List<String> text, ItemStack stack) {
		super();
		this.text = text;
		this.x = x;
		this.y = y;
		this.id = id;
		this.width = width;
		this.height = height;
		this.stack = stack;
		this.visible = true;
	}

	@Override
	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {

	}

	@Override
	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
		boolean hovered = mouseX >= this.x && mouseY >= this.y
				&& mouseX < this.x + width && mouseY < this.y + height;
		if (!visible || !hovered)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		if (stack != null)
			GuiHelper.renderToolTip(stack, mouseX, mouseY);
		else
			GuiHelper
					.drawHoveringText(text, mouseX, mouseY, mc.fontRendererObj);
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();

	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return false;
	}

}
