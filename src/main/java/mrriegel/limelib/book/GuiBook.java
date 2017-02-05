package mrriegel.limelib.book;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import mezz.jei.Internal;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.gui.Focus;
import mrriegel.limelib.gui.CommonGuiScreen;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.button.GuiButtonSimple;
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

public class GuiBook extends CommonGuiScreen {

	protected Book book;

	protected GuiButtonSimple left, right;

	private static final int maxLines = 17;
	private static final int maxSubChapters = 11;
	protected List<GuiButton> articleButtons = Lists.newArrayList();
	protected String currentText;
	protected int maxPage = 1, currentPage = 1, articlePos = 0, chapter = -1, article = -1;
	protected Chapter currentChapter;
	protected Article currentArticle;
	protected List<ItemSlot> slots = Lists.newArrayList();
	protected final boolean unicode = false;

	public GuiBook(Book book) {
		this.book = book;
		this.xSize = 340;
		this.ySize = 200;
		ModContainer mod = Loader.instance().getIndexedModList().get(Utils.getCurrentModID());
		currentText = TextFormatting.BOLD + mod.getName() + " - " + mod.getVersion() + "\n\n" + MinecraftForge.MC_VERSION;
	}

	public GuiBook(Book book, int chapter, int article) {
		this(book);
		this.chapter = chapter;
		this.article = article;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		drawer.drawFramedRectangle(5, 5, 50, ySize - 10);
		drawer.drawFramedRectangle(57, 5, 50, ySize - 10);
		drawer.drawFrame(109, 5, 225, ySize - 26, 1, Color.BLACK.getRGB());
		drawer.drawFrame(110, 6, 223, ySize - 28, 1, Color.DARK_GRAY.getRGB());
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawer.drawColoredRectangle(111, 7, 222, ySize - 29, ColorHelper.brighter(0xffd39b, 0.22));

		String pages = currentPage + "/" + maxPage;
		drawString(fontRendererObj, pages, guiLeft + (218 - fontRendererObj.getStringWidth(pages) / 2), guiTop + 185, 14737632);
		List<String> wrappedTextLines = fontRendererObj.listFormattedStringToWidth(currentText, 215);
		maxPage = wrappedTextLines.size() / maxLines;
		if (wrappedTextLines.size() % maxLines != 0)
			maxPage++;
		for (int i = 0; i < Math.min(wrappedTextLines.size(), maxLines); i++) {
			int index = i + (currentPage - 1) * maxLines;
			if (index >= wrappedTextLines.size())
				break;

			fontRendererObj.drawString(wrappedTextLines.get(index), guiLeft + 113, guiTop + 9 + i * 10, 0x111111, !true);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean uni = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(unicode);
		super.drawScreen(mouseX, mouseY, partialTicks);
		fontRendererObj.setUnicodeFlag(uni);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(left = new GuiButtonSimple(0, guiLeft + 109, guiTop + 181, 9, 14, "<", Color.black.getRGB(), Color.gray.getRGB(), null));
		buttonList.add(right = new GuiButtonSimple(1, guiLeft + 326, guiTop + 181, 9, 14, ">", Color.black.getRGB(), Color.gray.getRGB(), null));
		for (int i = 0; i < Article.maxItems; i++)
			slots.add(new ItemSlot(null, i, 0, guiTop + 8, 1, drawer, false, false, false, true));
		elementList.addAll(slots);
		for (int i = 0; i < book.chapters.size(); i++) {
			List<String> tooltip = Lists.newArrayList(book.chapters.get(i).name);
			for (Article c : book.chapters.get(i).articles)
				tooltip.add(TextFormatting.GRAY + "  " + c.name);
			/** clear tooltip */
			tooltip.clear();
			buttonList.add(new GuiButtonSimple(i + 100, guiLeft + 7, guiTop + 7 + i * 18, 46, 15, book.chapters.get(i).name, tooltip));
		}
		for (int i = 0; i < maxSubChapters; i++) {
			GuiButtonSimple b = new GuiButtonSimple(i + 1000, guiLeft + 59, guiTop + 7 + i * 17, 46, 14, "", Color.DARK_GRAY.getRGB(), Color.GRAY.getRGB(), null);
			b.visible = false;
			articleButtons.add(b);
		}
		buttonList.addAll(articleButtons);
		openLast();
	}

	private void openLast() {
		if (chapter != -1 && article != -1) {
			articlePos = 0;
			currentChapter = book.chapters.get(chapter);
			initArticleButtons();
			currentArticle = currentChapter.articles.get(article);
			currentText = formatText();
			chapter = -1;
			initSlots();
		} else {
			if (book.lastChapter != null) {
				articlePos = 0;
				currentChapter = book.lastChapter;
				initArticleButtons();
			}
			if (book.lastArticle != null) {
				currentArticle = book.lastArticle;
				currentText = formatText();
				currentPage = book.lastPage;
				initSlots();
			}
		}
	}

	private String formatText() {
		String s = TextFormatting.BOLD + currentArticle.name + TextFormatting.RESET + "\n\n" + currentArticle.text;
		s = s.replaceAll("<r>", TextFormatting.RESET.toString());
		s = s.replaceAll("<b>", TextFormatting.BOLD.toString());
		s = s.replaceAll("<i>", TextFormatting.ITALIC.toString());
		s = s.replaceAll("<u>", TextFormatting.UNDERLINE.toString());
		s = s.replaceAll("<s>", TextFormatting.STRIKETHROUGH.toString());
		for (int i = 0; i < TextFormatting.values().length; i++) {
			s = s.replaceAll("<" + i + ">", TextFormatting.values()[i].toString());
			s = s.replaceAll("<" + TextFormatting.values()[i].getFriendlyName() + ">", TextFormatting.values()[i].toString());
		}
		return s;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		book.lastChapter = currentChapter != null ? currentChapter : null;
		book.lastArticle = currentArticle != null ? currentArticle : null;
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
				articlePos = 0;
				currentChapter = c;
				initArticleButtons();
			}
		} else if (button.id >= 1000 && button.id <= 9999) {
			if (currentChapter != null) {
				Article sc = currentChapter.articles.get((button.id - 1000) + articlePos);
				if (sc != null) {
					currentArticle = sc;
					currentText = formatText();
					currentPage = 1;
				}
			}
		}
		if (currentArticle != null)
			initSlots();
	}

	private void initSlots() {
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).stack = ItemStack.EMPTY;
		}
		if (currentPage != 1)
			return;
		for (int i = 0; i < Math.min(currentArticle.stacks.size(), slots.size()); i++) {
			slots.get(i).stack = currentArticle.stacks.get(i);
			slots.get(i).x = guiLeft + 114 + (i * 17) + fontRendererObj.getStringWidth(TextFormatting.BOLD + currentArticle.name);
		}
	}

	private void initArticleButtons() {
		for (int i = 0; i < articleButtons.size(); i++) {
			if (i < currentChapter.articles.size()) {
				articleButtons.get(i).visible = true;
				articleButtons.get(i).displayString = currentChapter.articles.get(i + articlePos).name;
			} else {
				articleButtons.get(i).visible = false;
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseX = GuiDrawer.getMouseX();
		int mouseY = GuiDrawer.getMouseY();
		if (currentChapter != null) {
			GuiButton first = articleButtons.get(0), last = articleButtons.get(articleButtons.size() - 1);
			if (isPointInRegion(first.xPosition, first.yPosition, first.width, last.yPosition + last.height - first.yPosition, guiLeft + mouseX, guiTop + mouseY)) {
				int m = Mouse.getEventDWheel();
				if (m > 0)
					articlePos = Math.max(0, articlePos - 1);
				else if (m < 0)
					articlePos = Math.min(Math.max(0, currentChapter.articles.size() - maxSubChapters), articlePos + 1);
				initArticleButtons();
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (!Loader.isModLoaded("jei"))
			return;
		for (ItemSlot slot : slots) {
			if (!slot.stack.isEmpty() && slot.isMouseOver(mouseX, mouseY) && (mouseButton == 0 || mouseButton == 1)) {
				Internal.getRuntime().getRecipesGui().show(new Focus<ItemStack>(mouseButton == 0 ? Mode.OUTPUT : Mode.INPUT, slot.stack));
				break;
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
