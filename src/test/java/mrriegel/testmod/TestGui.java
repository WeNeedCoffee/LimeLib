package mrriegel.testmod;

import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import mrriegel.limelib.gui.CommonContainerTileInventory;
import mrriegel.limelib.gui.CommonGuiContainer;
import mrriegel.limelib.gui.GuiDrawer.Direction;
import mrriegel.limelib.gui.button.GuiButtonArrow;
import mrriegel.limelib.gui.element.AbstractSlot;
import mrriegel.limelib.gui.element.AbstractSlot.ItemSlot;
import mrriegel.limelib.helper.ColorHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class TestGui extends CommonGuiContainer {

	public TestGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	GuiTextField t;
	ItemSlot s;
	TestTile tile;
	List<Point> points = Lists.newArrayList();

	@Override
	public void initGui() {
		super.initGui();
		tile = (TestTile) ((CommonContainerTileInventory<?>) inventorySlots).getTile();
		buttonList.add(new GuiButtonExt(3, 11 + guiLeft, 17 + guiTop, 33, 22, "DUMB"));
		buttonList.add(new GuiButtonArrow(4, 10 + guiLeft, 50 + guiTop, Direction.UP));
		elementList.add(s = new AbstractSlot.ItemSlot(new ItemStack(Items.COOKED_BEEF), 5, 5 + guiLeft, 60 + guiTop, 3200, drawer, false, true, true, true));
		//		buttonList.add(new GuiButtonTooltip(5, guiLeft - 19, 78 + guiTop, 18, 22, "if", EnumDyeColor.PURPLE, Lists.newArrayList("horde", "maul")));
		t = new GuiTextField(1, fontRenderer, guiLeft + 130, guiTop + 77, 45, fontRenderer.FONT_HEIGHT);
		t.setEnableBackgroundDrawing(false);
		t.setFocused(true);
		t.setText(tile.k + "");

		//		panel = new MCPanel(60, 0, 100, 100, drawer);
		//		MCPanel p1 = new MCPanel(5, 5, 60, 40, drawer);
		//		MCPanel p2 = new MCPanel(10, 9, 32, 25, drawer);
		//		 p2.add(new MCPanel(7, 7, 9, 9, drawer));
		//		p1.add(p2);
		//		panel.add(p1);
		//		 panel.add(new MCLabel(4, 40, "oh damit", 0xff0000, drawer));
		points.clear();
		Random ran = new Random();
		for (int i = 0; i < 10; i++) {
			points.add(new Point(ran.nextInt(300) - 100, ran.nextInt(200) - 50));
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		//		drawer.drawBackgroundTexture();
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		for (Point p : points) {
			drawer.drawBackgroundTexture(p.x - 5, p.y - 5, 28, 28);

			drawer.drawSlot(p.x, p.y);
		}
		if (mouseX % 2 == 2) {
			drawer.drawPlayerSlots(19, 99);
			drawer.drawSlots(64, 19, 3, 3);
			drawer.drawProgressArrow(148, 12, 0.85f, Direction.LEFT);
			drawer.drawSizedSlot(150, 35, 9);
			drawer.drawTextfield(t);
			drawer.drawFrame(122, 11, 9, 48, 1, ColorHelper.getRGB(EnumDyeColor.BLACK));
			drawer.drawEnergyBarV(123, 12, 46, 0.69f);
			// drawer.drawColoredRectangle(120, 61, 18, 36,
			// ColorHelper.getRGB(EnumDyeColor.BLUE));
			t.drawTextBox();
			fontRenderer.drawString("ZEuth", 3, 4, 0xff);
			drawer.drawStopSign(180, 12);
			drawer.drawFlame(18, 18, 1.0f);
			drawer.drawScrollbar(0, 0, 100, 1.00f, Direction.RIGHT);
			drawer.drawFluidRect(155, 25, 10, 30, new FluidStack(FluidRegistry.WATER, 323));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		NBTTagCompound l = new NBTTagCompound();
		l.setString("fine", "number3");
		l.setLong("fine2", "amerose".hashCode() + Long.MAX_VALUE);
		tile.sendMessage(l);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!checkHotbarKeys(keyCode)) {
			if (t.textboxKeyTyped(typedChar, keyCode)) {
				try {
					tile.k = Integer.valueOf(t.getText());
				} catch (Exception e) {
					tile.k = 999;
				}
				NBTTagCompound nbt = new NBTTagCompound();
				//				NBTHelper.setInt(nbt, "k", tile.k);
				tile.sendMessage(nbt);
			} else
				super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (s.isVisible() && s.isMouseOver(mouseX, mouseY)) {
			if (mc.player.inventory.getItemStack() != null)
				s.stack = mc.player.inventory.getItemStack();
			else
				s.stack = null;
		}
	}

}
