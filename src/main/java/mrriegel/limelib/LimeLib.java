package mrriegel.limelib;

import mrriegel.limelib.datapart.DataPartSavedData;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.ClientEventHandler;
import mrriegel.limelib.util.Eventhandler;
import mrriegel.limelib.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LimeLib.MODID, name = LimeLib.NAME, version = LimeLib.VERSION)
public class LimeLib {

	@Instance(LimeLib.MODID)
	public static LimeLib INSTANCE;

	public static final String VERSION = "1.3.2";
	public static final String NAME = "LimeLib";
	public static final String MODID = "limelib";

	public static final Logger log = LogManager.getLogger(NAME);

	@SidedProxy(clientSide = "mrriegel.limelib.ClientProxy", serverSide = "mrriegel.limelib.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Utils.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		MinecraftForge.EVENT_BUS.register(new Eventhandler());
		MinecraftForge.EVENT_BUS.register(DataPartSavedData.class);
		if (event.getSide().isClient())
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}

}
