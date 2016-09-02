package mrriegel.testmod;

import java.io.IOException;

import mrriegel.limelib.gui.CommonContainerTile;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiButtonColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;

public class TestGui extends CommonGuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButtonColor(3, 11 + guiLeft, 17 + guiTop, 33, 15, "DUMB", EnumDyeColor.BLUE));
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
