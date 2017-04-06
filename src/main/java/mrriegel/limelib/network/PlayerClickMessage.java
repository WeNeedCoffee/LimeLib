package mrriegel.limelib.network;

import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerClickMessage extends AbstractMessage<PlayerClickMessage> {

	public PlayerClickMessage() {
		super();
	}

	public PlayerClickMessage(BlockPos pos, EnumHand hand, boolean left) {
		NBTHelper.setLong(nbt, "pos", pos.toLong());
		NBTHelper.setBoolean(nbt, "left", left);
		NBTHelper.setBoolean(nbt, "mainhand", hand == EnumHand.MAIN_HAND);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		DataPartRegistry reg = DataPartRegistry.get(player.world);
		DataPart part = reg.getDataPart(BlockPos.fromLong(NBTHelper.getLong(nbt, "pos")));
		EnumHand hand = NBTHelper.getBoolean(nbt, "mainhand") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		if (part != null) {
			if (NBTHelper.getBoolean(nbt, "left"))
				part.onLeftClicked(player, hand);
			else
				part.onRightClicked(player, hand);
		}
	}

}
