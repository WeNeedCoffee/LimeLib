package mrriegel.limelib.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import mrriegel.limelib.LimeLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

	public static SimpleNetworkWrapper wrapper;
	private static int index = 0;
	private static boolean defaultsRegistered = false;
	private static Map<Side, Set<Class<? extends AbstractMessage>>> registered = Maps.newHashMap();
	private static final boolean usePacket = false;
	private static Method m;

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
		registerMessage(EnergySyncMessage.class, Side.CLIENT);
		registerMessage(DataPartSyncMessage.class, Side.CLIENT);
		registerMessage(OpenGuiMessage.class, Side.SERVER);
		registerMessage(PlayerClickMessage.class, Side.SERVER);
		registerMessage(HUDProviderMessage.class, Side.CLIENT);
		registerMessage(RenderUpdateMessage.class, Side.CLIENT);
		registerMessage(TeleportMessage.class, Side.CLIENT);
		registerMessage(SplitMessage.class);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends AbstractMessage> classMessage, Side side) {
		Class<? extends IMessageHandler<REQ, REPLY>> c1 = (Class<? extends IMessageHandler<REQ, REPLY>>) classMessage;
		Class<REQ> c2 = (Class<REQ>) classMessage;
		if (usePacket)
			registerPacket(classMessage, side);
		else
			registerMessage(c1, c2, side);
		if (registered.get(side) == null)
			registered.put(side, Sets.newHashSet());
		registered.get(side).add(classMessage);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends AbstractMessage> classMessage) {
		registerMessage(classMessage, Side.CLIENT);
		registerMessage(classMessage, Side.SERVER);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
		init();
		if (!Stream.of(requestMessageType.getConstructors()).anyMatch((c) -> c.getParameterCount() == 0))
			throw new IllegalStateException(requestMessageType + " needs a public default constructor.");
		wrapper.registerMessage(messageHandler, requestMessageType, index++, side);
	}

	public static void registerPacket(Class<? extends Packet<INetHandler>> clazz, Side side) {
		init();
		if (m == null)
			m = ReflectionHelper.findMethod(EnumConnectionState.class, "registerPacket", "func_179245_a", EnumPacketDirection.class, Class.class);
		if (!Stream.of(clazz.getConstructors()).anyMatch((c) -> c.getParameterCount() == 0))
			throw new IllegalStateException(clazz + " needs a public default constructor.");
		try {
			m.invoke(EnumConnectionState.PLAY, side.isClient() ? EnumPacketDirection.CLIENTBOUND : EnumPacketDirection.SERVERBOUND, clazz);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static boolean isRegistered(Class<? extends AbstractMessage> classMessage, Side side) {
		return registered.get(side).contains(classMessage);
	}

	//TODO change parameter to abstractmessage
	public static void sendToAll(IMessage message) {
		init();
		if (!(message instanceof AbstractMessage) || ((AbstractMessage) message).shouldSend)
			if (usePacket) {
				if (message instanceof AbstractMessage)
					for (EntityPlayerMP player : players(Predicates.alwaysTrue()))
						player.connection.sendPacket((Packet<?>) message);
			} else
				wrapper.sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player) {
		init();
		if (!(message instanceof AbstractMessage) || ((AbstractMessage) message).shouldSend)
			if (usePacket) {
				if (message instanceof AbstractMessage)
					player.connection.sendPacket((Packet<?>) message);
			} else
				wrapper.sendTo(message, player);
	}

	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		init();
		if (!(message instanceof AbstractMessage) || ((AbstractMessage) message).shouldSend)
			if (usePacket) {
				if (message instanceof AbstractMessage)
					for (EntityPlayerMP player : players(p -> p.dimension == point.dimension && new Vec3d(point.x, point.y, point.z).distanceTo(new Vec3d(p.posX, p.posY, p.posZ)) <= point.range))
						player.connection.sendPacket((Packet<?>) message);
			} else
				wrapper.sendToAllAround(message, point);
	}

	public static void sendToDimension(IMessage message, int dimensionId) {
		init();
		if (!(message instanceof AbstractMessage) || ((AbstractMessage) message).shouldSend)
			if (usePacket) {
				if (message instanceof AbstractMessage)
					for (EntityPlayerMP player : players(p -> p.dimension == dimensionId))
						player.connection.sendPacket((Packet<?>) message);
			} else
				wrapper.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message) {
		init();
		if (!(message instanceof AbstractMessage) || ((AbstractMessage) message).shouldSend)
			if (usePacket) {
				if (message instanceof AbstractMessage)
					FMLClientHandler.instance().getClientPlayerEntity().connection.sendPacket((Packet<?>) message);
			} else
				wrapper.sendToServer(message);
	}

	private static Iterable<EntityPlayerMP> players(Predicate<EntityPlayer> pred) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
	}

}
