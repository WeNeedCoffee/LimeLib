package mrriegel.testmod;

import java.io.IOException;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiDrawer.Direction;
import mrriegel.limelib.gui.element.AbstractSlot;
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
import mrriegel.limelib.gui.element.GuiButtonArrow;
import mrriegel.limelib.gui.element.GuiButtonColor;
import mrriegel.limelib.gui.element.GuiButtonTooltip;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class TestGui extends CommonGuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	GuiTextField t;
	ItemSlot s;

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonColor(3, 11 + guiLeft, 17 + guiTop, 33, 22, "DUMB", EnumDyeColor.WHITE));
		buttonList.add(new GuiButtonArrow(4, 10 + guiLeft, 50 + guiTop, Direction.UP));
		buttonList.add(s = new AbstractSlot.ItemSlot(new ItemStack(Items.BEEF), 5, 5 + guiLeft, 60 + guiTop, 3200, false, true, true, true));
		buttonList.add(new GuiButtonTooltip(5, guiLeft - 19, 78 + guiTop, 18, 22, "if", EnumDyeColor.PURPLE, Lists.newArrayList("horde", "maul")));
		t = new GuiTextField(1, fontRendererObj, guiLeft + 130, guiTop + 77, 45, fontRendererObj.FONT_HEIGHT);
		t.setEnableBackgroundDrawing(false);
		t.setFocused(true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		drawer.drawPlayerSlots(19, 99);
		drawer.drawSlots(64, 19, 3, 3);
		int k = (int) System.currentTimeMillis();
		k /= 65;
		drawer.drawProgressArrow(148, 12, 0.4f, Direction.LEFT);
		drawer.drawSizedSlot(150, 35, 9);
		drawer.drawTextfield(t);
		t.drawTextBox();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		NBTTagCompound l = new NBTTagCompound();
		l.setString("fine", "number3");
		l.setLong("fine2", "amerose".hashCode() + Long.MAX_VALUE);
		((CommonContainerTile) inventorySlots).getTile().sendMessage(l);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!checkHotbarKeys(keyCode)) {
			if (t.textboxKeyTyped(typedChar, keyCode))
				;
			else
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
