package mrriegel.limelib.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Keyboard;

public class CommonGuiContainerSub extends CommonGuiContainer {

	GuiScreen parent;

	public CommonGuiContainerSub(GuiScreen parent, Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.parent = mc.currentScreen;
	}

	public CommonGuiContainerSub(GuiScreen parent, Container inventorySlotsIn, boolean darkBackground) {
		super(inventorySlotsIn, darkBackground);
		this.parent = mc.currentScreen;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE && parent != null) {
			onClosed();
			mc.currentScreen = parent;
		} else
			super.keyTyped(typedChar, keyCode);
	}

	protected void onClosed() {
	};

}
