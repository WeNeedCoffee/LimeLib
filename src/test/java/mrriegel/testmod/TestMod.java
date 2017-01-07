package mrriegel.testmod;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.helper.RenderHelper2;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.recipe.RecipeItemHandler;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Mod(modid = "lalal", name = "kohle", version = "${version}")
public class TestMod implements IGuiHandler {

	@Mod.Instance("lalal")
	public static TestMod mod;

	public static final CommonBlock block = new TestBlock();
	public static final CommonItem item = new TestItem();

	//	public TestBook book = new TestBook();

	public static final boolean ENABLE = false;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (!ENABLE)
			return;
		block.registerBlock();
		block.initModel();
		item.registerItem();
		item.initModel();
		EntityRegistry.registerModEntity(TestEntity.class, "test", 0, TestMod.mod, 80, 3, false);
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

	class Part extends DataPart {
		public int k;

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			k = compound.getInteger("k");
			super.readFromNBT(compound);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			compound.setInteger("k", k);
			return super.writeToNBT(compound);
		}

		@Override
		public void update(World world) {
			if (world.getTotalWorldTime() % 30 == 0)
				world.setBlockState(getPos().up(), Blocks.LAPIS_ORE.getDefaultState());
		}

		{
			k = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		}

	}

	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent event) {
		if (event instanceof Post && event.getType() == ElementType.TEXT) {
			GuiDrawer drawer = new GuiDrawer(0, 0, 0, 0, 0);
			drawer.hashCode();
			//			if (StackHelper.getStackFromBlock(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().objectMouseOver.getBlockPos()) != null)
			//				drawer.renderToolTip(StackHelper.getStackFromBlock(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().objectMouseOver.getBlockPos()), 0, 0);
			// drawer.drawColoredRectangle(0, 0, 490, 420,
			// ColorHelper.getRGB(0xff0000, 100));

		}
	}

	@SubscribeEvent
	public void right(RightClickBlock event) {
		if (!event.getWorld().isRemote && false) {
			BlockPos po = new BlockPos(0, 80, 0);
			if (DataPart.getDataPart(event.getWorld(), po) == null) {
				boolean added = DataPart.addDataPart(event.getWorld(), po, new Part(), false);
				System.out.println("add: " + added);
				System.out.println(DataPart.partMap);
			}
			Part part = (Part) DataPart.getDataPart(event.getWorld(), po);
			if (part != null) {
				//				part.k = 33;
				System.out.println(part.k + " time");
			}
			System.out.println(DataPart.partMap);
		}
		if (event.getWorld().getTileEntity(event.getPos()) instanceof TileEntityChest) {
			IItemHandler h = InvHelper.getItemHandler(event.getWorld().getTileEntity(event.getPos()), null);
			RecipeItemHandler rr = new RecipeItemHandler(Lists.newArrayList(new ItemStack(Items.BAKED_POTATO)), false, Items.POTATO, Items.COAL);
			if (rr.match(h)) {
				for (ItemStack x : rr.getOutput())
					event.getEntityPlayer().inventory.addItemStackToInventory(x);
				rr.removeIngredients(h);
			}
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
			//			if (!player.worldObj.isRemote && !player.isSneaking()) {
			//				if (held != null) {
			//					book.openGuiAt(held.getItem(), true);
			//				}
			//			}

			if (!e.getEntity().worldObj.isRemote && e.getEntityLiving() instanceof EntityPlayer) {
				TestEntity en = new TestEntity(e.getEntity().worldObj);
				en.setPosition(e.getEntity().posX, e.getEntity().posY, e.getEntity().posZ);
				en.worldObj.spawnEntityInWorld(en);
				System.out.println("spawned");
			}
			PlayerMainInvWrapper pmiw = new PlayerMainInvWrapper(player.inventory);
			R r = new R(Lists.newArrayList(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.IRON_INGOT, 4)), true, Items.APPLE, Blocks.COAL_BLOCK, new ItemStack(Blocks.BOOKSHELF));
			if (r.match(pmiw)) {
				for (ItemStack s : r.getResult(pmiw))
					ItemHandlerHelper.insertItemStacked(pmiw, s.copy(), false);
				r.removeIngredients(pmiw);
			}
			//			if (!player.worldObj.isRemote)
			//				PacketHandler.sendTo(new TestMessage(player.getEntityData()), (EntityPlayerMP) player);

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
			//			if (x == -1)
			//				return new GuiBook(book);
			//			else {
			//				return new GuiBook(book, x, y);
			//			}
			;
		return null;
	}

}
