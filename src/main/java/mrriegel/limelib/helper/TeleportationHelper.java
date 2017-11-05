package mrriegel.limelib.helper;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.util.GlobalBlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TeleportationHelper {

	public static void teleportEntity(EntityPlayer player, WorldServer target, BlockPos pos) {
		serverTeleport(player, pos, target.provider.getDimension());
	}

	//	public static void teleportToPosAndUpdate(Entity e, BlockPos pos) {
	//		e.setPositionAndUpdate(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5);
	//	}
	//
	//	public static void teleportToPos(Entity e, BlockPos pos) {
	//		e.setPositionAndRotation(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5, e.rotationYaw, e.rotationPitch);
	//	}

	public static boolean canTeleport(Entity e) {
		return e != null && !e.world.isRemote && e.isEntityAlive() && !e.isBeingRidden() && e.isNonBoss() && !e.isRiding() && (e instanceof EntityLivingBase || e instanceof EntityItem);
	}

	public static boolean serverTeleport(Entity entity, GlobalBlockPos pos) {
		return serverTeleport(entity, pos.getPos(), pos.getDimension());
	}

	public static boolean serverTeleport(Entity entity, BlockPos pos, int targetDim) {
		if (!canTeleport(entity))
			return false;
		EntityPlayerMP player = null;
		if (entity instanceof EntityPlayerMP) {
			player = (EntityPlayerMP) entity;
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int from = entity.dimension;
		if (from != targetDim) {
			MinecraftServer server = entity.world.getMinecraftServer();
			WorldServer fromDim = server.getWorld(from);
			WorldServer toDim = server.getWorld(targetDim);
			Teleporter teleporter = new Tele(toDim);
			if (player != null) {
				ReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, true, "invulnerableDimensionChange", "field_184851_cj");
				if (from == 1)
					entity.world.removeEntity(entity);
				server.getPlayerList().transferPlayerToDimension(player, targetDim, teleporter);
				if (from == 1 && entity.isEntityAlive()) { // get around vanilla
															// End
															// hacks
					toDim.spawnEntity(entity);
					toDim.updateEntityWithOptionalForce(entity, false);
				}
			} else {
				NBTTagCompound tagCompound = new NBTTagCompound();
				float rotationYaw = entity.rotationYaw;
				float rotationPitch = entity.rotationPitch;
				entity.writeToNBT(tagCompound);
				Class<? extends Entity> entityClass = entity.getClass();
				fromDim.removeEntity(entity);

				try {
					Entity newEntity = entityClass.getConstructor(World.class).newInstance(toDim);
					newEntity.readFromNBT(tagCompound);
					newEntity.setLocationAndAngles(x, y, z, rotationYaw, rotationPitch);
					newEntity.forceSpawn = true;
					toDim.spawnEntity(newEntity);
					newEntity.forceSpawn = false; // necessary?
				} catch (Exception e) {
					LimeLib.log.error("serverTeleport: Error creating a entity to be created in new dimension.");
					return false;
				}
			}
		}

		// Force the chunk to load
		if (!entity.world.isBlockLoaded(pos)) {
			entity.world.getChunkFromBlockCoords(pos);
		}

		entity.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);

		entity.fallDistance = 0;
		// play sound

		return true;
	}

	public static class Tele extends Teleporter {

		public Tele(WorldServer p_i1963_1_) {
			super(p_i1963_1_);
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
			int x = MathHelper.floor(entity.posX);
			int y = MathHelper.floor(entity.posY) - 1;
			int z = MathHelper.floor(entity.posZ);

			entity.setLocationAndAngles(x, y, z, entity.rotationPitch, entity.rotationYaw);
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;
		}

		@Override
		public void removeStalePortalLocations(long p_removeStalePortalLocations_1_) {
		}

	}
}
