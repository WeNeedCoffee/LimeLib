package mrriegel.limelib.gui.element;

import java.util.List;
import java.util.Random;

import mrriegel.limelib.helper.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.input.Mouse;

public class GuiComboBox extends GuiElement {

	public String current;
	public List<String> list;
	public int width, maxEntry;
	protected boolean extended;
	public int index, page = 1, maxPage = 1;

	public GuiComboBox(GuiScreen parent,int id, int x, int y, List<String> list, int width, int maxEntry, int index) {
		super(id, x, y,parent);
		this.current = list.isEmpty() ? "" : list.get(0);
		this.list = list;
		this.width = width;
		this.maxEntry = maxEntry;
		this.index = index;
	}

	public GuiComboBox(GuiScreen parent,int id, int x, int y, List<String> list, int width, int maxEntry) {
		this(parent,id, x, y, list, width, maxEntry, 0);
	}

	@Override
	public void drawBackground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		if (GuiScreen.isShiftKeyDown())
			width = new Random().nextInt(100) + 20;
		maxPage = Math.max(1, 1 + list.size() - maxEntry);
		if (maxPage < 1)
			maxPage = 1;
		if (page < 1)
			page = 1;
		if (page > maxPage)
			page = maxPage;
		mc.getTextureManager().bindTexture(GuiHelper.icons);
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + width && mouseY < this.y + 12;
		GuiUtils.drawTexturedModalRect(this.x, this.y, 0, 55, this.width / 2, 12,300);
		GuiUtils.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 55, this.width / 2, 12,300);
		String t = current;
		while (mc.fontRendererObj.getStringWidth(t) > width - 9)
			t = t.substring(0, t.length() - 1);
		mc.fontRendererObj.drawString(t, this.x + 3, this.y + (4) / 2, 14737632);
		mc.getTextureManager().bindTexture(GuiHelper.icons);
		GuiUtils.drawTexturedModalRect(this.x + this.width - 9, this.y, 200, 55, 9, 12,300);

	}

	@Override
	public void drawForeground(Minecraft mc, int mouseX, int mouseY) {
		if (!visible || !extended)
			return;
		boolean changed = false;
		for (int i = 0; i < (Math.min(maxEntry, list.size())); i++) {
			String t = list.get(i + page - 1);
			while (mc.fontRendererObj.getStringWidth(t) > width - 6)
				t = t.substring(0, t.length() - 1);
			int in = (i + 1) * 9;
			boolean num = mouseX >= this.x + 2 && mouseY >= this.y + in + 5 && mouseX < this.x - 2 + width && mouseY < this.y + 9 + in + 5;
			mc.fontRendererObj.drawString(t, this.x + 3, this.y + 4 + in, 14737632 - (num ? 0x222200 : 0), true);
			if (num) {
				index = i + page - 1;
				changed = true;
			}
		}
		if (!changed)
			index = -1;

	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
		if (extended && index >= 0)
			current = list.get(index);
		if (hovered)
			extended = !extended;
		else {
			if (extended)
				extended = !extended;
		}
		return false;
	}

	@Override
	public void handleMouseInput() {
		if (extended) {
			int mouse = Mouse.getEventDWheel();
			if (mouse == 0)
				return;
			if (mouse > 0 && page > 1)
				page--;
			if (mouse < 0 && page < maxPage)
				page++;
		}
	}

}
