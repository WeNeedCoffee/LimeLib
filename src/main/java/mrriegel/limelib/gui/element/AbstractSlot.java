package mrriegel.limelib.gui.element;

import java.util.List;

import mrriegel.limelib.gui.GuiDrawer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import com.google.common.collect.Lists;

public abstract class AbstractSlot extends GuiElement implements ITooltip {
	public int amount;
	public boolean number, square, smallFont, toolTip;

	public AbstractSlot(int id, int x, int y, int amount, GuiDrawer drawer, boolean number, boolean square, boolean smallFont, boolean toolTip) {
		super(id, x, y, 16, 16, drawer);
		this.amount = amount;
		this.number = number;
		this.square = square;
		this.smallFont = smallFont;
		this.toolTip = toolTip;
	}

	public void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			} else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		ScaledResolution sr = new ScaledResolution(mc);
		GuiUtils.drawHoveringText(list, x, y, sr.getScaledWidth(), sr.getScaledHeight(), -1, (font == null ? mc.fontRendererObj : font));
	}

	public static class ItemSlot extends AbstractSlot {

		public ItemStack stack;

		public ItemSlot(ItemStack stack, int id, int x, int y, int amount, GuiDrawer drawer, boolean number, boolean square, boolean smallFont, boolean toolTip) {
			super(id, x, y, amount, drawer, number, square, smallFont, toolTip);
			this.stack = stack;
		}

		@Override
		public void drawTooltip(int mouseX, int mouseY) {
			if (!visible)
				return;
			if (toolTip && stack != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				ScaledResolution sr = new ScaledResolution(mc);
				if (!GuiScreen.isShiftKeyDown())
					renderToolTip(stack, mouseX, mouseY);
				else
					GuiUtils.drawHoveringText(Lists.newArrayList("Amount: " + amount), mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRendererObj);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		@Override
		public void draw(int mouseX, int mouseY) {
			if (!visible)
				return;
			GlStateManager.pushMatrix();
			if (stack != null) {
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
				String num = amount < 1000 ? String.valueOf(amount) : amount < 1000000 ? amount / 1000 + "K" : amount / 1000000 + "M";
				if (number)
					if (smallFont) {
						GlStateManager.pushMatrix();
						GlStateManager.scale(.5f, .5f, .5f);
						mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x * 2 + 16, y * 2 + 16, num);
						GlStateManager.popMatrix();
					} else
						mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x, y, num);
			}
			if (square && isMouseOver(mouseX, mouseY)) {
				// GlStateManager.disableLighting();
				// GlStateManager.disableDepth();
				int j1 = x;
				int k1 = y;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				// GlStateManager.enableLighting();
				// GlStateManager.enableDepth();
			}
			GlStateManager.popMatrix();
		}

	}

	public static class FluidSlot extends AbstractSlot {
		Fluid fluid;

		public FluidSlot(Fluid fluid, int id, int x, int y, int amount, GuiDrawer drawer, boolean number, boolean square, boolean smallFont, boolean toolTip) {
			super(id, x, y, amount, drawer, number, square, smallFont, toolTip);
			this.fluid = fluid;
		}

		@Override
		public void drawTooltip(int mouseX, int mouseY) {
			if (!visible)
				return;
			if (toolTip && fluid != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				ScaledResolution sr = new ScaledResolution(mc);
				if (!GuiScreen.isShiftKeyDown())
					GuiUtils.drawHoveringText(Lists.newArrayList(fluid.getLocalizedName(new FluidStack(fluid, 1))), mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRendererObj);
				else
					GuiUtils.drawHoveringText(Lists.newArrayList("Amount: " + amount + " mB"), mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRendererObj);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		@Override
		public void draw(int mouseX, int mouseY) {
			if (!visible)
				return;
			if (fluid != null) {
				GlStateManager.pushMatrix();
				TextureAtlasSprite fluidIcon = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
				if (fluidIcon == null)
					return;
				int color = fluid.getColor(new FluidStack(fluid, 1));
				float a = ((color >> 24) & 0xFF) / 255.0F;
				float r = ((color >> 16) & 0xFF) / 255.0F;
				float g = ((color >> 8) & 0xFF) / 255.0F;
				float b = ((color >> 0) & 0xFF) / 255.0F;
				GlStateManager.color(r, g, b, a);
				this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				drawTexturedModalRect(x, y, fluidIcon, 16, 16);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();
				if (number) {
					String num = "" + (amount < 1000 ? amount : amount < 1000000 ? amount / 1000 : amount < 1000000000 ? amount / 1000000 : amount / 1000000000);
					num += amount < 1000 ? "mB" : amount < 1000000 ? "B" : amount < 1000000000 ? "KB" : "MB";
					if (smallFont) {
						GlStateManager.pushMatrix();
						GlStateManager.scale(.5f, .5f, .5f);
						mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, new ItemStack(Items.CHAINMAIL_BOOTS), x * 2 + 16, y * 2 + 16, num);
						GlStateManager.popMatrix();
					} else
						mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, new ItemStack(Items.CHAINMAIL_BOOTS), x, y, num);
				}
			}
			if (square && isMouseOver(mouseX, mouseY)) {
				// GlStateManager.disableLighting();
				// GlStateManager.disableDepth();
				int j1 = x;
				int k1 = y;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				// GlStateManager.enableLighting();
				// GlStateManager.enableDepth();
			}
		}

	}

}
