package mrriegel.limelib.tile;

import net.minecraft.entity.player.EntityPlayer;

public interface IOwneable {

	public String getOwner();

	public boolean canAccess(EntityPlayer player);

}
