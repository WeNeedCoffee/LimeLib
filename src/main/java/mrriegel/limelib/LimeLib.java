package mrriegel.limelib;

import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.plugin.TOP;
import mrriegel.limelib.util.ClientEventHandler;
import mrriegel.limelib.util.EventHandler;
import mrriegel.limelib.util.LimeCapabilities;
import mrriegel.limelib.util.Serious;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = LimeLib.MODID, name = LimeLib.NAME, version = LimeLib.VERSION)
public class LimeLib {

	@Instance(LimeLib.MODID)
	public static LimeLib INSTANCE;

	public static final String VERSION = "1.7.4";
	public static final String NAME = "LimeLib";
	public static final String MODID = "limelib";

	public static final Logger log = LogManager.getLogger(LimeLib.NAME);

	@SidedProxy(clientSide = "mrriegel.limelib.LimeClientProxy", serverSide = "mrriegel.limelib.LimeCommonProxy")
	public static LimeCommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.init(event.getSuggestedConfigurationFile());
		Utils.init();
		MinecraftForge.EVENT_BUS.register(EventHandler.class);
		if (event.getSide().isClient())
			MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		CapabilityDataPart.register();
		LimeCapabilities.register();
		Serious.preinit();
		wailaLoaded = Loader.isModLoaded("waila");
		jeiLoaded = Loader.isModLoaded("jei");
		teslaLoaded = Loader.isModLoaded("tesla");
		topLoaded = Loader.isModLoaded("theoneprobe");
		fluxLoaded = Loader.isModLoaded("redstoneflux");
	}

	public static boolean wailaLoaded, jeiLoaded, teslaLoaded, topLoaded, fluxLoaded;
	public static boolean wrenchAvailable;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		Serious.init();
		RecipeHelper.generateConstants();
		if (LimeLib.topLoaded)
			FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOP.class.getName());
		wrenchAvailable = StreamSupport.stream(ForgeRegistries.ITEMS.spliterator(), false).anyMatch(item -> StackHelper.isWrench(new ItemStack(item)));
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	static {
		//		MinecraftForge.EVENT_BUS.register(O.class);
	}

	static class O {
		@SubscribeEvent
		public static void spawn(EntityJoinWorldEvent event) {
			if (!event.getWorld().isRemote && event.getEntity() instanceof EntityItem) {
				System.out.println(event.getEntity());
				new Exception().printStackTrace();
				//				for(Entity e:event.getWorld().loadedEntityList){
				//					if(e instanceof EntityLiving)e.setDead();
				//				}
			}
		}

		@SubscribeEvent
		public static void click(RightClickBlock event) {
			if (!event.getWorld().isRemote) {
				NonNullList<ItemStack> l = NonNullList.create();
				event.getWorld().getBlockState(event.getPos()).getBlock().getDrops(l, event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), 0);
				System.out.println(l);
				System.out.println(event.getWorld().getBlockState(event.getPos()).getBlock().getDrops(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), 0));
			}
		}
	}

}
