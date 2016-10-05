package mrriegel.limelib.book;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import mrriegel.limelib.gui.CommonGuiScreen;
import mrriegel.limelib.gui.element.GuiButtonSimple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

import com.google.common.collect.Lists;

public class GuiBook extends CommonGuiScreen {

	protected Book book;

	GuiButtonSimple left, right;

	private static final int maxLines = 17;
	protected List<GuiButton> buttons;
	protected List<GuiButton> subChapters = Lists.newArrayList();
	protected String currentText = TextFormatting.BOLD + Loader.instance().activeModContainer().getName();
	protected int max = 1, current = 1;
	protected Chapter main;

	public GuiBook(Book book) {
		this.book = book;
		this.xSize = 340;
		this.ySize = 200;
		buttons = Lists.newArrayList();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		drawer.drawFramedRectangle(5, 5, 50, ySize - 10);
		drawer.drawFramedRectangle(57, 5, 50, ySize - 10);
		drawer.drawFrame(109, 5, 225, ySize - 26, 1, Color.BLACK.getRGB());
		drawer.drawFrame(110, 6, 223, ySize - 28, 1, Color.DARK_GRAY.getRGB());

		String pages = current + "/" + max;
		drawString(fontRendererObj, pages, guiLeft + (218 - fontRendererObj.getStringWidth(pages) / 2), guiTop + 185, 14737632);
		List<String> wrappedTextLines = fontRendererObj.listFormattedStringToWidth(currentText, 215);
		max = wrappedTextLines.size() / maxLines;
		if (wrappedTextLines.size() % maxLines != 0)
			max++;
		for (int i = 0; i < Math.min(wrappedTextLines.size(), maxLines); i++) {
			int index = i + (current - 1) * maxLines;
			if (index >= wrappedTextLines.size())
				break;
			fontRendererObj.drawString(wrappedTextLines.get(index), guiLeft + 113, guiTop + 9 + i * 10, Color.BLACK.getRGB());
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(left = new GuiButtonSimple(0, guiLeft + 109, guiTop + 181, 9, 14, "<", Color.black.getRGB(), Color.gray.getRGB(), null));
		buttonList.add(right = new GuiButtonSimple(1, guiLeft + 326, guiTop + 181, 9, 14, ">", Color.black.getRGB(), Color.gray.getRGB(), null));
		for (int i = 0; i < book.chapters.size(); i++) {
			buttonList.add(new GuiButtonSimple(i + 100, guiLeft + 7, guiTop + 7 + i * 18, 46, 15, book.chapters.get(i).name, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB(), Lists.newArrayList(book.chapters.get(i).name)));
		}
		for (int i = 0; i < 10; i++) {
			GuiButtonSimple b = new GuiButtonSimple(i + 1000, guiLeft + 59, guiTop + 7 + i * 18, 46, 15, "", Color.DARK_GRAY.getRGB(), Color.GRAY.getRGB(), null);
			b.visible = false;
			subChapters.add(b);
		}
		buttonList.addAll(subChapters);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0)
			current = Math.max(1, current - 1);
		else if (button.id == 1)
			current = Math.min(max, current + 1);
		else if (button.id >= 100 && button.id <= 999) {
			Chapter c = book.chapters.get(button.id - 100);
			if (c != null) {
				main = c;
				int subs = c.subChapters.size();
				for (int i = 0; i < subChapters.size(); i++) {
					if (i < subs) {
						subChapters.get(i).visible = true;
						subChapters.get(i).displayString = c.subChapters.get(i).name;
						((GuiButtonSimple) subChapters.get(i)).setTooltip(Lists.newArrayList(c.subChapters.get(i).name));
					} else {
						subChapters.get(i).visible = false;
					}
				}
			}
		} else if (button.id >= 1000 && button.id <= 9999) {
			if (main != null) {
				Chapter sc = main.subChapters.get(button.id - 1000);
				if (sc != null) {
					currentText = TextFormatting.BOLD + sc.name + TextFormatting.RESET + "\n" + sc.text;
					current = 1;
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
