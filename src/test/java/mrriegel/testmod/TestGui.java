package mrriegel.testmod;

import java.io.IOException;

import mrriegel.limelib.gui.CommonContainerTile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class TestGui extends GuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		// drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonExt(44, 0 + guiLeft, 0 + guiTop, 44, 11, "klick"));
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

}
