package mrriegel.limelib.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

public abstract class ServerData {

	private static final Set<ServerData> datas = new HashSet<>();
	protected static MinecraftServer server;
	protected static File mainDir;

	public static void register(ServerData data) {
		datas.add(data);
	}

	{
		register(this);
	}

	public static void start(MinecraftServer server) {
		ServerData.server = server;
		File dir = DimensionManager.getCurrentSaveRootDirectory();
		if (dir == null)
			dir = server.getActiveAnvilConverter().getSaveLoader(server.getFolderName(), false).getWorldDirectory();
		dir.mkdirs();
		mainDir = dir;
		for (ServerData data : datas) {
			data.read();
		}
	}

	public static void stop() {
		for (ServerData data : datas) {
			data.write();
		}
	}

	protected abstract void read();

	protected abstract void write();

}
