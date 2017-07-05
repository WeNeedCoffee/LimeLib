package mrriegel.limelib.network;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.EnergyHelper;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class EnergySyncMessage extends AbstractMessage {

	public EnergySyncMessage() {
	}

	public EnergySyncMessage(EntityPlayerMP player) {
		List<BlockPos> lis1 = Lists.newArrayList();
		List<Long> lis2 = Lists.newArrayList(), lis3 = Lists.newArrayList();
		BlockPos playerPos = new BlockPos(player);
		for (BlockPos p : BlockPos.getAllInBox(playerPos.add(-6, -6, -6), playerPos.add(6, 6, 6))) {
			if (EnergyHelper.isEnergyContainer(player.world.getTileEntity(p), null) != null) {
				lis1.add(p);
				lis2.add(EnergyHelper.getEnergy(player.world.getTileEntity(p), null));
				lis3.add(EnergyHelper.getMaxEnergy(player.world.getTileEntity(p), null));
			}
		}
		if (lis1.isEmpty())
			shouldSend = false;
		NBTHelper.setList(nbt, "lis1", lis1);
		NBTHelper.setList(nbt, "lis2", lis2);
		NBTHelper.setList(nbt, "lis3", lis3);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		List<BlockPos> lis1 = NBTHelper.getList(nbt, "lis1", BlockPos.class);
		List<Long> lis2 = NBTHelper.getList(nbt, "lis2", Long.class), lis3 = NBTHelper.getList(nbt, "lis3", Long.class);
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
