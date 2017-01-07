package mrriegel.testmod;

import java.util.Iterator;
import java.util.List;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.TaskEntity;
import mrriegel.limelib.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.collect.Lists;

public class TestEntity extends TaskEntity {

	public TestEntity(World worldIn) {
		super(worldIn);
	}

	@Override
	protected boolean canRun() {
		return true;
	}

	protected List<BlockPos> getList() {
		return Utils.getBlockPosList(NBTHelper.getLongList(nbt, "list"));
	}

	protected static List<BlockPos> getChunk(World world, BlockPos pos) {
		System.out.println("first");
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		List<BlockPos> lis = Lists.newLinkedList();
		for (int y = world.getActualHeight() - 1; y > 0; y--)
			for (int x = chunk.xPosition * 16; x < chunk.xPosition * 16 + 16; x++)
				for (int z = chunk.zPosition * 16; z < chunk.zPosition * 16 + 16; z++)
					if (BlockHelper.isBlockBreakable(world, new BlockPos(x, y, z)))
						lis.add(new BlockPos(x, y, z));
		return lis;
	}

	List<BlockPos> lis = null;

	@Override
	protected void run() {
		if (lis == null)
			lis = getChunk(worldObj, getPosition());
		Iterator<BlockPos> it = lis.listIterator();
		if (it.hasNext()) {
			BlockPos p = it.next();
			List<ItemStack> r = BlockHelper.breakBlockWithFortune(worldObj, p, 0, null, false, false);
			if (worldObj.rand.nextInt(20) == 1) {
				EntityPlayer xx = Utils.getRandomPlayer(worldObj);
				System.out.println(xx);
				for (ItemStack s : r) {
					//					StackHelper.spawnItemStack(worldObj, getPosition(), s);
					if (xx == null)
						ItemHandlerHelper.insertItem(new PlayerMainInvWrapper(xx.inventory), s, false);
				}
			}
			it.remove();
		}
		NBTHelper.setLongList(nbt, "list", Utils.getLongList(Lists.newArrayList(it)));
	}

	@Override
	protected boolean done() {
		return lis != null && lis.isEmpty();
	}

}
