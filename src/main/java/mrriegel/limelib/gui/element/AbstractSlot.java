package mrriegel.limelib.gui.element;

import java.util.List;

import mrriegel.limelib.gui.GuiDrawer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.config.GuiUtils;

import com.google.common.collect.Lists;

public abstract class AbstractSlot extends GuiElement implements ITooltip {
	public int amount;
	public boolean number, square, smallFont, toolTip, editable;

	public AbstractSlot(int id, int x, int y, int amount, GuiDrawer drawer, boolean number, boolean square, boolean smallFont, boolean toolTip) {
		super(id, x, y, 16, 16, drawer);
		this.amount = amount;
		this.number = number;
		this.square = square;
		this.smallFont = smallFont;
		this.toolTip = toolTip;
	}

	protected List<String> getTooltip(boolean shift) {
		return null;
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
				NBTTagCompound n = stack.hasTagCompound() ? stack.getTagCompound().copy() : null;
				List<String> tips = getTooltip(GuiScreen.isShiftKeyDown());
				if (tips != null)
					GuiUtils.drawHoveringText(tips, mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRendererObj);
				stack.setTagCompound(n);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		@Override
		protected List<String> getTooltip(boolean shift) {
			return shift ? Lists.newArrayList("Amount: " + amount) : GuiDrawer.getTooltip(stack);
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

		@Override
		public void onClick(int button) {
			if (editable)
				stack = mc.player.inventory.getItemStack();
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
				List<String> tips = getTooltip(GuiScreen.isShiftKeyDown());
				if (tips != null)
					GuiUtils.drawHoveringText(tips, mouseX, mouseY, sr.getScaledWidth(), sr.getScaledHeight(), -1, mc.fontRendererObj);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}

		@Override
		protected List<String> getTooltip(boolean shift) {
			return shift ? Lists.newArrayList("Amount: " + amount + " mB") : Lists.newArrayList(fluid.getLocalizedName(new FluidStack(fluid, 1)));
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

		@Override
		public void onClick(int button) {
			if (editable) {
				FluidStack s = FluidUtil.getFluidContained(mc.player.inventory.getItemStack());
				if (s != null && s.getFluid() != null)
					fluid = s.getFluid();
				else
					fluid = null;
			}
		}

	}

}
