package mrriegel.testmod;

import mrriegel.limelib.gui.element.GuiCheckBox;
import mrriegel.limelib.gui.element.GuiComboBox;
import mrriegel.limelib.gui.element.GuiLabel;
import mrriegel.limelib.gui.element.GuiSlot;
import mrriegel.limelib.util.StackWrapper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

public class TestGui extends GuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	GuiCheckBox check;
	GuiComboBox combo;
	GuiSlot slot;
	GuiLabel label;

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_MISSING_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		// check.drawBackground(mc, mouseX, mouseY);
		// combo.drawBackground(mc, mouseX, mouseY);
		// slot.drawBackground(mc, mouseX, mouseY);
		// label.drawBackground(mc, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		// check.drawForeground(mc, mouseX, mouseY);
		// combo.drawForeground(mc, mouseX, mouseY);
		// slot.drawForeground(mc, mouseX, mouseY);
		// label.drawForeground(mc, mouseX, mouseY);
	}

	@Override
	public void initGui() {
		super.initGui();
		check = new GuiCheckBox(this, 0, 0, 0, false);
		combo = new GuiComboBox(this, 1, 30, 0, Lists.newArrayList("laura", "peter"), 18, 3);
		slot = new GuiSlot(this, 2, 60, 0, new StackWrapper(new ItemStack(Blocks.COAL_BLOCK), 32), true, true, false, true);
		label = new GuiLabel(this, 3, 44, 0, "hallo hey low?", 1241241, false);
	}

}
