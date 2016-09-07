package mrriegel.limelib.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleportationHelper {

	public static void teleportToDimension(Entity entity, int dimension, BlockPos pos) {
		if (!canTeleport(entity))
			return;
		int oldDimension = entity.worldObj.provider.getDimension();
		if (oldDimension == dimension)
			return;
		MinecraftServer server = entity.worldObj.getMinecraftServer();
		WorldServer oldWorld = server.worldServerForDimension(oldDimension);
		WorldServer newWorld = server.worldServerForDimension(dimension);

		if (newWorld == null || newWorld.getMinecraftServer() == null) {
			throw new IllegalArgumentException("Dimension: " + dimension + " doesn't exist!");
		}
		oldWorld.updateEntityWithOptionalForce(entity, false);

		boolean loaded = newWorld.getChunkFromBlockCoords(pos).isLoaded();
		if (!loaded)
			newWorld.getChunkProvider().loadChunk(pos.getX() >> 4, pos.getZ() >> 4);
		// newWorld.getChunkProvider().unload(newWorld.getChunkFromBlockCoords(pos));
		newWorld.getBlockState(pos);
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.addExperienceLevel(0);
			newWorld.getMinecraftServer().getPlayerList().transferPlayerToDimension(player, dimension, new CustomTeleporter(newWorld, pos));
		} else {
			newWorld.getMinecraftServer().getPlayerList().transferEntityToWorld(entity, oldDimension, (WorldServer) entity.worldObj, newWorld, new CustomTeleporter(newWorld, pos));
			// oldWorld.removeEntity(entity);
		}
		teleportToPos(entity, pos);
		if (oldDimension == 1) {
			teleportToPos(entity, pos);
			newWorld.spawnEntityInWorld(entity);
			newWorld.updateEntityWithOptionalForce(entity, false);
		}
		entity.fallDistance = 0F;
	}

	public static void teleportToDimension(EntityPlayer player, WorldServer target, BlockPos pos) {
		teleportToDimension(player, target.provider.getDimension(), pos);
	}

	public static void teleportToPos(Entity e, BlockPos pos) {
		e.setPosition(pos.getX() + .5, pos.getY() + .05, pos.getZ() + .5);
	}

	public static boolean canTeleport(Entity e) {
		return e != null && !e.worldObj.isRemote && e.isEntityAlive() && !e.isBeingRidden() && e.isNonBoss() && !e.isRiding() && (e instanceof EntityLivingBase || e instanceof EntityItem);
	}

	public static class CustomTeleporter extends Teleporter {
		private final WorldServer worldServer;
		private final BlockPos pos;

		public CustomTeleporter(WorldServer worldServer, BlockPos pos) {
			super(worldServer);
			this.worldServer = worldServer;
			this.pos = pos;
		}

		@Override
		public void placeInPortal(Entity entityIn, float rotationYaw) {
			this.worldServer.getBlockState(pos);
			teleportToPos(entityIn, pos);
		}

	}
}
