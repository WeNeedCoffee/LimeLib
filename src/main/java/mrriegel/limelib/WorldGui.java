package mrriegel.limelib;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import mrriegel.limelib.gui.GuiDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class WorldGui {

	public int width = 250, height = 150;
	public final Vec3d pos;
	public Vec3d a, b, c, d;
	public final float yaw, pitch;
	protected GuiDrawer drawer;

	int num = 0;

	public List<GuiButton> buttons = new ArrayList<>();

	private final Minecraft mc = Minecraft.getMinecraft();

	public WorldGui() {
		pos = mc.player.getLook(0).add(mc.player.getPositionEyes(0));
		yaw = mc.player.rotationYaw;
		pitch = mc.player.rotationPitch;
		width = 250;
		height = 150;
	}

	public void init() {
		double halfWidth = width / 2d, halfHeight = height / 2d;
		a = pos.add(vec(halfWidth * scale, halfHeight * scale, pitch, yaw));
		b = pos.add(vec(-halfWidth * scale, halfHeight * scale, pitch, yaw));
		c = pos.add(vec(-halfWidth * scale, -halfHeight * scale, pitch, yaw));
		d = pos.add(vec(halfWidth * scale, -halfHeight * scale, pitch, yaw));
		drawer = new GuiDrawer(0, 0, width, height, 0);
		buttons.add(new GuiButtonExt(0, 13, 13, 30, 30, "+"));
		buttons.add(new GuiButtonExt(0, 53, 13, 30, 30, "-"));
	}

	public void draw(int mouseX, int mouseY) {
		drawer.drawBackgroundTexture();
		for (GuiButton b : buttons)
			b.drawButton(mc, mouseX, mouseY, 0);
		mc.fontRenderer.drawString(num + "", 90, 27, Color.BLACK.getRGB());
	}

	public void click(int mouse, int mouseX, int mouseY) {
		for (GuiButton b : buttons)
			if (b.isMouseOver()) {
				if (b.displayString.equals("+"))
					num += mc.player.isSneaking() ? 10 : 1;
				else if (b.displayString.equals("-"))
					num -= mc.player.isSneaking() ? 10 : 1;
			}
	}

	private static WorldGui openGui;
	private static double scale = .005, u, v, maxU, maxV;

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
		if (openGui != null && Mouse.getEventButtonState() && Mouse.getEventButton() == 1) {
			if ((u >= 0.0 && u <= maxU && v >= 0.0 && v <= maxV))
				//TODO math.floor
				openGui.click(Mouse.getEventButton(), (int) ((openGui.width / maxU) * u), (int) ((openGui.height / maxV) * v));

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
			double x = openGui.pos.x - TileEntityRendererDispatcher.staticPlayerX;
			double y = openGui.pos.y - TileEntityRendererDispatcher.staticPlayerY;
			double z = openGui.pos.z - TileEntityRendererDispatcher.staticPlayerZ;
			GlStateManager.pushMatrix();
			GlStateManager.depthMask(false);
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(-MathHelper.wrapDegrees(openGui.yaw), 0, 1, 0);
			GlStateManager.rotate(openGui.pitch, 1, 0, 0);
			GlStateManager.rotate(180f, 0, 0, 1);
			double halfWidth = openGui.width / 2d, halfHeight = openGui.height / 2d;
			GlStateManager.translate(-halfWidth, -halfHeight, 0);
			openGui.draw((int) ((openGui.width / maxU) * u), (int) ((openGui.height / maxV) * v));
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
		}
	}

}
