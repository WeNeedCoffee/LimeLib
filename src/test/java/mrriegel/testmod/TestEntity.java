package mrriegel.testmod;

import java.util.Iterator;
import java.util.List;

import mrriegel.limelib.helper.BlockHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.util.TaskEntity;
import mrriegel.limelib.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

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
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		List<BlockPos> lis = Lists.newLinkedList();
		for (int y = world.getActualHeight() - 1; y > 0; y--)
			for (int x = chunk.xPosition * 16; x < chunk.xPosition * 16 + 16; x++)
				for (int z = chunk.zPosition * 16; z < chunk.zPosition * 16 + 16; z++)
					if (BlockHelper.isBlockBreakable(world, new BlockPos(x, y, z)))
						lis.add(new BlockPos(x, y, z));
		return lis;
	}

	@Override
	protected void run() {
		Iterator<BlockPos> it = getList().listIterator();
		if (it.hasNext()) {
			BlockPos p = it.next();
			BlockHelper.breakBlockWithFortune(worldObj, p, 0, null, false, false);
			it.remove();
		}
	}

	@Override
	protected boolean done() {
		return getList().isEmpty();
	}

}
