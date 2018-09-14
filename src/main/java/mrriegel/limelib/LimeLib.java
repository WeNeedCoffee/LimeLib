package mrriegel.limelib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.plugin.TOP;
import mrriegel.limelib.util.LimeCapabilities;
import mrriegel.limelib.util.ServerData;
import mrriegel.limelib.util.Utils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = LimeLib.MODID, name = LimeLib.NAME, version = LimeLib.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class LimeLib {

	@Instance(LimeLib.MODID)
	public static LimeLib INSTANCE;

	public static final String VERSION = "1.7.12";
	public static final String NAME = "LimeLib";
	public static final String MODID = "limelib";

	public static final Logger log = LogManager.getLogger(LimeLib.NAME);

	@SidedProxy(clientSide = "mrriegel.limelib.LimeClientProxy", serverSide = "mrriegel.limelib.LimeCommonProxy")
	public static LimeCommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LimeConfig.init(event.getSuggestedConfigurationFile());
		Utils.init();
		//TODO move to limecaps
		CapabilityDataPart.register();
		LimeCapabilities.register();
		wailaLoaded = Loader.isModLoaded("waila");
		jeiLoaded = Loader.isModLoaded("jei");
		teslaLoaded = Loader.isModLoaded("tesla");
		topLoaded = Loader.isModLoaded("theoneprobe");
		fluxLoaded = Loader.isModLoaded("redstoneflux");
		if (RecipeHelper.dev) {
			Dev.preInit();
			MinecraftForge.EVENT_BUS.register(Dev.class);
			System.out.println("zip");
		}
	}

	public static boolean wailaLoaded, jeiLoaded, teslaLoaded, topLoaded, fluxLoaded;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		RecipeHelper.generateConstants();
		if (LimeConfig.commandBlockCreativeTab) {
			Blocks.COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
			Blocks.CHAIN_COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
			Blocks.REPEATING_COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
		}
		if (LimeLib.topLoaded)
			FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOP.class.getName());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (RecipeHelper.dev) {
		}
	}

	@Mod.EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event) {
		ServerData.start(event.getServer());
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		ServerData.stop();
	}

	static {
		if (RecipeHelper.dev)
			FluidRegistry.enableUniversalBucket();
		
	}

}
