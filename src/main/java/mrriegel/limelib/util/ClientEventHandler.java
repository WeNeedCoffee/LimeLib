package mrriegel.limelib.util;

import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Maps;

import mrriegel.limelib.LimeConfig;
import mrriegel.limelib.LimeLib;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.datapart.RenderRegistry;
import mrriegel.limelib.datapart.RenderRegistry.RenderDataPart;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.EnergyHelper;
import mrriegel.limelib.helper.EnergyHelper.Energy;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.tile.IHUDProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = LimeLib.MODID, value = { Side.CLIENT })
public class ClientEventHandler {

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(ParticleHelper.roundParticle);
		event.getMap().registerSprite(ParticleHelper.sparkleParticle);
		event.getMap().registerSprite(ParticleHelper.squareParticle);
	}

	private static Minecraft mc = null;

	private static Minecraft getMC() {
		if (mc == null)
			mc = Minecraft.getMinecraft();
		return mc;
	}

	//TODO remove this
	public static Map<BlockPos, Pair<Long, Long>> energyTiles = Maps.newHashMap();

	@SubscribeEvent
	public static void renderEnergy(Post event) {
		Minecraft mc = getMC();
		if (!LimeConfig.showEnergy || mc.world == null || mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || mc.objectMouseOver.getBlockPos() == null || mc.world.getTileEntity(mc.objectMouseOver.getBlockPos()) == null || energyTiles.isEmpty())
			return;
		BlockPos p = mc.objectMouseOver.getBlockPos();
		if (event.getType() == ElementType.TEXT && energyTiles.containsKey(p)) {
			Energy energyType = null;
			if ((energyType = EnergyHelper.isEnergyContainer(mc.world.getTileEntity(p), null)) == null) {
				energyTiles.remove(p);
				return;
			}
			ScaledResolution sr = event.getResolution();
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GuiDrawer drawer = new GuiDrawer(0, 0, 0, 0, 0);
			int color = 0;
			drawer.drawColoredRectangle(0, 0, 44, 44, color);
			long energy = energyTiles.get(p).getLeft(), max = energyTiles.get(p).getRight();
			String text = (!GuiScreen.isShiftKeyDown() ? Utils.formatNumber(energy) : energy) + "/" + (!GuiScreen.isShiftKeyDown() ? Utils.formatNumber(max) : max) + " " + energyType.unit;
			int lenght = 90/* mc.fontRenderer.getStringWidth(text) */;
			mc.fontRenderer.drawString(text, (sr.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2f, (sr.getScaledHeight() - 15 - mc.fontRenderer.FONT_HEIGHT) / 2f, GuiScreen.isShiftKeyDown() ? 0xffff00 : 0x80ffff00, true);
			if (LimeConfig.energyConfigHint) {
				boolean before = mc.fontRenderer.getUnicodeFlag();
				mc.fontRenderer.setUnicodeFlag(true);
				String config = LimeConfig.CONFIGHINT;
				mc.fontRenderer.drawString(config, (sr.getScaledWidth() - mc.fontRenderer.getStringWidth(config)) / 2f, (sr.getScaledHeight() + 40 - mc.fontRenderer.FONT_HEIGHT) / 2f, 0x40ffff00, true);
				mc.fontRenderer.setUnicodeFlag(before);
			}
			drawer.drawEnergyBarH((sr.getScaledWidth() - lenght) / 2, (sr.getScaledHeight() + 20 - 8) / 2, lenght, (float) ((double) energy / (double) max));
			drawer.drawFrame((sr.getScaledWidth() - lenght) / 2 - 1, (sr.getScaledHeight() + 20 - 8) / 2 - 1, lenght + 2, 9, 1, Color.BLACK.getRGB());
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public static void tick(ClientTickEvent event) {
		Minecraft mc = getMC();
		if (event.phase == Phase.END && mc.world != null && !mc.isGamePaused()) {
			DataPartRegistry reg = DataPartRegistry.get(mc.world);
			if (reg != null) {
				Iterator<DataPart> it = reg.getParts().stream().filter(p -> p != null && mc.world.isBlockLoaded(p.getPos())).collect(Collectors.toList()).iterator();
				while (it.hasNext()) {
					DataPart part = it.next();
					part.updateClient(mc.world);
					part.ticksExisted++;
				}
			}
			if (mc.player != null && mc.player.ticksExisted % 2 == 0)
				rayTrace = DataPart.rayTrace(mc.player);
		}
	}

	public static Map<BlockPos, List<String>> supplierTexts = Maps.newHashMap();

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		//ihudprovider
		RayTraceResult rtr = getMC().objectMouseOver;
		if (rtr != null && rtr.typeOfHit == Type.BLOCK && getMC().inGameHasFocus) {
			TileEntity t = getMC().world.getTileEntity(rtr.getBlockPos());
			IHUDProvider tile = IHUDProvider.isHUDProvider(t) ? IHUDProvider.getHUDProvider(t) : null;
			if (tile != null) {
				boolean sneak = getMC().player.isSneaking();
				EnumFacing face = rtr.sideHit.getOpposite();
				boolean playerhorizontal = false;
				if (face.getAxis() == Axis.Y || playerhorizontal)
					face = getMC().player.getHorizontalFacing();
				List<String> tmp = null;
				if (tile.readingSide().isServer()) {
					List<String> foo = supplierTexts.get(t.getPos());
					if (foo != null)
						tmp = foo;
					else
						tmp = tile.getData(sneak, face.getOpposite());
				} else
					tmp = tile.getData(sneak, face.getOpposite());
				if (tmp != null && !tmp.isEmpty()) {
					double x = t.getPos().getX() - TileEntityRendererDispatcher.staticPlayerX;
					double y = t.getPos().getY() - TileEntityRendererDispatcher.staticPlayerY;
					double z = t.getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ;
					GlStateManager.pushMatrix();
					double dx = face.getAxis() == Axis.Z ? 0.5F : Math.max(-0.001, face.getAxisDirection().getOffset() * -1.001);
					double dz = face.getAxis() == Axis.X ? 0.5F : Math.max(-0.001, face.getAxisDirection().getOffset() * -1.001);
					GlStateManager.translate((float) x + dx, (float) y + 1F, (float) z + dz);
					float f1 = face.getHorizontalIndex() * 90f;
					if (face.getAxis() == Axis.Z)
						f1 += 180f;
					GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
					GlStateManager.enableRescaleNormal();
					FontRenderer fontrenderer = getMC().fontRenderer;
					float f3 = 0.010416667F;
					//					GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
					GlStateManager.scale(f3, -f3, f3);
					GlStateManager.glNormal3f(0.0F, 0.0F, -f3);
					GlStateManager.depthMask(false);
					final int maxWordLength = 93;
					boolean cutLongLines = tile.lineBreak(sneak, face.getOpposite());
					final double factor = MathHelper.clamp(tile.scale(sneak, face.getOpposite()), .1, 2.);
					List<String> text = tmp.stream().filter(s -> s != null)//
							.flatMap(s -> (!cutLongLines ? Collections.singletonList(s) : fontrenderer.listFormattedStringToWidth(s, (int) (maxWordLength / factor))).stream()).collect(Collectors.toList());
					int lineHeight = fontrenderer.FONT_HEIGHT + 1;
					int oy = (int) -(lineHeight * text.size() * factor);
					int ysize = -oy;
					new GuiDrawer(0, 0, 0, 0, 0).drawColoredRectangle(-48, oy, 96, ysize, tile.getBackgroundColor(sneak, face.getOpposite()));
					GlStateManager.translate(0, -text.size() * lineHeight * factor, 0);
					GlStateManager.scale(factor, factor, factor);
					for (int j = 0; j < text.size(); ++j) {
						String s = text.get(j);
						boolean shadow = s.contains(IHUDProvider.SHADOWFONT);
						if (shadow)
							s = s.replace(IHUDProvider.SHADOWFONT, "");
						int width = fontrenderer.getStringWidth(s);
						boolean tooLong = !cutLongLines && width * factor > maxWordLength;
						double fac = maxWordLength / (width * factor);
						int xx = tile.center(sneak, face.getOpposite()) || tooLong ? -width / 2 : (int) (-46 / factor);
						if (tooLong)
							GlStateManager.scale(fac, 1, 1);
						fontrenderer.drawString(s, xx, j * 10 + 1, 0xFFFFFFFF, shadow);
						if (tooLong)
							GlStateManager.scale(1. / fac, 1, 1);
					}
					GlStateManager.scale(1. / factor, 1. / factor, 1. / factor);
					GlStateManager.depthMask(true);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();

				}
			}
		}
		//datapart
		DataPartRegistry reg = DataPartRegistry.get(getMC().world);
		if (reg != null) {
			reg.getParts().stream().filter(p -> p != null && getMC().player.getDistance(p.getX(), p.getY(), p.getZ()) < 64).forEach(p -> {
				@SuppressWarnings("rawtypes")
				RenderDataPart ren = RenderRegistry.map.get(p.getClass());
				if (ren != null)
					ren.render(p, p.getX() - TileEntityRendererDispatcher.staticPlayerX, p.getY() - TileEntityRendererDispatcher.staticPlayerY, p.getZ() - TileEntityRendererDispatcher.staticPlayerZ, event.getPartialTicks());
			});
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void itemToolTip(ItemTooltipEvent event) {
		Minecraft mc = getMC();
		if (LimeConfig.commandBlockCreativeTab && mc.currentScreen instanceof GuiContainerCreative && ((GuiContainerCreative) mc.currentScreen).getSelectedTabIndex() == CreativeTabs.REDSTONE.getTabIndex() && Block.getBlockFromItem(event.getItemStack().getItem()) instanceof BlockCommandBlock) {
			event.getToolTip().add(TextFormatting.YELLOW + LimeConfig.CONFIGHINT);
		}

	}

	public static DataPart rayTrace = null;

	@SubscribeEvent
	public static void draw(DrawBlockHighlightEvent event) {
		DataPart part = rayTrace;
		if (part != null && part.getHighlightBox() != null) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos blockpos = part.getPos();
			double d0 = TileEntityRendererDispatcher.staticPlayerX;
			double d1 = TileEntityRendererDispatcher.staticPlayerY;
			double d2 = TileEntityRendererDispatcher.staticPlayerZ;
			RenderGlobal.drawSelectionBoundingBox(part.getHighlightBox().offset(blockpos).grow(0.0020000000949949026D).offset(-d0, -d1, -d2), 0.0F, 0.0F, 0.0F, 0.4F);
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			event.setCanceled(true);
		}
	}

	static int width = 100, height = 30;
	static Vec3d a, b, c, d;

	//	@SubscribeEvent
	public static void test2(RenderWorldLastEvent event) {
		if (!RecipeHelper.dev)
			return;
		if (GuiScreen.isCtrlKeyDown() /*&& mc.player.motionX == 0 && mc.player.motionZ == 0*/) {
			float yaw = yawC != 0f ? yawC : (yawC = mc.player.rotationYaw);
			float pitch = pitchC != 0f ? pitchC : (pitchC = mc.player.rotationPitch);
			Vec3d vecPos = guiPos != null ? guiPos : (guiPos = mc.player.getLook(event.getPartialTicks()).add(mc.player.getPositionEyes(event.getPartialTicks())));
			double x = vecPos.x - TileEntityRendererDispatcher.staticPlayerX;
			double y = vecPos.y - TileEntityRendererDispatcher.staticPlayerY;
			double z = vecPos.z - TileEntityRendererDispatcher.staticPlayerZ;
			GlStateManager.pushMatrix();
			GlStateManager.depthMask(false);
			GlStateManager.translate(x, y, z);
			float scale = .005f;
			GlStateManager.scale(scale, scale, scale);
			boolean rot = true;
			if (rot) {
				GlStateManager.rotate(-MathHelper.wrapDegrees(yaw), 0, 1, 0);
				GlStateManager.rotate(pitch, 1, 0, 0);
				GlStateManager.rotate(180f, 0, 0, 1);
			}
			double halfWidth = width / 2d, halfHeight = height / 2d;
			GlStateManager.translate(-halfWidth, -halfHeight, 0);
			if (a == null) {
				//				Vec3d see = new Vec3d(-halfWidth * scale, halfHeight * scale, 0);
				//				Matrix4f m = new Matrix4f();
				//				m.m03 = (float) see.x;
				//				m.m13 = (float) see.y;
				//				m.m23 = (float) see.z;
				//				if (rot) {
				//					//					m = m.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1));
				//					m = m.rotate((float) Math.toRadians(-pitch), new Vector3f(1, 0, 0));
				//					m = m.rotate((float) Math.toRadians(-MathHelper.wrapDegrees(-yaw)), new Vector3f(0, 1, 0));
				//				}
				//				see = new Vec3d(m.m03, m.m13, m.m23);
				a = vecPos.add(vec(halfWidth * scale, halfHeight * scale, pitch, yaw));
				b = vecPos.add(vec(-halfWidth * scale, halfHeight * scale, pitch, yaw));
				c = vecPos.add(vec(-halfWidth * scale, -halfHeight * scale, pitch, yaw));
				d = vecPos.add(vec(halfWidth * scale, -halfHeight * scale, pitch, yaw));
			}
			//			new GuiDrawer(0, 0, 0, 0, 0).drawColoredRectangle(0, 0, 10, 10, Color.RED.getRGB());
			new GuiDrawer(0, 0, width, height, 0).drawBackgroundTexture();
			new GuiButton(0, 100, 100, 80, 20, "Button").drawButton(mc, 0, 0, event.getPartialTicks());
			//			new CommonGuiButton(0, 0, 0, 40, 10, "masu").drawButton(mc, 0, 0, event.getPartialTicks());
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
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

	private static Vec3d guiPos;
	private static float yawC, pitchC;

	//	@SubscribeEvent
	public static void test(InputEvent.KeyInputEvent event) {
		if (!Keyboard.getEventKeyState() && Keyboard.getEventKey() == 29) {
			guiPos = null;
			yawC = 0f;
			pitchC = 0f;
			a = b = c = d = null;
		}
	}

	//	@SubscribeEvent
	public static void test(InputEvent.MouseInputEvent event) {
		if (guiPos != null && Mouse.getEventButtonState() && Mouse.getEventButton() == 1 && a != null) {
			boolean res = false;
			Vec3d d2 = b.subtract(a);
			Vec3d d3 = d.subtract(a);
			Vec3d n = d2.crossProduct(d3);
			Vec3d dr = mc.player.getLook(0);
			double ndot = n.dotProduct(dr);
			if (Math.abs(ndot) >= 1e-6d) {
				double t = -n.dotProduct(mc.player.getPositionEyes(0).subtract(a)) / ndot;
				Vec3d m = mc.player.getPositionEyes(0).add(dr.scale(t));
				Vec3d dm = m.subtract(a);
				double u = dm.dotProduct(d2);
				double v = dm.dotProduct(d3);
				double maxU = d2.dotProduct(d2);
				double maxV = d3.dotProduct(d3);
				System.out.println("" + (width / maxU) * u);
				res = (u >= 0.0 && u <= maxU && v >= 0.0 && v <= maxV);
			}
			System.out.println(res + "");

		}
	}

	@SubscribeEvent
	public static void test(RenderWorldLastEvent event) {
		if (!RecipeHelper.dev)
			return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		Vec3d arr = new Vec3d(9 + .5, 6, 5 + .5);
		//		arr = new Vec3d(d3, d4, d5).add(player.getLookVec().normalize().scale(2.));
		//		arr = new Vec3d(arr.x, d4 + 1.5, arr.z);
		if (arr.distanceTo(player.getPositionVector()) > 32)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.disableTexture2D();
		//		GlStateManager.depthMask(false);
		//		GlStateManager.disableDepth();
		double d3 = TileEntityRendererDispatcher.staticPlayerX;
		double d4 = TileEntityRendererDispatcher.staticPlayerY;
		double d5 = TileEntityRendererDispatcher.staticPlayerZ;
		int color = 0;
		GlStateManager.translate(arr.x - d3, arr.y - d4, arr.z - d5);
		float angle = (System.currentTimeMillis() / 10) % 360;
		//		angle = -MathHelper.wrapDegrees(player.rotationYaw) - 90;
		Vector2d arrow = new Vector2d(arr.x, arr.z);
		Entity e = null;
		List<EntityLiving> lis = player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(arr.addVector(6, 6, 6), arr.addVector(-6, -6, -6)));
		if (!lis.isEmpty()) {
			e = lis.get(0);
			Vector2d play = new Vector2d(e.posX, e.posZ);

		}
		GlStateManager.rotate(angle, .0f, 1f, .0f);
		GlStateManager.translate(-.5, 0, -.5);
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder bb = tes.getBuffer();
		GL11.glLineWidth(5f / (float) (arr.distanceTo(new Vec3d(d3, d4, d5))));
		bb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(.5, -.5, .5).color(.9f, .4f, .7f, 1f).endVertex();
		bb.pos(.5, 1.5, .5).color(.9f, .4f, .7f, 1f).endVertex();
		tes.draw();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		Random ran = new Random(System.identityHashCode(player));
		Vector4f col = new Vector4f(.1f, .4f, .6f, 1.f);
		//IndentationError: unexpected indent

		//west
		float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.EAST);
		bb.pos(.0, .4, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .6, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .6, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .4, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();

		bb.pos(.6, .4, .9).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .9).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .1).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .1).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();

		//top                    col.   col.y  col.z  col.w
		//IndentationError: unexpected indent

		diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.UP);
		bb.pos(.0, .6, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .6, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		//bottom                 col.   col.y  col.z  col.w
		//IndentationError: unexpected indent

		diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.DOWN);
		bb.pos(.0, .4, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .4, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		//north                  col.   col.y  col.z  col.w
		//IndentationError: unexpected indent

		diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.NORTH);
		bb.pos(.0, .4, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .6, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .3).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		//north-east
		bb.pos(.6, .4, .1).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .1).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(1., .6, .5).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(1., .4, .5).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		//south                  col.   col.y  col.z  col.w
		//IndentationError: unexpected indent

		diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.SOUTH);
		bb.pos(.6, .4, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .6, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.0, .4, .7).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		//south-east
		bb.pos(1., .4, .5).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(1., .6, .5).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .9).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .9).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		tes.draw();

		bb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
		//top                    col.   col.y  col.z  col.w
		//IndentationError: unexpected indent

		diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.UP);
		bb.pos(.6, .6, .1).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .6, .9).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(1., .6, .5).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		//bottom                    col.   col.y  col.z  col.w
		//IndentationError: unexpected indent

		diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(EnumFacing.DOWN);
		bb.pos(1., .4, .5).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .9).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		bb.pos(.6, .4, .1).color(col.x * diffuse, col.y * diffuse, col.z * diffuse, col.w).endVertex();
		tes.draw();

		//		GlStateManager.enableDepth();
		//		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
