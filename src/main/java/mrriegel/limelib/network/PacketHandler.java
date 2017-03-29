package mrriegel.limelib.network;

import java.util.Map;
import java.util.Set;

import mrriegel.limelib.LimeLib;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PacketHandler {

	public static SimpleNetworkWrapper wrapper;
	private static int index = 0;
	private static boolean defaultsRegistered = false;
	private static Map<Side, Set<Class<? extends AbstractMessage<?>>>> registered = Maps.newHashMap();

	private PacketHandler() {
	}

	public static void init() {
		if (wrapper == null)
			wrapper = new SimpleNetworkWrapper(LimeLib.NAME);
		registerDefaults();
	}

	private static void registerDefaults() {
		if (defaultsRegistered)
			return;
		defaultsRegistered = true;
		registerMessage(TileMessage.class, Side.SERVER);
		registerMessage(TileGuiMessage.class, Side.SERVER);
		registerMessage(TileSyncMessage.class);
		registerMessage(TeleportMessage.class, Side.CLIENT);
		registerMessage(EnergySyncMessage.class, Side.CLIENT);
		registerMessage(DataPartSyncMessage.class, Side.CLIENT);
		registerMessage(OpenGuiMessage.class, Side.SERVER);
		registerMessage(PlayerClickMessage.class, Side.SERVER);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends AbstractMessage<?>> classMessage, Side side) {
		Class<? extends IMessageHandler<REQ, REPLY>> c1 = (Class<? extends IMessageHandler<REQ, REPLY>>) classMessage;
		Class<REQ> c2 = (Class<REQ>) classMessage;
		registerMessage(c1, c2, side);
		if (registered.get(side) == null)
			registered.put(side, Sets.newHashSet());
		registered.get(side).add(classMessage);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends AbstractMessage<?>> classMessage) {
		registerMessage(classMessage, Side.CLIENT);
		registerMessage(classMessage, Side.SERVER);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		init();
		wrapper.registerMessage(messageHandler, requestMessageType, index++, side);
	}

	public static boolean isRegistered(Class<? extends AbstractMessage<?>> classMessage, Side side) {
		return registered.get(side).contains(classMessage);
	}

	public static void sendToAll(IMessage message) {
		init();
		wrapper.sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player) {
		init();
		wrapper.sendTo(message, player);
	}

	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		init();
		wrapper.sendToAllAround(message, point);
	}

	public static void sendToDimension(IMessage message, int dimensionId) {
		init();
		wrapper.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message) {
		init();
		wrapper.sendToServer(message);
	}

}
