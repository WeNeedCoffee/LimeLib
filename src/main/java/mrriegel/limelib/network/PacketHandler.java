package mrriegel.limelib.network;

import java.util.Map;

import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Maps;

public class PacketHandler {

	public static Map<String, SimpleNetworkWrapper> wrappers;
	public static Map<String, Integer> indices;

	public static void init() {
		wrappers = Maps.newHashMap();
		wrappers.put(Utils.getModID(), new SimpleNetworkWrapper(Utils.getModID()));
		indices = Maps.newHashMap();
		indices.put(Utils.getModID(), new Integer(0));
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends AbstractMessage> classMessage, Side side) {
		Class<? extends IMessageHandler<REQ, REPLY>> c1 = (Class<? extends IMessageHandler<REQ, REPLY>>) classMessage;
		Class<REQ> c2 = (Class<REQ>) classMessage;
		registerMessage(c1, c2, side);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		if (wrappers == null || indices == null)
			init();
		wrappers.get(Utils.getModID()).registerMessage(messageHandler, requestMessageType, indices.get(Utils.getModID()), side);
		indices.put(Utils.getModID(), indices.get(Utils.getModID()) + 1);
	}

	public static void sendToAll(IMessage message) {
		wrappers.get(Utils.getModID()).sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player) {
		wrappers.get(Utils.getModID()).sendTo(message, player);
	}

	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		wrappers.get(Utils.getModID()).sendToAllAround(message, point);
	}

	public static void sendToDimension(IMessage message, int dimensionId) {
		wrappers.get(Utils.getModID()).sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message) {
		wrappers.get(Utils.getModID()).sendToServer(message);
	}

}
