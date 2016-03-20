package mrriegel.limelib.gui.element;

import net.minecraft.client.Minecraft;

public interface IGuiElement {
	public void drawBackground(Minecraft mc, int mouseX, int mouseY);

	public void drawForeground(Minecraft mc, int mouseX, int mouseY);
	
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY);
}
