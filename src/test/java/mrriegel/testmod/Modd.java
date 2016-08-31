package mrriegel.testmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "lalal", name = "kohle")
public class Modd {

	SimpleNetworkWrapper IN = new SimpleNetworkWrapper("lalalal");

	@Mod.Instance("lalal")
	public static Modd modd;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		IN.registerMessage(TestMessage.class, TestMessage.class, 0, Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@SubscribeEvent
	public void jump(LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			NBTTagCompound x = new NBTTagCompound();
			x.setString("l", "kkkk OMBERT");
			if (!e.getEntityLiving().worldObj.isRemote)
				IN.sendTo(new TestMessage(x), (EntityPlayerMP) e.getEntityLiving());
			if (e.getEntityLiving().worldObj.isRemote)
				IN.sendToServer(new TestMessage(x));
		}
	}

}
