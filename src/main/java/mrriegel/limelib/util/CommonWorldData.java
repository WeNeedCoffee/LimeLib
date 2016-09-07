package mrriegel.limelib.util;

import javax.annotation.Nullable;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.WorldDataMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class CommonWorldData extends WorldSavedData {

	public CommonWorldData(String name) {
		super(name);
	}

	protected abstract boolean isGlobal();

	public void sync(@Nullable EntityPlayer player) {
		NBTTagCompound nbt = serializeNBT();
		NBTHelper.setString(nbt, "className", getClass().getName());
		NBTHelper.setString(nbt, "mapName", mapName);
		NBTHelper.setBoolean(nbt, "global", isGlobal());
		if (player instanceof EntityPlayerMP) {
			PacketHandler.sendTo(new WorldDataMessage(nbt), (EntityPlayerMP) player);
		} else
			for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
				PacketHandler.sendTo(new WorldDataMessage(nbt), (EntityPlayerMP) p);
			}
	}

}
