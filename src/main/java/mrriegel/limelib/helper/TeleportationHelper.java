package mrriegel.limelib.helper;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.TeleportMessage;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TeleportationHelper {

	static Field invul = ReflectionHelper.findField(EntityPlayerMP.class, "invulnerableDimensionChange", "field_184851_cj");

	public static void prepareEntity(Entity entity) {
		if (entity instanceof EntityPlayerMP) {
			try {
				invul.set(entity, true);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean canTeleport(Entity entity) {
		return entity != null && !entity.world.isRemote && entity.isEntityAlive() && !entity.isBeingRidden() && !entity.isRiding() && (entity instanceof EntityLivingBase || entity instanceof EntityItem);
	}

	public static Optional<Entity> teleport(Entity entity, GlobalBlockPos pos) {
		return teleport(entity, pos.getPos(), pos.getDimension());
	}

	public static Optional<Entity> teleport(Entity entity, BlockPos pos) {
		return teleport(entity, pos, entity.world.provider.getDimension());
	}

	public static Optional<Entity> teleport(Entity entity, Vec3d vec) {
		return teleport(entity, vec, entity.world.provider.getDimension());
	}

	public static Optional<Entity> teleport(Entity entity, BlockPos pos, int targetDim) {
		return teleport(entity, new Vec3d(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5), targetDim);
	}

	public static Optional<Entity> teleport(Entity entity, Vec3d vec, int targetDim) {
		if (!canTeleport(entity))
			return Optional.empty();
		prepareEntity(entity);
		double x = vec.x, y = vec.y, z = vec.z;

		int from = entity.world.provider.getDimension();
		if (from != targetDim) {
			if (!DimensionManager.isDimensionRegistered(targetDim)) {
				LimeLib.log.error("serverTeleport: Dimension " + targetDim + " is not registered.");
				return Optional.empty();
			}
			MinecraftServer server = entity.world.getMinecraftServer();
			WorldServer fromDim = server.getWorld(from);
			WorldServer toDim = server.getWorld(targetDim);
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayerMP) entity).addExperienceLevel(0);
				Teleporter teleporter = new Tele(toDim, x, y, z, entity.rotationYaw, entity.rotationPitch);
				if (from == 1)
					entity.world.removeEntity(entity);
				print("1t", toDim);
				server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, targetDim, teleporter);
				//				teleport(entity, vec);
				if (from == 1 && entity.isEntityAlive()) {
					toDim.spawnEntity(entity);
					toDim.updateEntityWithOptionalForce(entity, false);
					entity.setPositionAndUpdate(x, y, z);
				}
				print("2t", toDim);
				PacketHandler.sendTo(new TeleportMessage(), (EntityPlayerMP) entity);
				Entity e = entity;
				if ("".isEmpty())
					toDim.getMinecraftServer().futureTaskQueue.add(new FutureTask<>(Executors.callable(() -> prepareEntity(e))));
			} else {
				NBTTagCompound tagCompound = entity.serializeNBT();
				float rotationYaw = entity.rotationYaw;
				float rotationPitch = entity.rotationPitch;
				fromDim.removeEntity(entity);

				try {
					Entity newEntity = EntityList.createEntityFromNBT(tagCompound, toDim);
					newEntity.setLocationAndAngles(x, y, z, rotationYaw, rotationPitch);
					newEntity.forceSpawn = true;
					toDim.spawnEntity(newEntity);
					newEntity.forceSpawn = false;
					entity = newEntity;
				} catch (Exception e) {
					LimeLib.log.error("serverTeleport: Error creating a entity to be created in new dimension.");
					return Optional.empty();
				}
			}
			print("3t", toDim);
		} else {
			entity.setPositionAndUpdate(x, y, z);
		}
		return Optional.of(entity);
	}

	public static final void print(String pre, World world) {
		//		System.out.println(pre + " World " + world.provider.getDimension() + " has players: " + world.loadedEntityList.stream().anyMatch(e -> e instanceof EntityPlayer));
	}

	public static class Tele extends Teleporter {
		double x, y, z;
		float yaw, pitch;

		public Tele(WorldServer worldIn, double x, double y, double z, float yaw, float pitch) {
			super(worldIn);
			this.x = x;
			this.y = y;
			this.z = z;
			this.yaw = yaw;
			this.pitch = pitch;
		}

		@Override
		public boolean makePortal(Entity p_makePortal_1_) {
			return true;
		}

		@Override
		public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
			return true;
		}

		@Override
		public void placeInPortal(Entity entity, float rotationYaw) {
			if (!entity.world.isBlockLoaded(new BlockPos(x, y, z)))
				entity.world.getBlockState(new BlockPos(x, y, z));
			teleport(entity, new Vec3d(x, y, z));
			entity.rotationYaw = yaw;
			entity.rotationPitch = pitch;
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;

		}

		@Override
		public void removeStalePortalLocations(long p_removeStalePortalLocations_1_) {
		}

	}
}
