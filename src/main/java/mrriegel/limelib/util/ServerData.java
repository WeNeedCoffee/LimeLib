package mrriegel.limelib.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class ServerData implements INBTSerializable<NBTTagCompound> {

	private static final Set<ServerData> datas = new HashSet<>();
	protected static MinecraftServer server;
	protected static File mainDir;

	public static void register(ServerData data) {
		datas.add(data);
	}

	{
		register(this);
	}

	public static void start(MinecraftServer server) throws IOException {
		ServerData.server = server;
		File dir = DimensionManager.getCurrentSaveRootDirectory();
		if (dir == null)
			dir = server.getActiveAnvilConverter().getSaveLoader(server.getFolderName(), false).getWorldDirectory();
		dir.mkdirs();
		mainDir = dir;
		for (ServerData data : datas) {
			data.read(mainDir);
		}
	}

	public static void stop() throws IOException {
		for (ServerData data : datas) {
			data.read(mainDir);
		}
	}

	protected abstract void read(File mainDir);

	protected abstract void write(File mainDir);

}
