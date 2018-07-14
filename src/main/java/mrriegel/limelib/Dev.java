package mrriegel.limelib;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.RegistryHelper;
import mrriegel.limelib.helper.TeleportationHelper;
import mrriegel.limelib.tile.IHUDProvider;
import mrriegel.limelib.util.LimeCapabilities;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

public class Dev {
	
	public static Block colorFluid;
	
	public static void preInit() {
		Connect.preInit();
		Fluid fluid = new Fluid("das", new ResourceLocation(LimeLib.MODID, "fluid/liquid"), new ResourceLocation(LimeLib.MODID, "fluid/liquid_flow"));
		FluidRegistry.registerFluid(fluid);
		FluidRegistry.addBucketForFluid(fluid);
		colorFluid = new BlockFluidClassic(fluid, Material.WATER);
		colorFluid.setRegistryName("colorfluid");
		colorFluid.setUnlocalizedName(colorFluid.getRegistryName().toString());
		RegistryHelper.register(colorFluid);
		ModelLoader.setCustomStateMapper(colorFluid, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
		GameRegistry.registerWorldGenerator((Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) -> {
			if(random.nextDouble()>.1)
				return;
			BlockPos p = new BlockPos(chunkX * 16, random.nextInt(20) + 54, chunkZ * 16);
			boolean gend = new WorldGenLakes(colorFluid).generate(world, random, p);
			if (gend != false)
				System.out.println(p);
		}, 30);
	}
	
	@SubscribeEvent
	public static void attachTile(AttachCapabilitiesEvent<TileEntity> event) {
		if (event.getObject() instanceof TileEntityFurnace)
			event.addCapability(new ResourceLocation(LimeLib.MODID, "dd"), new ICapabilityProvider() {
				TileEntityFurnace tile = (TileEntityFurnace) event.getObject();
				IHUDProvider pro = new IHUDProvider() {

					@Override
					public List<String> getData(boolean sneak, EnumFacing facing) {
						List<String> lis = Lists.newArrayList();
						lis.add(TextFormatting.RED + "" + TextFormatting.ITALIC + IHUDProvider.SHADOWFONT + "Burntime: " + tile.getField(0));
						ItemStack in = tile.getStackInSlot(0);
						lis.add("Input: " + (in.isEmpty() ? "" : (in.getDisplayName() + " " + in.getCount() + "x")));
						ItemStack out = tile.getStackInSlot(2);
						lis.add("Output: " + (out.isEmpty() ? "" : (out.getDisplayName() + " " + out.getCount() + "x")));
						ItemStack fu = tile.getStackInSlot(1);
						lis.add("Fuel: " + (fu.isEmpty() ? "" : (fu.getDisplayName() + " " + fu.getCount() + "x")));
						lis.add(IHUDProvider.SHADOWFONT + (sneak ? facing.toString().toUpperCase() : facing.toString().toLowerCase()));
						return lis;
					}

					@Override
					public Side readingSide() {
						return Side.SERVER;
					}

					@Override
					public double scale(boolean sneak, EnumFacing facing) {
						int ticks = FMLClientHandler.instance().getClientPlayerEntity().ticksExisted;
						double k = (Math.sin((ticks + FMLClientHandler.instance().getClient().getRenderPartialTicks()) / 10.) + 1) / 2 + .5;
						if (!"".isEmpty())
							return k;
						//						return (System.currentTimeMillis() / 350) % 2 == 0 ? .99 : .97;
						return 1;
					}

					@Override
					public boolean lineBreak(boolean sneak, EnumFacing facing) {
						if (!"".isEmpty()) {
							return (System.currentTimeMillis() / 200) % 2 == 0;
						}
						return !!!!false;
					}

					@Override
					public boolean center(boolean sneak, EnumFacing facing) {
						if (!"".isEmpty()) {
							return (System.currentTimeMillis() / 300) % 2 == 0;
						}
						return false;
					}

					@Override
					public boolean requireFocus() {
						return !true;
					}

					@Override
					public int getBackgroundColor(boolean sneak, EnumFacing facing) {
						return IHUDProvider.super.getBackgroundColor(sneak, facing);
						//						return ColorHelper.getRGB(ColorHelper.getRainbow(25), 0x84);
					}
				};

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capability == LimeCapabilities.hudproviderCapa;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					if (hasCapability(capability, facing))
						return (T) pro;
					return null;
				}

			});
	}
	

	@SubscribeEvent
	public static void test(RenderWorldLastEvent event) {
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

	@SubscribeEvent
	public static void blockColor(ColorHandlerEvent.Block event) {
		event.getBlockColors().registerBlockColorHandler((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) -> {
			if (!false)
				return 0x7f7f0f;
			return new Random(Minecraft.getMinecraft().player.ticksExisted).nextInt();
		}, colorFluid);
	}

	private static Object2IntOpenHashMap<Class<? extends Entity>> colorMap = new Object2IntOpenHashMap<>();

	@SubscribeEvent
	public static void overlay(RenderGameOverlayEvent event) throws Exception {
		if (event instanceof RenderGameOverlayEvent.Post && event.getType() == ElementType.TEXT) {
			int color = 0xff7fff00;
			Minecraft mc=Minecraft.getMinecraft();
			RayTraceResult rtr = mc.objectMouseOver;
			if (rtr == null || rtr.typeOfHit != Type.ENTITY || rtr.entityHit == null)
				return;
			Entity entity = rtr.entityHit;
			if (!(entity instanceof EntityLivingBase))
				return;
			Class<? extends Entity> ec = entity.getClass();
			if (colorMap.containsKey(ec)) {
				color = colorMap.getInt(ec);
			} else {
				Render<?> render = mc.getRenderManager().getEntityClassRenderObject(ec);
				ResourceLocation rl = (ResourceLocation) ReflectionHelper.findMethod(Render.class, "getEntityTexture", "func_110775_a", Entity.class).invoke(render, entity);
				BufferedImage bi = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream());
				int red = 0, green = 0, blue = 0, count = 0;
				for (int x = 0; x < bi.getWidth(); x++)
					for (int y = 0; y < bi.getHeight(); y++) {
						int rgb = bi.getRGB(x, y);
						if (ColorHelper.getAlpha(rgb) == 255) {
							red += ColorHelper.getRed(rgb);
							green += ColorHelper.getGreen(rgb);
							blue += ColorHelper.getBlue(rgb);
							count++;
						}
					}
				color = new Color((red / count), (green / count), (blue / count)).getRGB();
				colorMap.put(ec, color);
			}
			GuiUtils.drawGradientRect(0, 0, 0, 50, 50, color, color);
		}
	}
	

	@SubscribeEvent
	public static void test(LivingJumpEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if (!player.world.isRemote) {
			//			try {
			//				new CommandOp().execute(player.getServer(), player, new String[] { player.getName() });
			//			} catch (CommandException e) {
			//				e.printStackTrace();
			//			}
			System.out.println("sds");
			BlockPos pb = new BlockPos(player);
			TileEntity t = player.world.getTileEntity(pb.down());
			
			new Thread(() -> {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				player.world.getMinecraftServer().addScheduledTask(() -> {
					//					player.world.setBlockState(pb, Blocks.BEDROCK.getDefaultState());
					ItemStack e = ItemStack.EMPTY;
					List<Item> list = Lists.newArrayList(ForgeRegistries.ITEMS);
					while (e.isEmpty()) {
						e = new ItemStack(list.get(player.world.rand.nextInt(list.size())));
					}
					Block.spawnAsEntity(player.world, pb, e);
				});
			}).start();
			//			Stopwatch sw = Stopwatch.createStarted();
			//			System.out.println(sw.stop().elapsed(TimeUnit.MICROSECONDS) + " micros");
			List<EntitySheep> sheeps = player.world.getEntitiesWithinAABB(EntitySheep.class, new AxisAlignedBB(new BlockPos(player).add(-6, -6, -6), new BlockPos(player).add(6, 6, 6)));
			if (".".isEmpty())
				if (!sheeps.isEmpty() && false) {
					for (EntitySheep sheep : sheeps) {
						if (player.dimension == 0) {
							TeleportationHelper.teleport(sheep, new BlockPos(110, 30, -20), 1);
						} else {
							TeleportationHelper.teleport(sheep, new BlockPos(12, 5, -6), 0);
						}
					}
				} else {
					if (player.dimension == 0) {
						//					TeleportationHelper.teleport(player, new BlockPos(110, 30, -20), 1);
						TeleportationHelper.teleport(player, new BlockPos(110, 130, -20), 12);
					} else {
						TeleportationHelper.teleport(player, new BlockPos(12, 5, -6), 0);
					}
				}
		} else {
		}
	}

	@SubscribeEvent
	public static void test(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
		} else {
		}
	}

}
