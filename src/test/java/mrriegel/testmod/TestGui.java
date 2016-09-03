package mrriegel.testmod;

import java.io.IOException;

import com.google.common.collect.Lists;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.CommonGuiContainer;
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
		buttonList.add(new AbstractSlot.ItemSlot(new ItemStack(Items.BEEF), 5, 5 + guiLeft, 60 + guiTop, 32, true, true, true, true));
		buttonList.add(new GuiButtonTooltip(5, 5 + guiLeft, 80 + guiTop, 32, 20, "ku", EnumDyeColor.YELLOW, Lists.newArrayList("horde", "maul")));
		t = new GuiTextField(1, fontRendererObj, guiLeft + 130, guiTop + 77, 45, fontRendererObj.FONT_HEIGHT);
		t.setEnableBackgroundDrawing(false);
		t.setFocused(true);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawTextfield(t);
		t.drawTextBox();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// TODO Auto-generated method stub
		super.actionPerformed(button);
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
