package mrriegel.testmod;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.RenderHelper2;
import mrriegel.limelib.helper.TeleportationHelper;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.recipe.AbstractRecipe;
import mrriegel.limelib.tile.CommonTileInventory;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Mod(modid = "lalal", name = "kohle")
public class TestMod implements IGuiHandler {

	@Mod.Instance("lalal")
	public static TestMod mod;

	public static final CommonBlock block = new TestBlock();
	public static final CommonItem item = new TestItem();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println("zap");
		System.out.println(Loader.instance().activeModContainer().getName());
		OBJLoader.INSTANCE.addDomain("lalal");
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
			// drawer.drawColoredRectangle(0, 0, 490, 420,
			// ColorHelper.getRGB(0xff0000, 70));
			// Minecraft.getMinecraft().fontRendererObj.drawString("lalala", 13,
			// 13, ColorHelper.getRGB(EnumDyeColor.CYAN));

		}
	}

	@SubscribeEvent
	public void jump(LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			ItemStack held = player.getHeldItemMainhand();
			NBTTagCompound x = new NBTTagCompound();
			x.setString("l", WordUtils.capitalizeFully(RandomStringUtils.randomAlphabetic(new Random().nextInt(6) + 4)));
			if (!e.getEntityLiving().worldObj.isRemote) {
				PacketHandler.sendTo(new TestMessage(NBTHelper.getTag("oasis", 77L, .45122f, "lach mal")), (EntityPlayerMP) player);
			}
			PlayerMainInvWrapper pmiw = new PlayerMainInvWrapper(player.inventory);
			R r = new R(Lists.newArrayList(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.IRON_INGOT, 4)), true, Items.APPLE, Blocks.COAL_BLOCK, new ItemStack(Blocks.BOOKSHELF));
			if (r.match(pmiw)) {
				for (ItemStack s : r.getResult(pmiw))
					ItemHandlerHelper.insertItemStacked(pmiw, s.copy(), false);
				r.removeIngredients(pmiw);
			}
			RayTraceResult ray = Utils.rayTrace(player);
			// System.out.println(ray.typeOfHit);
			if (!player.worldObj.isRemote && false) {
				List<EntitySheep> lis = player.worldObj.getEntitiesWithinAABB(EntitySheep.class, new AxisAlignedBB(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5));
				if (held == null)
					for (EntitySheep sheep : lis) {
						if (sheep.worldObj.provider.getDimension() == 0)
							TeleportationHelper.teleportEntity(sheep, -1, new BlockPos(0, 129, 0));
						else
							TeleportationHelper.teleportEntity(sheep, 0, new BlockPos(65, 81, 268));
					}
				else {
					if (player.worldObj.provider.getDimension() == 0)
						TeleportationHelper.teleportEntity(player, -1, new BlockPos(0, 129, 0));
					else
						TeleportationHelper.teleportEntity(player, 0, new BlockPos(65, 81, 268));
				}
				// ItemEntityWrapper w = new
				// ItemEntityWrapper(e.getEntityLiving(), 4);
				// for (int i = 0; i < w.getSlots(); i++)
				// System.out.println(i + " " + w.getStackInSlot(i));
				// ItemHandlerHelper.insertItemStacked(w, new
				// ItemStack(Items.BAKED_POTATO), false);
				// System.out.println("ins");
				// for (int i = 0; i < w.getSlots(); i++)
				// System.out.println(i + " " + w.getStackInSlot(i));
				// ItemHandlerHelper.insertItemStacked(pmiw,
				// w.getStackInSlot(0), false);
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

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new TestContainer(player.inventory, (CommonTileInventory) world.getTileEntity(new BlockPos(x, y, z)));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new TestGui(new TestContainer(player.inventory, (CommonTileInventory) world.getTileEntity(new BlockPos(x, y, z))));
	}

}
