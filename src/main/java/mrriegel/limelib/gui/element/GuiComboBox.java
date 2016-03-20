package mrriegel.limelib.gui.element;

import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import mrriegel.limelib.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiComboBox extends Gui implements IGuiElement {
	public String current;
	public List<String> list;
	public int x, y, id, width, maxEntry;
	public boolean visible;
	protected boolean hovered, extended;

	public GuiComboBox(int id, int x, int y, List<String> list, int width,
			int maxEntry) {
		super();
		this.list = list;
		this.x = x;
		this.y = y;
		this.id = id;
		this.width = width;
		this.maxEntry = maxEntry;
		this.visible = true;
		current = list.isEmpty() ? "" : list.get(0);
	}

	@Override
	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		if (GuiScreen.isShiftKeyDown())
			width = new Random().nextInt(100) + 20;
		mc.getTextureManager().bindTexture(GuiHelper.icons);
		this.hovered = mouseX >= this.x && mouseY >= this.y
				&& mouseX < this.x + width && mouseY < this.y + 12;
		this.drawTexturedModalRect(this.x, this.y, 0, 55, this.width / 2, 12);
		this.drawTexturedModalRect(this.x + this.width / 2, this.y,
				200 - this.width / 2, 55, this.width / 2, 12);
		String t = current;
		while (mc.fontRendererObj.getStringWidth(t) > width - 9)
			t = t.substring(0, t.length() - 1);
		this.drawString(mc.fontRendererObj, t, this.x + 3, this.y + (4) / 2,
				14737632);
		mc.getTextureManager().bindTexture(GuiHelper.icons);
		this.drawTexturedModalRect(this.x + this.width - 9, this.y, 200, 55, 9,
				12);

	}

	@Override
	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
//		if (!visible || !extended)
//			return;
		for (int i = 0; i < (Math.min(maxEntry, list.size())); i++) {
			String t = list.get(i);
			while (mc.fontRendererObj.getStringWidth(t) > width - 6)
				t = t.substring(0, t.length() - 1);
			mc.fontRendererObj.drawString(t, this.x + 3, this.y + 4+(i+1)*9, 14737632,true);
//			this.drawString(mc.fontRendererObj, t, this.x + 3, this.y + 4+(i+1)*9,
//					14737632);
		}

	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		extended = !extended;
		return false;
	}

}
