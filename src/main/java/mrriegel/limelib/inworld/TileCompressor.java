package mrriegel.limelib.inworld;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import mrriegel.limelib.tile.CommonTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

public class TileCompressor extends CommonTile implements ITickable {

	InnerWorld innerWorld;
	int boxSize = 4;
	BlockPos offset = new BlockPos(1, 0, 1);

	@Override
	public void update() {
		offset = new BlockPos(1, 0, 1);
		boxSize = 3;
		if (world.isRemote)
			return;
		if (innerWorld != null)
			innerWorld.updateEntities();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return side(facing).stream().map(p -> innerWorld.getTileEntity(p)).filter(Objects::nonNull).anyMatch(t -> t.hasCapability(capability, facing)) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		T cap = side(facing).stream().map(p -> innerWorld.getTileEntity(p)).filter(Objects::nonNull).map(t -> t.getCapability(capability, facing)).filter(Objects::nonNull).findFirst().orElse(null);
		if (cap != null)
			return cap;
		return super.getCapability(capability, facing);
	}

	@Override
	public void neighborChanged(IBlockState state, Block block, BlockPos fromPos) {
		if (innerWorld == null)
			createWorld();
		else
			deleteWorld();
	}

	public void createWorld() {
		System.out.println("creat");
		innerWorld = new InnerWorld(world, boxSize, this);
		for (BlockPos p : BlockPos.getAllInBox(0, 0, 0, boxSize - 1, boxSize - 1, boxSize - 1)) {
			innerWorld.setBlockState(p, world.getBlockState(toRealPos(p)));
			innerWorld.setTileEntity(p, world.getTileEntity(toRealPos(p)));
			world.removeTileEntity(p.add(pos.add(offset)));
			world.setBlockToAir(p.add(pos.add(offset)));
		}
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(offset), pos.add(offset).add(boxSize, boxSize, boxSize)), e -> !(e instanceof EntityPlayer));
		System.out.println(entities);
		System.out.println("ents real:" + entities.size());
		for (Entity e : entities) {
			Entity e2 = EntityList.createEntityFromNBT(e.serializeNBT(), innerWorld);
			//			e2.setUniqueId(MathHelper.getRandomUUID());
			world.removeEntity(e);
			e2.setWorld(innerWorld);
			Vec3d vec = toFakePos(e.getPositionVector());
			e2.setPosition(vec.x, vec.y, vec.z);
			innerWorld.spawnEntity(e2);
		}
		System.out.println(innerWorld.loadedEntityList);
	}

	public void deleteWorld() {
		System.out.println("delet");
		for (BlockPos p : BlockPos.getAllInBox(0, 0, 0, boxSize - 1, boxSize - 1, boxSize - 1)) {
			world.setBlockState(toRealPos(p), innerWorld.getBlockState(p));
			world.setTileEntity(toRealPos(p), innerWorld.getTileEntity(p));
		}
		System.out.println(innerWorld.loadedEntityList);
		System.out.println("ents fake:" + innerWorld.loadedEntityList.size());
		for (Entity e : innerWorld.loadedEntityList) {
			Entity e2 = EntityList.createEntityFromNBT(e.serializeNBT(), world);
			//			e2.setUniqueId(MathHelper.getRandomUUID());
			e2.setWorld(world);
			Vec3d vec = toRealPos(e.getPositionVector());
			e2.setPositionAndUpdate(vec.x, vec.y, vec.z);
			world.spawnEntity(e2);
		}
		innerWorld = null;
		//		world.spawnEntity(new EntityItem(world, pos.getX() + .5, pos.getY() + 1.3, pos.getZ() + .5, new ItemStack(Blocks.QUARTZ_BLOCK)));
	}

	private Collection<BlockPos> side(EnumFacing facing) {
		if (innerWorld == null)
			return Collections.emptyList();
		Set<BlockPos> big = Sets.newHashSet(BlockPos.getAllInBox(0, 0, 0, boxSize - 1, boxSize - 1, boxSize - 1));
		if (facing == null) {
			Iterable<BlockPos> small = BlockPos.getAllInBox(1, 1, 1, boxSize - 2, boxSize - 2, boxSize - 2);
			big.removeAll(Sets.newHashSet(small));
			return big;
		}
		switch (facing) {
		case DOWN:
			return big.stream().filter(p -> p.getY() == 0).collect(Collectors.toList());
		case EAST:
			return big.stream().filter(p -> p.getX() == boxSize - 1).collect(Collectors.toList());
		case NORTH:
			return big.stream().filter(p -> p.getZ() == 0).collect(Collectors.toList());
		case SOUTH:
			return big.stream().filter(p -> p.getZ() == boxSize - 1).collect(Collectors.toList());
		case UP:
			return big.stream().filter(p -> p.getY() == boxSize - 1).collect(Collectors.toList());
		case WEST:
			return big.stream().filter(p -> p.getX() == 0).collect(Collectors.toList());
		default:
			return Collections.emptyList();
		}
	}

	BlockPos toRealPos(BlockPos pos) {
		return pos.add(this.pos).add(offset);
	}

	BlockPos toFakePos(BlockPos pos) {
		return pos.subtract(this.pos).subtract(offset);
	}

	Vec3d toRealPos(Vec3d pos) {
		return pos.add(new Vec3d(this.pos)).add(new Vec3d(offset));
	}

	Vec3d toFakePos(Vec3d pos) {
		return pos.subtract(new Vec3d(this.pos)).subtract(new Vec3d(offset));
	}

}
