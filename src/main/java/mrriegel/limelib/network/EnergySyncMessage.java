package mrriegel.limelib.network;

import java.util.List;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.EnergyHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.helper.WorldHelper;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class EnergySyncMessage extends AbstractMessage<EnergySyncMessage> {

	public EnergySyncMessage() {
	}

	public EnergySyncMessage(EntityPlayerMP player) {
		List<BlockPos> lis1 = Lists.newArrayList();
		List<Long> lis2 = Lists.newArrayList(), lis3 = Lists.newArrayList();
		for (BlockPos p : WorldHelper.getCuboid(new BlockPos(player), 6)) {
			if (EnergyHelper.isEnergyContainer(player.world.getTileEntity(p), null) != null) {
				lis1.add(p);
				lis2.add(EnergyHelper.getEnergy(player.world.getTileEntity(p), null));
				lis3.add(EnergyHelper.getMaxEnergy(player.world.getTileEntity(p), null));
			}
		}
		if (lis1.isEmpty())
			sendMessage = false;
		NBTHelper.setLongList(nbt, "lis1", Utils.getLongList(lis1));
		NBTHelper.setLongList(nbt, "lis2", lis2);
		NBTHelper.setLongList(nbt, "lis3", lis3);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		List<BlockPos> lis1 = Utils.getBlockPosList(NBTHelper.getLongList(nbt, "lis1"));
		List<Long> lis2 = NBTHelper.getLongList(nbt, "lis2"), lis3 = NBTHelper.getLongList(nbt, "lis3");
		LimeLib.proxy.energyTiles().clear();
		try {
			for (int i = 0; i < lis1.size(); i++) {
				LimeLib.proxy.energyTiles().put(lis1.get(i), Pair.of(lis2.get(i), lis3.get(i)));
			}
		} catch (IndexOutOfBoundsException e) {
			LimeLib.proxy.energyTiles().clear();
		}
	}

}
