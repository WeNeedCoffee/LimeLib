package mrriegel.testmod;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.List;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.book.GuiBook;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.helper.RenderHelper2;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.recipe.AbstractRecipe;
import mrriegel.limelib.tile.CommonTileInventory;
import mrriegel.limelib.util.FilterItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Mod(modid = "lalal", name = "kohle")
public class TestMod implements IGuiHandler {

	@Mod.Instance("lalal")
	public static TestMod mod;

	public static final CommonBlock block = new TestBlock();
	public static final CommonItem item = new TestItem();

	public TestBook book = new TestBook();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		block.registerBlock();
		block.initModel();
		item.registerItem();
		item.initModel();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, this);
		PacketHandler.registerMessage(TestMessage.class, Side.CLIENT);
		book.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	class R extends AbstractRecipe<PlayerMainInvWrapper> {

		public R(List<ItemStack> output, boolean order, Object... input) {
			super(output, order, input);
		}

		@Override
		protected List<ItemStack> getList(PlayerMainInvWrapper object) {
			List<ItemStack> hotbar = Lists.newArrayList();
			for (int i = 0; i < 9; i++)
				hotbar.add(object.getInventoryPlayer().getStackInSlot(i));
			hotbar.removeAll(Collections.singleton(null));
			return hotbar;
		}

		@Override
		public void removeIngredients(PlayerMainInvWrapper object) {
			for (Object o : getInput()) {
				FilterItem f = null;
				if (o instanceof Item)
					f = new FilterItem((Item) o);
				if (o instanceof Block)
					f = new FilterItem((Block) o);
				if (o instanceof String)
					f = new FilterItem((String) o);
				if (o instanceof ItemStack) {
					f = new FilterItem((ItemStack) o);
				}
				InvHelper.extractItem(object, f, 1, false);
			}
		}

		@Override
		public List<ItemStack> getResult(PlayerMainInvWrapper object) {
			return getOutput();
		}

	}

	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent event) {
		if (event instanceof Post && event.getType() == ElementType.TEXT) {
			GuiDrawer drawer = new GuiDrawer(0, 0, 0, 0, 0);
			//			if (StackHelper.getStackFromBlock(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().objectMouseOver.getBlockPos()) != null)
			//				drawer.renderToolTip(StackHelper.getStackFromBlock(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().objectMouseOver.getBlockPos()), 0, 0);
			// drawer.drawColoredRectangle(0, 0, 490, 420,
			// ColorHelper.getRGB(0xff0000, 100));

		}
	}

	@SubscribeEvent
	public void jump(LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			ItemStack held = player.getHeldItemMainhand();
			book.init();
			if (player.worldObj.isRemote && !player.isSneaking()) {
				if (held != null) {
					book.openGuiAt(held.getItem(), true);
				}
			}
			//			System.out.println(A+" "+B);

			PlayerMainInvWrapper pmiw = new PlayerMainInvWrapper(player.inventory);
			R r = new R(Lists.newArrayList(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.IRON_INGOT, 4)), true, Items.APPLE, Blocks.COAL_BLOCK, new ItemStack(Blocks.BOOKSHELF));
			if (r.match(pmiw)) {
				for (ItemStack s : r.getResult(pmiw))
					ItemHandlerHelper.insertItemStacked(pmiw, s.copy(), false);
				r.removeIngredients(pmiw);
			}

		}
	}

	// @SubscribeEvent
	public void r(RenderWorldLastEvent e) {
		if (Minecraft.getMinecraft().thePlayer.getHeldItemMainhand() == null)
			return;
		for (TileEntity t : Minecraft.getMinecraft().theWorld.loadedTileEntityList)
			RenderHelper2.renderBlockOverlays(e, Minecraft.getMinecraft().thePlayer, Sets.newHashSet(t.getPos()), ColorHelper.getRGB(Color.CYAN.getRGB(), 144), Color.orange.getRGB());
	}

	@SubscribeEvent
	public void update(final LivingUpdateEvent e) {
		if (!(e.getEntity() instanceof EntityPlayer) && e.getEntity().worldObj.isRemote && (e.getEntity().worldObj.getTotalWorldTime() + new BlockPos(e.getEntity()).hashCode()) % 10 == 0 && !GuiScreen.isCtrlKeyDown()) {

			// for (Vec3d v : ParticleHelper.getVecsForExplosion(0.1, 54,
			// Axis.Y))
			// ParticleHelper.renderParticle(new
			// CommonParticle(e.getEntity().posX, e.getEntity().posY +
			// e.getEntity().height - .1, e.getEntity().posZ, v.xCoord, 0.05,
			// v.zCoord).setMaxAge2(40 + new Random().nextInt(20)).setColor(new
			// Random().nextInt(0xffffff), 0));
			BlockPos pos = new BlockPos(e.getEntity());
			int range = 8;
			List<EntityLivingBase> lis = e.getEntity().worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)), new Predicate<EntityLivingBase>() {

				@Override
				public boolean apply(EntityLivingBase input) {
					return e.getEntity().getClass().equals(input.getClass());
				}
			});
			lis.remove(e.getEntity());
			for (EntityLivingBase ent : lis)
				for (Vec3d v : ParticleHelper.getVecsForLine(e.getEntity().posX, e.getEntity().posY + e.getEntity().height - .1, e.getEntity().posZ, ent.posX, ent.posY + ent.height - .1, ent.posZ, 3))
					ParticleHelper.renderParticle(new CommonParticle(v.xCoord, v.yCoord, v.zCoord));
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
			if (x == -1)
				return new GuiBook(book);
			else {
				return new GuiBook(book, x, y);
			}
		// return new GuiScreenBook(player, new ItemStack(Items.WRITTEN_BOOK),
		// false);
		return null;
	}

}
