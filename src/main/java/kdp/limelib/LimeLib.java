package kdp.limelib;

import kdp.limelib.helper.RecipeHelper;
import kdp.limelib.network.AbstractMessage;
import kdp.limelib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;

@Mod("limelib")
public class LimeLib {
	public LimeLib() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.addListener((LivingJumpEvent event) -> {
			if (event.getEntityLiving() instanceof EntityPlayer) {
				if (event.getEntityLiving().world.isRemote) {
					//PacketHandler.channel.sendToServer(new M());
				} else {
					MinecraftServer s;
					EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
					//PacketHandler.channel.send(PacketDistributor.PLAYER.with(()->player), new M());
					PacketHandler.channel.sendTo(new M(), player.connection.netManager,
							NetworkDirection.PLAY_TO_CLIENT);
				}
			}
		});
	}

	private void setup(final FMLCommonSetupEvent event) {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~+++");
		//RecipeHelper.addCraftingRecipe(new ItemStack(Items.APPLE, 3), null, true, "rtr"," r ",'r',Items.BAKED_POTATO,'t',Blocks.EMERALD_BLOCK);
		//RecipeHelper.addSmeltingRecipe(new ItemStack(Blocks.EMERALD_BLOCK), Items.EMERALD, 3., 35);
		RecipeHelper.generateFiles();

		PacketHandler.register(M.class);
	}

	public static class M extends AbstractMessage {

		@Override
		public void handleMessage(EntityPlayer player) {
			// TODO Auto-generated method stub
			System.out.println(player.getClass() + " " + Thread.currentThread());

		}

	}
}
