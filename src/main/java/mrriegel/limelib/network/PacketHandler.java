package mrriegel.limelib.network;

import mrriegel.limelib.LimeLib;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

	public static SimpleNetworkWrapper wrapper;
	public static int index = 0;
	private static boolean defaultsRegistered = false;

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
		registerMessage(WorldDataMessage.class, Side.CLIENT);
		registerMessage(TileSyncMessage.class, Side.SERVER);
		registerMessage(TileSyncMessage.class, Side.CLIENT);
		registerMessage(TeleportMessage.class, Side.CLIENT);
		registerMessage(DataPartSyncMessage.class, Side.CLIENT);

	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends AbstractMessage<?>> classMessage, Side side) {
		Class<? extends IMessageHandler<REQ, REPLY>> c1 = (Class<? extends IMessageHandler<REQ, REPLY>>) classMessage;
		Class<REQ> c2 = (Class<REQ>) classMessage;
		registerMessage(c1, c2, side);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		init();
		wrapper.registerMessage(messageHandler, requestMessageType, index++, side);

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
