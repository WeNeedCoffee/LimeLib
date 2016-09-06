package mrriegel.testmod;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.helper.InvHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.RenderHelper2;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.tile.CommonTileInventory;
import mrriegel.limelib.util.AbstractRecipe;
import mrriegel.limelib.util.DataWrapper;
import mrriegel.limelib.util.FilterItem;
import mrriegel.limelib.util.ItemInvWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

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
	public void jump(LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			ItemStack held = player.getHeldItemMainhand();
			NBTTagCompound x = new NBTTagCompound();
			x.setString("l", WordUtils.capitalizeFully(RandomStringUtils.randomAlphabetic(new Random().nextInt(6) + 4)));
			if (!e.getEntityLiving().worldObj.isRemote) {
				PacketHandler.sendTo(new TestMessage(NBTHelper.getTag("oasis", 77L, .45122f, new ItemStack(Items.NETHER_STAR), "lach mal")), (EntityPlayerMP) player);
			}
			PlayerMainInvWrapper pmiw = new PlayerMainInvWrapper(player.inventory);
			R r = new R(Lists.newArrayList(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.IRON_INGOT, 4)), true, Items.APPLE, Blocks.COAL_BLOCK, new ItemStack(Blocks.BOOKSHELF));
			if (r.match(pmiw)) {
				for (ItemStack s : r.getResult(pmiw))
					ItemHandlerHelper.insertItemStacked(pmiw, s.copy(), false);
				r.removeIngredients(pmiw);
			}
			if (held != null && !player.worldObj.isRemote) {
				System.out.println(held.getTagCompound());
				ItemInvWrapper w = new ItemInvWrapper(held, 8);
				System.out.println(held.getTagCompound());
				ItemHandlerHelper.insertItemStacked(w, new ItemStack(Items.APPLE, 55), false);
				System.out.println(held.getTagCompound());
				InvHelper.extractItem(w, new FilterItem(Items.APPLE), 2, false);
				System.out.println(held.getTagCompound());
			}
			// if (e.getEntityLiving().worldObj.isRemote)
			// IN.sendToServer(new TestMessage(x));#
		}
	}

	@SubscribeEvent
	public void r(RenderWorldLastEvent e) {
		Color c = Color.cyan;
		if (Minecraft.getMinecraft().thePlayer.getHeldItemMainhand() == null)
			return;
		c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 144);
		for (TileEntity t : Minecraft.getMinecraft().theWorld.loadedTileEntityList)
			RenderHelper2.renderBlockOverlays(e, Minecraft.getMinecraft().thePlayer, Sets.newHashSet(t.getPos()), c, Color.orange);
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
