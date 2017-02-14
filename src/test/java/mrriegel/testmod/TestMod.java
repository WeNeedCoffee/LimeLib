package mrriegel.testmod;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.helper.RenderHelper2;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.recipe.RecipeItemHandler;
import mrriegel.limelib.recipe.ShapedRecipeExt;
import mrriegel.limelib.recipe.ShapelessRecipeExt;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

//@Mod(modid = "lalal", name = "kohle", version = "${version}")
public class TestMod implements IGuiHandler {

	@Mod.Instance("lalal")
	public static TestMod mod;

	public static final CommonBlock block = new TestBlock();
	public static final CommonItem item = new TestItem();
	public static Fluid alcohol;
	public static Block alcoholBlock;

//	public TestBook book = new TestBook();

	public static final boolean ENABLE = false;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (!ENABLE)
			return;
		block.registerBlock();
		block.initModel();
		item.registerItem();
		item.initModel();

		alcohol = new Fluid("alcohol", new ResourceLocation("lalal", "fluid/alcohol_still"), new ResourceLocation("lalal", "fluid/alcohol_flowing"));
		FluidRegistry.registerFluid(alcohol);
		FluidRegistry.addBucketForFluid(alcohol);
		alcoholBlock = new BlockFluidClassic(alcohol, Material.WATER);
		alcoholBlock.setRegistryName("alcohol");
		alcoholBlock.setUnlocalizedName(alcoholBlock.getRegistryName().toString());
		if (LimeLib.proxy.getSide().isClient())
			ModelLoader.setCustomStateMapper(alcoholBlock, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
		//		GameRegistry.register(alcoholBlock);
		Part part = new Part();
		part.setRegistryName("party");
		GameRegistry.register(part);
		GameRegistry.addRecipe(new ShapedRecipeExt(new ItemStack(Blocks.BEDROCK), " r ", " b ", " s ", 's', Blocks.STONE, 'r', Blocks.RED_FLOWER, 'b', Lists.newArrayList(Items.GUNPOWDER, Items.APPLE, Blocks.LEVER, new ItemStack(Items.BAKED_POTATO))));
		GameRegistry.addRecipe(new ShapelessRecipeExt(new ItemStack(Blocks.OBSIDIAN), Items.BLAZE_POWDER, Lists.newArrayList(Blocks.HOPPER, Items.LAVA_BUCKET)));
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (!ENABLE)
			return;
		MinecraftForge.EVENT_BUS.register(this);
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, this);
		PacketHandler.registerMessage(TestMessage.class, Side.CLIENT);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	class R extends RecipeItemHandler {

		public R(List<ItemStack> output, boolean order, Object... input) {
			super(output, order, input);
		}

		@Override
		protected List<ItemStack> getIngredients(IItemHandler object) {
			List<ItemStack> hotbar = Lists.newArrayList();
			for (int i = 0; i < 9; i++)
				hotbar.add(((PlayerMainInvWrapper) object).getInventoryPlayer().getStackInSlot(i));
			hotbar.removeAll(Collections.singleton(null));
			return hotbar;
		}

	}

	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent event) {
		if (event instanceof Post && event.getType() == ElementType.TEXT) {
			GuiDrawer drawer = new GuiDrawer(0, 0, 0, 0, 0);
			drawer.hashCode();
			//			if (StackHelper.getStackFromBlock(Minecraft.getMinecraft().world, Minecraft.getMinecraft().objectMouseOver.getBlockPos()) != null)
			//				drawer.renderToolTip(StackHelper.getStackFromBlock(Minecraft.getMinecraft().world, Minecraft.getMinecraft().objectMouseOver.getBlockPos()), 0, 0);
			// drawer.drawColoredRectangle(0, 0, 490, 420,
			// ColorHelper.getRGB(0xff0000, 100));

		}
	}

	@SubscribeEvent
	public void jump(LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			ItemStack held = player.getHeldItemMainhand();
			if (held != null)
				held.canEditBlocks();
			//			book.init();
			//			if (!player.world.isRemote && !player.isSneaking()) {
			//				if (!held.isEmpty()) {
			//					book.openGuiAt(held.getItem(), true);
			//				}
			//			}
			if (!player.world.isRemote) {
				PlayerMainInvWrapper pmiw = new PlayerMainInvWrapper(player.inventory);
				R r = new R(Lists.newArrayList(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.IRON_INGOT, 4)), true, Items.APPLE, Blocks.COAL_BLOCK, new ItemStack(Blocks.BOOKSHELF));
				if (r.match(pmiw) && r.removeIngredients(pmiw, false)) {
					for (ItemStack s : r.getResult(pmiw))
						ItemHandlerHelper.insertItemStacked(pmiw, s.copy(), false);

				}
				//			if (!player.world.isRemote)
				//				PacketHandler.sendTo(new TestMessage(player.getEntityData()), (EntityPlayerMP) player);

			}
		}
	}

	// @SubscribeEvent
	public void r(RenderWorldLastEvent e) {
		if (Minecraft.getMinecraft().player.getHeldItemMainhand() == null)
			return;
		for (TileEntity t : Minecraft.getMinecraft().world.loadedTileEntityList)
			RenderHelper2.renderBlockOverlays(e, Minecraft.getMinecraft().player, Sets.newHashSet(t.getPos()), ColorHelper.getRGB(Color.CYAN.getRGB(), 144), Color.orange.getRGB());
	}

	@SubscribeEvent
	public void update(final LivingUpdateEvent e) {
		if (!(e.getEntity() instanceof EntityPlayer) && e.getEntity().world.isRemote && (e.getEntity().world.getTotalWorldTime() + new BlockPos(e.getEntity()).hashCode()) % 10 == 0 && !GuiScreen.isCtrlKeyDown()) {

			// for (Vec3d v : ParticleHelper.getVecsForExplosion(0.1, 54,
			// Axis.Y))
			// ParticleHelper.renderParticle(new
			// CommonParticle(e.getEntity().posX, e.getEntity().posY +
			// e.getEntity().height - .1, e.getEntity().posZ, v.xCoord, 0.05,
			// v.zCoord).setMaxAge2(40 + new Random().nextInt(20)).setColor(new
			// Random().nextInt(0xffffff), 0));
			BlockPos pos = new BlockPos(e.getEntity());
			int range = 8;
			List<EntityLivingBase> lis = e.getEntity().world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)), new Predicate<EntityLivingBase>() {

				@Override
				public boolean apply(EntityLivingBase input) {
					return e.getEntity().getClass().equals(input.getClass());
				}
			});
			lis.remove(e.getEntity());
			for (EntityLivingBase ent : lis)
				for (Vec3d v : ParticleHelper.getVecsForLine(e.getEntity().posX, e.getEntity().posY + e.getEntity().height - .1, e.getEntity().posZ, ent.posX, ent.posY + ent.height - .1, ent.posZ, 3))
					LimeLib.proxy.renderParticle(new CommonParticle(v.xCoord, v.yCoord, v.zCoord).setMaxAge2(10).setTexture(ParticleHelper.sparkleParticle));
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0)
			return new TestContainer(player.inventory, (CommonTileInventory) world.getTileEntity(new BlockPos(x, y, z)));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0)
			return new TestGui(new TestContainer(player.inventory, (CommonTileInventory) world.getTileEntity(new BlockPos(x, y, z))));
		else if (ID == 1)
			//			if (x == -1)
			//				return new GuiBook(book);
			//			else {
			//				return new GuiBook(book, x, y);
			//			}
			;
		return null;
	}

	public static class Part extends Impl<Part> {
		public static IForgeRegistry<Part> PARTREGISTRY = new RegistryBuilder<Part>().setName(new ResourceLocation("lalal" + ":partss")).setType(Part.class).create();

	}
}
