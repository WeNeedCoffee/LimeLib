package mrriegel.limelib.network;

import mrriegel.testmod.TestMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public abstract class PacketHandler {

	public static SimpleNetworkWrapper wrapper;

	public static void register(String name) {
		int index = 0;
		wrapper = new SimpleNetworkWrapper(name);
		wrapper.registerMessage(TestMessage.class, TestMessage.class, index++, Side.CLIENT);
	}

	public static void sendToAll(IMessage message) {
		wrapper.sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player) {
		wrapper.sendTo(message, player);
	}

	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		wrapper.sendToAllAround(message, point);
	}

	public static void sendToDimension(IMessage message, int dimensionId) {
		wrapper.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message) {
		wrapper.sendToServer(message);
	}

}
