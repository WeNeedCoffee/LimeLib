package mrriegel.limelib;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import mrriegel.limelib.datapart.CapabilityDataPart;
import mrriegel.limelib.helper.RecipeHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.plugin.TOP;
import mrriegel.limelib.tile.IHUDProvider;
import mrriegel.limelib.util.LimeCapabilities;
import mrriegel.limelib.util.ServerData;
import mrriegel.limelib.util.Utils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.fml.client.FMLClientHandler;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

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
			Connect.preInit();
		}
//		int kk=4/0;
	}

	public static boolean wailaLoaded, jeiLoaded, teslaLoaded, topLoaded, fluxLoaded;
	//	public static boolean wrenchAvailable;

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
		//		wrenchAvailable = StreamSupport.stream(ForgeRegistries.ITEMS.spliterator(), false).anyMatch(item -> StackHelper.isWrench(new ItemStack(item)));
		if (RecipeHelper.dev) {
			//			UnderWorld.init();
			MinecraftForge.EVENT_BUS.register(INSTANCE);
			Connect.init();
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Mod.EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event) {
		try {
			ServerData.start(event.getServer());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		try {
			ServerData.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void attachTile(AttachCapabilitiesEvent<TileEntity> event) {
		if (event.getObject() instanceof TileEntityFurnace)
			event.addCapability(new ResourceLocation(MODID, "dd"), new ICapabilityProvider() {
				TileEntityFurnace tile = (TileEntityFurnace) event.getObject();
				IHUDProvider pro = new IHUDProvider() {

					@Override
					public List<String> getData(boolean sneak, EnumFacing facing) {
						List<String> lis = Lists.newArrayList();
						lis.add(TextFormatting.RED + "" + TextFormatting.ITALIC + IHUDProvider.SHADOWFONT + "Burntime: " + tile.getField(0));
						ItemStack in = tile.getStackInSlot(0);
						lis.add("Input: " + (in.isEmpty() ? "" : (in.getDisplayName() + " " + in.getCount() + "x")));
						ItemStack out = tile.getStackInSlot(2);
						lis.add("Output: " + (out.isEmpty() ? "" : (out.getDisplayName() + " " + out.getCount() + "x")));
						ItemStack fu = tile.getStackInSlot(1);
						lis.add("Fuel: " + (fu.isEmpty() ? "" : (fu.getDisplayName() + " " + fu.getCount() + "x")));
						lis.add(IHUDProvider.SHADOWFONT + (sneak ? facing.toString().toUpperCase() : facing.toString().toLowerCase()));
						return lis;
					}

					@Override
					public Side readingSide() {
						return Side.SERVER;
					}

					@Override
					public double scale(boolean sneak, EnumFacing facing) {
						int ticks = FMLClientHandler.instance().getClientPlayerEntity().ticksExisted;
						double k = (Math.sin((ticks + FMLClientHandler.instance().getClient().getRenderPartialTicks()) / 10.) + 1) / 2 + .5;
						if (!"".isEmpty())
							return k;
						//						return (System.currentTimeMillis() / 350) % 2 == 0 ? .99 : .97;
						return 1;
					}

					@Override
					public boolean lineBreak(boolean sneak, EnumFacing facing) {
						if (!"".isEmpty()) {
							return (System.currentTimeMillis() / 200) % 2 == 0;
						}
						return !!!!false;
					}

					@Override
					public boolean center(boolean sneak, EnumFacing facing) {
						if (!"".isEmpty()) {
							return (System.currentTimeMillis() / 300) % 2 == 0;
						}
						return false;
					}

					@Override
					public boolean requireFocus() {
						return !true;
					}

					@Override
					public int getBackgroundColor(boolean sneak, EnumFacing facing) {
						return IHUDProvider.super.getBackgroundColor(sneak, facing);
						//						return ColorHelper.getRGB(ColorHelper.getRainbow(25), 0x84);
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
