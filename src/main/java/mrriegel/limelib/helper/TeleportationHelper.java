package mrriegel.limelib.helper;

import java.lang.reflect.Field;

import mrriegel.limelib.LimeLib;
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

	public static boolean teleport(Entity entity, GlobalBlockPos pos) {
		return teleport(entity, pos.getPos(), pos.getDimension());
	}

	public static boolean teleport(Entity entity, BlockPos pos) {
		return teleport(entity, pos, entity.world.provider.getDimension());
	}

	public static boolean teleport(Entity entity, Vec3d vec) {
		return teleport(entity, vec, entity.world.provider.getDimension());
	}

	public static boolean teleport(Entity entity, BlockPos pos, int targetDim) {
		return teleport(entity, new Vec3d(pos.getX() + .5, pos.getY(), pos.getZ() + .5), targetDim);
	}

	public static boolean teleport(Entity entity, Vec3d vec, int targetDim) {
		if (!canTeleport(entity))
			return false;
		prepareEntity(entity);
		double x = vec.x, y = vec.y, z = vec.z;

		int from = entity.world.provider.getDimension();
		if (from != targetDim) {
			if (!DimensionManager.isDimensionRegistered(targetDim)) {
				LimeLib.log.error("serverTeleport: Dimension " + targetDim + " is not registered.");
				return false;
			}
			MinecraftServer server = entity.world.getMinecraftServer();
			WorldServer fromDim = server.getWorld(from);
			WorldServer toDim = server.getWorld(targetDim);
			if (entity instanceof EntityPlayerMP) {
				Teleporter teleporter = new Tele(toDim, x, y, z, entity.rotationYaw, entity.rotationPitch);
				if (from == 1)
					entity.world.removeEntity(entity);
				server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, targetDim, teleporter);
				if (from == 1 && entity.isEntityAlive()) { // get around vanilla End hacks
					toDim.spawnEntity(entity);
					toDim.updateEntityWithOptionalForce(entity, false);
					entity.setPositionAndUpdate(x, y, z);
					entity.fallDistance = 0;
				}
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
					newEntity.forceSpawn = false; // necessary?
					newEntity.fallDistance = 0;
				} catch (Exception e) {
					LimeLib.log.error("serverTeleport: Error creating a entity to be created in new dimension.");
					return false;
				}
			}
		} else
			entity.setPositionAndUpdate(x, y, z);
		return true;
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
			entity.setLocationAndAngles(x, y, z, yaw, pitch);
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;

		}

		@Override
		public void removeStalePortalLocations(long p_removeStalePortalLocations_1_) {
		}

	}
}
