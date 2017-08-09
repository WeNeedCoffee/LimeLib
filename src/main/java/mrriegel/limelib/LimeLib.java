package mrriegel.limelib;

import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.helper.StackHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.plugin.TOP;
import mrriegel.limelib.tile.IHUDProvider;
import mrriegel.limelib.util.ClientEventHandler;
import mrriegel.limelib.util.EventHandler;
import mrriegel.limelib.util.LimeCapabilities;
import mrriegel.limelib.util.Serious;
import mrriegel.limelib.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = LimeLib.MODID, name = LimeLib.NAME, version = LimeLib.VERSION)
public class LimeLib {

	@Instance(LimeLib.MODID)
	public static LimeLib INSTANCE;

	public static final String VERSION = "1.7.7";
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
		if (RecipeHelper.dev)
			MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@SubscribeEvent
	public void attachTile(AttachCapabilitiesEvent<TileEntity> event) {
		if (event.getObject() instanceof TileEntityFurnace)
			event.addCapability(new ResourceLocation("dd"), new ICapabilityProvider() {
				TileEntityFurnace tile = (TileEntityFurnace) event.getObject();
				IHUDProvider pro = new IHUDProvider() {

					@Override
					public boolean showData(boolean sneak, EnumFacing facing) {
						return true;
					}

					@Override
					public List<String> getData(boolean sneak, EnumFacing facing) {
						List<String> lis = Lists.newArrayList();
						lis.add(TextFormatting.DARK_RED + "Burntime: " + tile.getField(0));
						ItemStack in = tile.getStackInSlot(0);
						lis.add("Input: " + (in.isEmpty() ? "" : (in.getDisplayName() + " " + in.getCount() + "x")));
						ItemStack out = tile.getStackInSlot(2);
						lis.add("Output: " + (out.isEmpty() ? "" : (out.getDisplayName() + " " + out.getCount() + "x")));
						ItemStack fu = tile.getStackInSlot(1);
						lis.add("Fuel: " + (fu.isEmpty() ? "" : (fu.getDisplayName() + " " + fu.getCount() + "x")));
						//						if (sneak)
						//							lis.add(facing.toString().toUpperCase());
						return lis;
					}

					@Override
					public Side readingSide() {
						return Side.SERVER;
					}

					@Override
					public double scale(boolean sneak, EnumFacing facing) {
						return .7;
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

}
