package mrriegel.testmod;

import java.io.IOException;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiDrawer.Direction;
import mrriegel.limelib.gui.element.AbstractSlot;
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
import mrriegel.limelib.gui.element.GuiButtonArrow;
import mrriegel.limelib.gui.element.GuiButtonTooltip;
import mrriegel.limelib.gui.element.MCLabel;
import mrriegel.limelib.gui.element.MCPanel;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

public class TestGui extends CommonGuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	GuiTextField t;
	ItemSlot s;
	TestTile tile;

	@Override
	public void initGui() {
		super.initGui();
		tile = (TestTile) ((CommonContainerTile) inventorySlots).getTile();
		// buttonList.add(new GuiButtonColor(3, 11 + guiLeft, 17 + guiTop, 33,
		// 22, "DUMB", EnumDyeColor.WHITE));
		buttonList.add(new GuiButtonArrow(4, 10 + guiLeft, 50 + guiTop, Direction.UP));
		buttonList.add(s = new AbstractSlot.ItemSlot(new ItemStack(Items.BEEF), 5, 5 + guiLeft, 60 + guiTop, 3200, false, true, true, true));
		buttonList.add(new GuiButtonTooltip(5, guiLeft - 19, 78 + guiTop, 18, 22, "if", EnumDyeColor.PURPLE, Lists.newArrayList("horde", "maul")));
		t = new GuiTextField(1, fontRendererObj, guiLeft + 130, guiTop + 77, 45, fontRendererObj.FONT_HEIGHT);
		t.setEnableBackgroundDrawing(false);
		t.setFocused(true);
		t.setText(tile.k + "");

		panel = new MCPanel(60, 0, 100, 100, drawer);
		MCPanel p1 = new MCPanel(5, 5, 60, 40, drawer);
		MCPanel p2 = new MCPanel(10, 9, 32, 25, drawer);
		// p2.add(new MCPanel(7, 7, 9, 9, drawer));
		p1.add(p2);
		panel.add(p1);
//		panel.add(new MCLabel(4, 40, "oh damit", 0xff0000, drawer));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		if (mouseX % 2 != 2) {
			drawer.drawPlayerSlots(19, 99);
			drawer.drawSlots(64, 19, 3, 3);
			int k = (int) System.currentTimeMillis();
			k /= 65;
			drawer.drawProgressArrow(148, 12, 0.85f, Direction.LEFT);
			drawer.drawSizedSlot(150, 35, 9);
			drawer.drawTextfield(t);
			int c = 99;
			drawer.drawFrame(122, 11, 9, 48, 1, ColorHelper.getRGB(EnumDyeColor.BLACK));
			drawer.drawEnergyBarV(123, 12, 46, 0.69f);
			// drawer.drawColoredRectangle(120, 61, 18, 36,
			// ColorHelper.getRGB(EnumDyeColor.BLUE));
			t.drawTextBox();
			fontRendererObj.drawString(TextFormatting.AQUA + "ZEuth", 3, 4, 0xff);
			drawer.drawStopSign(180, 12);
			drawer.drawFlame(18, 18, 1.0f);
			drawer.drawScrollbar(0, 0, 100, 1.00f, Direction.RIGHT);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		NBTTagCompound l = new NBTTagCompound();
		l.setString("fine", "number3");
		l.setLong("fine2", "amerose".hashCode() + Long.MAX_VALUE);
		tile.sendMessage(l);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!checkHotbarKeys(keyCode)) {
			if (t.textboxKeyTyped(typedChar, keyCode)) {
				try {
					tile.k = Integer.valueOf(t.getText());
				} catch (Exception e) {
					tile.k = 999;
				}
				NBTTagCompound nbt = new NBTTagCompound();
				NBTHelper.setInt(nbt, "k", tile.k);
				tile.sendMessage(nbt);
			} else
				super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (s.enabled && s.visible && s.isMouseOver()) {
			if (mc.thePlayer.inventory.getItemStack() != null)
				s.stack = mc.thePlayer.inventory.getItemStack();
			else
				s.stack = null;
		}
	}

}
