package mrriegel.limelib.gui;


public interface IGuiBase {
	public int getGuiLeft();

	public int getGuiRight();

	public int getGuiTop();

	public int getGuiBottom();

	public int getXSize();

	public int getYSize();

	abstract void drawGuiForegroundLayer(int mouseX, int mouseY);

	public void drawGuiBackgroundLayer(float partialTicks, int mouseX,
			int mouseY);

}
