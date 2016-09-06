package mrriegel.testmod;

import java.io.IOException;

import com.google.common.collect.Lists;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.CommonGuiContainer.Direction;
import mrriegel.limelib.gui.element.AbstractSlot;
import mrriegel.limelib.gui.element.GuiButtonArrow;
import mrriegel.limelib.gui.element.GuiButtonColor;
import mrriegel.limelib.gui.element.GuiButtonTooltip;
import mrriegel.limelib.gui.element.GuiScrollbar;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

public class TestGui extends CommonGuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	GuiTextField t;

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonColor(3, 11 + guiLeft, 17 + guiTop, 33, 22, "DUMB", EnumDyeColor.WHITE));
		buttonList.add(new GuiButtonArrow(4, 10 + guiLeft, 50 + guiTop, Direction.UP));
		buttonList.add(new AbstractSlot.ItemSlot(new ItemStack(Items.BEEF), 5, 5 + guiLeft, 60 + guiTop, 3200, true, false, true, true));
		buttonList.add(new GuiButtonTooltip(5, 5 + guiLeft, 78 + guiTop, 32, 20, "ku", EnumDyeColor.BLACK, Lists.newArrayList("horde", "maul")));
		t = new GuiTextField(1, fontRendererObj, guiLeft + 130, guiTop + 77, 45, fontRendererObj.FONT_HEIGHT);
		t.setEnableBackgroundDrawing(false);
		t.setFocused(true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// drawBackgroundTexture(0, 0, 80, 80);
		// drawBackgroundTexture(90, 0, 80, 80);
		// drawBackgroundTexture(0, 95, 80, 80);
		// drawBackgroundTexture(90, 95, 80, 80);
		drawBackgroundTexture();
		// drawSlot(32, 13);
		drawPlayerSlots(19, 99);
		drawSlots(64, 19, 3, 3);
		int k = (int) System.currentTimeMillis();
		k /= 65;
		drawProgressArrow(148, 12, 0.4f, Direction.LEFT);
		// drawTextfield(138, 67, 32);
		// drawScrollbar(4, 89, 50, 0, Direction.DOWN);
		drawSizedSlot(150, 35, 9);
		drawTextfield(t);
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
}
