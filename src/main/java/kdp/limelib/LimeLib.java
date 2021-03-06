package kdp.limelib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kdp.limelib.helper.RecipeHelper;
import kdp.limelib.helper.nbt.NBTBuilder;
import kdp.limelib.network.AbstractMessage;
import kdp.limelib.network.PacketHandler;
import kdp.limelib.util.EventHandler;

@Mod("limelib")
public class LimeLib {

	public static final Logger LOG = LogManager.getLogger(LimeLib.class);

	public LimeLib() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(EventHandler.class);
		MinecraftForge.EVENT_BUS.addListener((LivingJumpEvent event) -> {
			if (event.getEntityLiving() instanceof EntityPlayer) {
				if (event.getEntityLiving().world.isRemote) {
					PacketHandler.sendToServer(new M());
				} else {
					EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
					PacketHandler.sendToPlayers(new M(), player);
				}
			}
		});
	}

	private void setup(final FMLCommonSetupEvent event) {
		WorldAddition.register();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~+++");
		//RecipeHelper.addCraftingRecipe(new ItemStack(Items.APPLE, 3), null, true, "rtr"," r ",'r',Items.BAKED_POTATO,'t',Blocks.EMERALD_BLOCK);
		//RecipeHelper.addSmeltingRecipe(new ItemStack(Blocks.EMERALD_BLOCK), Items.EMERALD, 3., 35);
		RecipeHelper.generateFiles();

		PacketHandler.register(M.class);
		PacketHandler.init();
		//throw new RuntimeException(LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT).getClass().toString());
	}

	public static class M extends AbstractMessage {

		public M() {
			NBTBuilder.of(nbt).set("eins", RandomStringUtils.randomNumeric(5))
					.set("zwei", new BlockPos(3, 88, -1000000));
		}

		@Override
		public void handleMessage(EntityPlayer player) {
			System.out.println(player);
			System.out.println(player.getClass() + " " + Thread.currentThread());
			System.out.println(EffectiveSide.get() + ": " + nbt);

		}

	}
}
