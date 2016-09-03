package mrriegel.testmod;

import java.io.IOException;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiButtonArrow;
import mrriegel.limelib.gui.GuiButtonColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;

public class TestGui extends CommonGuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	GuiTextField t;

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonColor(3, 11 + guiLeft, 17 + guiTop, 33, 22, "DUMB", EnumDyeColor.WHITE));
		buttonList.add(new GuiButtonArrow(4, 10+guiLeft, 50+guiTop, Direction.UP));
		t=new GuiTextField(1, fontRendererObj, guiLeft+130, guiTop+77, 45, fontRendererObj.FONT_HEIGHT);
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
		((CommonContainerTile) inventorySlots).getTile().sendMessge(l);
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
