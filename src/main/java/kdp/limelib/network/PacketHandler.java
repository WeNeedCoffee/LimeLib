package kdp.limelib.network;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Predicate;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.GameData;

public class PacketHandler {

	private static final String VERSION = "1.0";
	private static SimpleChannel channel = NetworkRegistry.newSimpleChannel(GameData.checkPrefix("ch1"), () -> VERSION,
			v -> VERSION.equals(v), v -> VERSION.equals(v));
	private static int index = 0;

	public static void init() {
		register(OpenGuiMessage.class);
	}

	public static void register(Class<? extends AbstractMessage> classMessage) {
		Validate.isTrue(Arrays.stream(classMessage.getConstructors()).//
				anyMatch(c -> c.getParameterCount() == 0 && Modifier.isPublic(c.getModifiers())),
				classMessage + " needs a public default constructor.");
		channel.registerMessage(index++, (Class<AbstractMessage>) classMessage, (m, b) -> m.encode(b), b -> {
			try {
				AbstractMessage m = classMessage.newInstance();
				m.decode(b);
				return m;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}, (m, c) -> m.handleMessage(m, c.get()));
	}

	public static void sendToServer(AbstractMessage message) {
		channel.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static void sendToPlayers(AbstractMessage message, EntityPlayerMP... players) {
		for (EntityPlayerMP player : players) {
			channel.send(PacketDistributor.PLAYER.with(() -> player), message);
		}
	}

	public static void sendToPlayers(AbstractMessage message, Predicate<EntityPlayerMP> pred) {
		sendToPlayers(message, ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().stream()
				.filter(pred).toArray(EntityPlayerMP[]::new));
	}

	public static void sendToDimension(AbstractMessage message, DimensionType dimension) {
		channel.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
	}

	public static void sendToAll(AbstractMessage message) {
		channel.send(PacketDistributor.ALL.noArg(), message);
	}

}
