package mrriegel.limelib;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Strings;

import mrriegel.limelib.gui.GuiDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class WorldGui {

	public int width = 250, height = 150;
	public final Vec3d guiPos, playerPos;
	public Vec3d a, b, c, d;
	public final float yaw, pitch;
	protected GuiDrawer drawer;

	int num = 0;

	public List<GuiButton> buttons = new ArrayList<>();

	private final Minecraft mc = Minecraft.getMinecraft();

	public WorldGui() {
		playerPos = mc.player.getPositionEyes(0);
		guiPos = mc.player.getLook(0).add(playerPos);
		yaw = mc.player.rotationYaw;
		pitch = mc.player.rotationPitch;
		width = 250;
		height = 150;
	}

	public void init() {
		buttons.clear();
		double halfWidth = width / 2d, halfHeight = height / 2d;
		a = guiPos.add(vec(halfWidth * scale, halfHeight * scale, pitch, yaw));
		b = guiPos.add(vec(-halfWidth * scale, halfHeight * scale, pitch, yaw));
		c = guiPos.add(vec(-halfWidth * scale, -halfHeight * scale, pitch, yaw));
		d = guiPos.add(vec(halfWidth * scale, -halfHeight * scale, pitch, yaw));
		drawer = new GuiDrawer(0, 0, width, height, 0);
		buttons.add(new GuiButtonExt(0, 13, 13, 30, 30, "+"));
		buttons.add(new GuiButtonExt(0, 53, 13, 30, 30, "-"));
	}

	public void draw(int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		mc.fontRenderer.drawString(num + "", 90, 27, Color.BLACK.getRGB());
		for (GuiButton b : buttons)
			b.drawButton(mc, mouseX, mouseY, 0);
		//		GlStateManager.disableDepth();
		GuiUtils.drawGradientRect(0, 15, 10, 160, 30, 0x44100010, 0x44100010);
		//		GlStateManager.enableDepth();
		//		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.disableLighting();
		RenderHelper.enableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Blocks.YELLOW_GLAZED_TERRACOTTA), 100, 15);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		for (GuiButton b : buttons)
			if (b.isMouseOver())
				GuiUtils.drawHoveringText(Arrays.asList("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.", Strings.repeat(b.displayString, 8), Strings.repeat(b.displayString, 18)), mouseX, mouseY, width, height, -1, mc.fontRenderer);
	}

	public void click(int mouse, int mouseX, int mouseY) {
		System.out.println("click " + mc.player.ticksExisted % 1000);
		for (GuiButton b : buttons)
			if (b.isMouseOver()) {
				if (b.displayString.equals("+"))
					num += mc.player.isSneaking() ? 10 : 1;
				else if (b.displayString.equals("-"))
					num -= mc.player.isSneaking() ? 10 : 1;
			}
	}

	private static WorldGui openGui;
	private static double scale = .0065, u, v, maxU, maxV;

	@SubscribeEvent
	public static void key(InputEvent.KeyInputEvent event) {
		if (!Keyboard.getEventKeyState() && Keyboard.getEventKey() == 29) {
			openGui = null;
		}
		if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == 29) {
			openGui = new WorldGui();
			openGui.init();
		}
	}

	@SubscribeEvent
	public static void tick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.phase == Phase.END && openGui != null && mc != null && mc.player != null) {
			Vec3d d2 = openGui.b.subtract(openGui.a);
			Vec3d d3 = openGui.d.subtract(openGui.a);
			Vec3d n = d2.crossProduct(d3);
			Vec3d dr = mc.player.getLook(0);
			double ndot = n.dotProduct(dr);
			if (Math.abs(ndot) >= 1e-6d) {
				double t = -n.dotProduct(mc.player.getPositionEyes(0).subtract(openGui.a)) / ndot;
				Vec3d m = mc.player.getPositionEyes(0).add(dr.scale(t));
				Vec3d dm = m.subtract(openGui.a);
				u = dm.dotProduct(d2);
				v = dm.dotProduct(d3);
				maxU = d2.dotProduct(d2);
				maxV = d3.dotProduct(d3);
			}
		}
	}

	@SubscribeEvent
	public static void click(InputEvent.MouseInputEvent event) {
		if (openGui != null) {
			int wheel = Mouse.getEventDWheel();
			if (wheel > 0) {
				scale += .00025;
				openGui.init();
				Minecraft.getMinecraft().player.inventory.changeCurrentItem(-wheel);
			} else if (wheel < 0) {
				scale -= .00025;
				openGui.init();
				Minecraft.getMinecraft().player.inventory.changeCurrentItem(-wheel);
			}
		}
		if (openGui != null && Mouse.getEventButtonState() && Mouse.getEventButton() == 1) {
			if ((u >= 0.0 && u <= maxU && v >= 0.0 && v <= maxV)) {
				Vec3d see = openGui.guiPos.subtract(openGui.playerPos).scale(.1);
				Vec3d seeN = see.scale(-1);
				Vec3d front = openGui.guiPos.add(seeN);
				Vec3d back = openGui.guiPos.add(see);
				Vec3d p = openGui.mc.player.getPositionEyes(0);
				if (p.distanceTo(front) < p.distanceTo(back))
					openGui.click(Mouse.getEventButton(), (int) ((openGui.width / maxU) * u), (int) ((openGui.height / maxV) * v));
			}
		}
	}

	private static Vec3d vec(double x, double y, double pitch, double yaw) {
		Vec3d v = new Vec3d(x, y, 0);
		Matrix4f m = new Matrix4f();
		m.m03 = (float) v.x;
		m.m13 = (float) v.y;
		m.m23 = (float) v.z;
		//		m = m.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1));
		m = m.rotate((float) Math.toRadians(-pitch), new Vector3f(1, 0, 0));
		m = m.rotate((float) Math.toRadians(-MathHelper.wrapDegrees(-yaw)), new Vector3f(0, 1, 0));
		return new Vec3d(m.m03, m.m13, m.m23);
	}

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		if (GuiScreen.isCtrlKeyDown() && openGui != null) {
			double x = openGui.guiPos.x - TileEntityRendererDispatcher.staticPlayerX;
			double y = openGui.guiPos.y - TileEntityRendererDispatcher.staticPlayerY;
			double z = openGui.guiPos.z - TileEntityRendererDispatcher.staticPlayerZ;
			GlStateManager.pushMatrix();
			GlStateManager.depthMask(false);
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(-MathHelper.wrapDegrees(openGui.yaw), 0, 1, 0);
			GlStateManager.rotate(openGui.pitch, 1, 0, 0);
			GlStateManager.rotate(180f, 0, 0, 1);
			double halfWidth = openGui.width / 2d, halfHeight = openGui.height / 2d;
			GlStateManager.translate(-halfWidth, -halfHeight, 0);
			GlStateManager.scale(1, 1, 0);
			openGui.draw((int) ((openGui.width / maxU) * u), (int) ((openGui.height / maxV) * v));
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void interact(PlayerInteractEvent event) {
		//		System.out.println(event.getClass().getSimpleName() + " " + event.getHand() + " " + (event.getWorld().isRemote ? "Client" : "Server"));
		if (event.isCancelable() && event.getWorld().isRemote && false) {
			event.setCanceled(true);
			event.setResult(Result.DENY);
			if (event instanceof LeftClickBlock) {
				((LeftClickBlock) event).setUseBlock(Result.DENY);
				((LeftClickBlock) event).setUseItem(Result.DENY);
			} else if (event instanceof RightClickBlock) {
				((RightClickBlock) event).setUseBlock(Result.DENY);
				((RightClickBlock) event).setUseItem(Result.DENY);
			}
		}
	}

}
