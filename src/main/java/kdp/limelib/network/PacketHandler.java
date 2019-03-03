package kdp.limelib.network;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.lang3.Validate;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.GameData;

public class PacketHandler {

	private static final String VERSION = "1.0";
	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(GameData.checkPrefix("ch1"), () -> VERSION,
			v -> VERSION.equals(v), v -> VERSION.equals(v));
	private static int index = 0;

	public static void init() {
	}

	public static void register(Class<? extends AbstractMessage> classMessage) {
		Validate.isTrue(
				Arrays.stream(classMessage.getConstructors())
						.anyMatch(c -> c.getParameterCount() == 0 && Modifier.isPublic(c.getModifiers())),
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

}
