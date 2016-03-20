package mrriegel.limelib.gui;

import java.io.IOException;
import java.util.List;

import mrriegel.limelib.gui.element.GuiLabel;
import mrriegel.limelib.gui.element.GuiSlot;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

public class GuiContainerBase extends GuiContainer implements IGuiBase {

	protected List<GuiSlot> slotList = Lists.<GuiSlot> newArrayList();
	protected List<GuiLabel> labelList = Lists.<GuiLabel> newArrayList();

	public GuiContainerBase(Container inventorySlotsIn, int xSize, int ySize) {
		super(inventorySlotsIn);
		this.xSize = xSize;
		this.ySize = ySize;
	}

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiRight() {
		return guiLeft + xSize;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public int getGuiBottom() {
		return guiTop + ySize;
	}

	@Override
	public int getXSize() {
		return xSize;
	}

	@Override
	public int getYSize() {
		return ySize;
	}

	@Override
	public void initGui() {
		super.initGui();
		slotList = Lists.<GuiSlot> newArrayList();
		labelList = Lists.<GuiLabel> newArrayList();
	}

	@Override
	public void drawGuiForegroundLayer(int mouseX, int mouseY) {

	}

	@Override
	public void drawGuiBackgroundLayer(float partialTicks, int mouseX,
			int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,
			int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (GuiSlot s : slotList) {
			if (s.mousePressed(mc, mouseX, mouseY)) {
				slotClicked(s, mouseButton);
			}
		}
		for (GuiLabel s : labelList) {
			if (s.mousePressed(mc, mouseX, mouseY)) {
				labelClicked(s, mouseButton);
			}
		}
	}

	protected void slotClicked(GuiSlot slot, int mouseButton)
			throws IOException {
	}

	protected void labelClicked(GuiLabel label, int mouseButton)
			throws IOException {
	}

	protected void drawInventory(int x, int y) {
		this.mc.getTextureManager().bindTexture(
				new ResourceLocation("limelib:textures/gui/base.png"));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.drawTexturedModalRect(x + j * 18, y + i * 18, 200, 0, 18,
						18);
				// this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8
				// + j * 18, 174 + i * 18));
			}
		}
		// for (int i = 0; i < 9; ++i) {
		// this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 232));
		// }
	}

}
