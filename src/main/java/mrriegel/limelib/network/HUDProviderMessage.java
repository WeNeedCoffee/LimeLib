package mrriegel.limelib.network;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.tile.IHUDProvider;
import mrriegel.limelib.util.ClientEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class HUDProviderMessage extends AbstractMessage {

	private static final String SPLIT = "~#~²";

	public HUDProviderMessage() {
	}

	public HUDProviderMessage(EntityPlayerMP player) {
		List<BlockPos> lis1 = Lists.newArrayList();
		List<String> lis2 = Lists.newArrayList();
		BlockPos playerPos = new BlockPos(player);
		for (BlockPos p : BlockPos.getAllInBox(playerPos.add(-6, -6, -6), playerPos.add(6, 6, 6))) {
			TileEntity t = player.world.getTileEntity(p);
			if (IHUDProvider.isHUDProvider(t)) {
				IHUDProvider ds = IHUDProvider.getHUDProvider(t);
				if (ds.readingSide().isServer()) {
					lis1.add(p);
					lis2.add(Joiner.on(SPLIT).join(ds.getData(player.isSneaking(), player.getHorizontalFacing())));
				}
			}
		}
		if (lis1.isEmpty())
			shouldSend = false;
		NBTHelper.setList(nbt, "lis1", lis1);
		NBTHelper.setList(nbt, "lis2", lis2);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		List<BlockPos> lis1 = NBTHelper.getList(nbt, "lis1", BlockPos.class);
		List<String> lis2 = NBTHelper.getList(nbt, "lis2", String.class);
		ClientEventHandler.supplierTexts.clear();
		for (int i = 0; i < lis1.size(); i++) {
			BlockPos p = lis1.get(i);
			TileEntity t = player.world.getTileEntity(p);
			if (IHUDProvider.isHUDProvider(t)) {
				ClientEventHandler.supplierTexts.put(t.getPos(), Lists.newArrayList(lis2.get(i).split(SPLIT)));
			}
		}
	}

}
