package mrriegel.limelib;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class LimeConfig {

	public static final String CONFIGHINT = "Can be disabled in LimeLib config.";
	public static Configuration config;

	public static boolean showEnergy, energyConfigHint, commandBlockCreativeTab;

	public static void init(File file) {
		config = new Configuration(file);
		config.load();

		//TODO remove
		showEnergy = config.getBoolean("showEnergy", Configuration.CATEGORY_CLIENT, false, "");
		energyConfigHint = config.getBoolean("energyConfigHint", Configuration.CATEGORY_CLIENT, true, "");
		commandBlockCreativeTab = config.getBoolean("commandBlockCreativeTab", Configuration.CATEGORY_CLIENT, true, "Command blocks are available in redstone creative tab.");

		if (config.hasChanged())
			config.save();
	}

}
