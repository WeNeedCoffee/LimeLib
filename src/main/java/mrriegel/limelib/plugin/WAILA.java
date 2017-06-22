package mrriegel.limelib.plugin;

import org.apache.commons.lang3.tuple.Pair;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import mrriegel.limelib.tile.IInfoProvider;
import net.minecraft.tileentity.TileEntity;

@WailaPlugin
public class WAILA implements IWailaPlugin {

	@Override
	public void register(IWailaRegistrar registrar) {
		for (Pair<IInfoProvider<?>, Class<? extends TileEntity>> e : IInfoProvider.getProviders()) {
			registrar.registerBodyProvider(e.getKey(), e.getValue());
			registrar.registerHeadProvider(e.getKey(), e.getValue());
			registrar.registerTailProvider(e.getKey(), e.getValue());
			registrar.registerNBTProvider(e.getKey(), e.getValue());
			registrar.registerStackProvider(e.getKey(), e.getValue());
		}
	}

}
