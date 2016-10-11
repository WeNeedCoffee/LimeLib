package mrriegel.limelib.book;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import mrriegel.limelib.gui.CommonGuiScreen;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
import mrriegel.limelib.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

public class GuiBook extends CommonGuiScreen {

	protected Book book;

	GuiButtonSimple left, right;

	private static final int maxLines = 17;
	private static final int maxSubChapters = 11;
	protected List<GuiButton> subChapButtons = Lists.newArrayList();
	protected String currentText;
	protected int maxPage = 1, currentPage = 1, subChapterPos = 0;
	protected Chapter main;
	protected SubChapter subMain;
	protected List<ItemSlot> slots = Lists.newArrayList();
	private int chapter = -1, subChapter = -1;

	public GuiBook(Book book) {
		this.book = book;
		this.xSize = 340;
		this.ySize = 200;
		ModContainer mod = Loader.instance().getIndexedModList().get(Utils.getCurrentModID());
		currentText = TextFormatting.BOLD + mod.getName() + "  " + mod.getVersion() + "\n\n" + MinecraftForge.MC_VERSION;
	}

	public GuiBook(Book book, int chapter, int subChapter) {
		this(book);
		this.chapter = chapter;
		this.subChapter = subChapter;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		drawer.drawFramedRectangle(5, 5, 50, ySize - 10);
		drawer.drawFramedRectangle(57, 5, 50, ySize - 10);
		drawer.drawFrame(109, 5, 225, ySize - 26, 1, Color.BLACK.getRGB());
		drawer.drawFrame(110, 6, 223, ySize - 28, 1, Color.DARK_GRAY.getRGB());
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		// drawer.drawColoredRectangle(111, 7, 222, ySize - 29,
		// ColorHelper.darker(Color.DARK_GRAY.getRGB(), 0.1));

		String pages = currentPage + "/" + maxPage;
		drawString(fontRendererObj, pages, guiLeft + (218 - fontRendererObj.getStringWidth(pages) / 2), guiTop + 185, 14737632);
		List<String> wrappedTextLines = fontRendererObj.listFormattedStringToWidth(currentText, 215);
		maxPage = wrappedTextLines.size() / maxLines;
		if (wrappedTextLines.size() % maxLines != 0)
			maxPage++;
		GlStateManager.pushMatrix();
		double scale = .7;
		GlStateManager.scale(scale, scale, 0);
		GlStateManager.translate((guiLeft) * scale, (guiTop) * scale, 0);
		for (int i = 0; i < Math.min(wrappedTextLines.size(), maxLines); i++) {
			int index = i + (currentPage - 1) * maxLines;
			if (index >= wrappedTextLines.size())
				break;
			fontRendererObj.drawString(wrappedTextLines.get(index), guiLeft + 113, guiTop + 9 + i * 10, 0x111111, !true);
		}
		GlStateManager.popMatrix();
		if (!isShiftKeyDown() && subMain != null) {
			// drawer.drawItemStack(subMain.stack, 315, 8);
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(left = new GuiButtonSimple(0, guiLeft + 109, guiTop + 181, 9, 14, "<", Color.black.getRGB(), Color.gray.getRGB(), null));
		buttonList.add(right = new GuiButtonSimple(1, guiLeft + 326, guiTop + 181, 9, 14, ">", Color.black.getRGB(), Color.gray.getRGB(), null));
		for (int i = 0; i < 10; i++)
			slots.add(new ItemSlot(null, 0, 0, guiTop + 7, 1, drawer, false, false, false, true));
		elementList.addAll(slots);
		for (int i = 0; i < book.chapters.size(); i++) {
			List<String> tooltip = Lists.newArrayList(book.chapters.get(i).name);
			for (SubChapter c : book.chapters.get(i).subChapters)
				tooltip.add(TextFormatting.GRAY + "  " + c.name);
			/** clear tooltip */
			tooltip.clear();
			buttonList.add(new GuiButtonSimple(i + 100, guiLeft + 7, guiTop + 7 + i * 18, 46, 15, book.chapters.get(i).name, Color.BLACK.getRGB(), Color.DARK_GRAY.getRGB(), tooltip));
		}
		for (int i = 0; i < maxSubChapters; i++) {
			GuiButtonSimple b = new GuiButtonSimple(i + 1000, guiLeft + 59, guiTop + 7 + i * 17, 46, 14, "", Color.DARK_GRAY.getRGB(), Color.GRAY.getRGB(), null);
			b.visible = false;
			subChapButtons.add(b);
		}
		buttonList.addAll(subChapButtons);
		openLast();
	}

	private void openLast() {
		if (chapter != -1 && subChapter != -1) {
			subChapterPos = 0;
			main = book.chapters.get(chapter);
			initSubChapters();
			subMain = main.subChapters.get(subChapter);
			currentText = formatText();
			initSlots();
		} else {
			if (book.lastChapter != null) {
				subChapterPos = 0;
				main = book.lastChapter;
				initSubChapters();
			}
			if (book.lastSubChapter != null) {
				subMain = book.lastSubChapter;
				currentText = formatText();
				currentPage = book.lastPage;
				initSlots();
			}
		}
	}

	private String formatText() {
		String s = TextFormatting.BOLD + subMain.name + TextFormatting.RESET + "\n\n" + subMain.text;
		s = s.replaceAll("<r>", TextFormatting.RESET.toString());
		s = s.replaceAll("<b>", TextFormatting.BOLD.toString());
		s = s.replaceAll("<i>", TextFormatting.ITALIC.toString());
		s = s.replaceAll("<u>", TextFormatting.UNDERLINE.toString());
		s = s.replaceAll("<s>", TextFormatting.STRIKETHROUGH.toString());
		return s;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		book.lastChapter = main != null ? main : null;
		book.lastSubChapter = subMain != null ? subMain : null;
		book.lastPage = currentPage;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0)
			currentPage = Math.max(1, currentPage - 1);
		else if (button.id == 1)
			currentPage = Math.min(maxPage, currentPage + 1);
		else if (button.id >= 100 && button.id <= 999) {
			Chapter c = book.chapters.get(button.id - 100);
			if (c != null) {
				subChapterPos = 0;
				main = c;
				initSubChapters();
			}
		} else if (button.id >= 1000 && button.id <= 9999) {
			if (main != null) {
				SubChapter sc = main.subChapters.get((button.id - 1000) + subChapterPos);
				if (sc != null) {
					subMain = sc;
					currentText = formatText();
					currentPage = 1;
				}
			}
		}
		initSlots();
	}

	private void initSlots() {
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).stack = null;
		}
		if (currentPage != 1)
			return;
		for (int i = 0; i < Math.min(subMain.stacks.size(), slots.size()); i++) {
			slots.get(i).stack = subMain.stacks.get(i);
			slots.get(i).x = guiLeft + 114 + (i * 18) + fontRendererObj.getStringWidth(TextFormatting.BOLD + subMain.name);
		}
	}

	private void initSubChapters() {
		int subs = main.subChapters.size();
		for (int i = 0; i < subChapButtons.size(); i++) {
			if (i < subs) {
				subChapButtons.get(i).visible = true;
				subChapButtons.get(i).displayString = main.subChapters.get(i + subChapterPos).name;
			} else {
				subChapButtons.get(i).visible = false;
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseX = GuiDrawer.getMouseX();
		int mouseY = GuiDrawer.getMouseY();
		if (main != null) {
			GuiButton first = subChapButtons.get(0), last = subChapButtons.get(subChapButtons.size() - 1);
			if (isPointInRegion(first.xPosition, first.yPosition, first.width, last.yPosition + last.height - first.yPosition, guiLeft + mouseX, guiTop + mouseY)) {
				int m = Mouse.getEventDWheel();
				if (m > 0)
					subChapterPos = Math.max(0, subChapterPos - 1);
				else if (m < 0)
					subChapterPos = Math.min(Math.max(0, main.subChapters.size() - maxSubChapters), subChapterPos + 1);
				initSubChapters();
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
