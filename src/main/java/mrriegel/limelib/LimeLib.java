package mrriegel.limelib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.plugin.TOP;
import mrriegel.limelib.util.ClientEventHandler;
import mrriegel.limelib.util.EventHandler;
import mrriegel.limelib.util.Serious;
import mrriegel.limelib.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LimeLib.MODID, name = LimeLib.NAME, version = LimeLib.VERSION)
public class LimeLib {

	@Instance(LimeLib.MODID)
	public static LimeLib INSTANCE;

	public static final String VERSION = "1.7.0";
	public static final String NAME = "LimeLib";
	public static final String MODID = "limelib";

	public static final Logger log = LogManager.getLogger(LimeLib.NAME);

	@SidedProxy(clientSide = "mrriegel.limelib.LimeClientProxy", serverSide = "mrriegel.limelib.LimeCommonProxy")
	public static LimeCommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.init(event.getSuggestedConfigurationFile());
		Utils.init();
		CapabilityDataPart.register();
		wailaLoaded = Loader.isModLoaded("waila");
		jeiLoaded = Loader.isModLoaded("jei");
		teslaLoaded = Loader.isModLoaded("tesla");
		topLoaded = Loader.isModLoaded("theoneprobe");
	}

	public static boolean wailaLoaded, jeiLoaded, teslaLoaded, topLoaded;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		MinecraftForge.EVENT_BUS.register(EventHandler.class);
		if (event.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		}
		Serious.init();
		if (LimeLib.topLoaded)
			FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOP.class.getName());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

}
