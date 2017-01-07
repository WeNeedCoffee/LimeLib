package mrriegel.limelib;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

	private static Configuration config;

	public static boolean showEnergy;

	public static void init(File file) {
		config = new Configuration(file);
		config.load();

		showEnergy = config.getBoolean("showEnergy", Configuration.CATEGORY_CLIENT, true, "");

		if (config.hasChanged())
			config.save();
	}

}
