package mrriegel.limelib.tile;

import java.util.List;

import javax.annotation.Nullable;

import mrriegel.limelib.util.LimeCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

public interface IHUDProvider {

	public static final String SHADOWFONT = "?~~%z";

	@Nullable
	List<String> getData(boolean sneak, EnumFacing facing);

	default int getBackgroundColor(boolean sneak, EnumFacing facing) {
		return 0x44FFFFFF;
	}

	default Side readingSide() {
		return Side.CLIENT;
	}

	default boolean center(boolean sneak, EnumFacing facing) {
		return true;
	}

	default double scale(boolean sneak, EnumFacing facing) {
		return .8;
	}

	default boolean lineBreak(boolean sneak, EnumFacing facing) {
		return true;
	}

	static boolean isHUDProvider(TileEntity t) {
		if (t != null)
			return t.hasCapability(LimeCapabilities.hudproviderCapa, null) || t instanceof IHUDProvider;
		return false;
	}

	static IHUDProvider getHUDProvider(TileEntity t) {
		IHUDProvider dk = null;
		if (t != null)
			dk = t.getCapability(LimeCapabilities.hudproviderCapa, null);
		if (dk == null)
			dk = (IHUDProvider) t;
		return dk;
	}

}
