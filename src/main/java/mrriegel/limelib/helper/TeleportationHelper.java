package mrriegel.limelib.helper;

import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.network.TeleportMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TeleportationHelper {

	@Deprecated
	public static void teleportToDimension(Entity entity, int dimension, BlockPos pos) {
		if (!canTeleport(entity))
			return;
		if (dimension != Integer.MAX_VALUE - Short.MAX_VALUE)
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
		Teleporter teleporter = new CustomTeleporter(newWorld, pos);
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.addExperienceLevel(0);
			newWorld.getMinecraftServer().getPlayerList().transferPlayerToDimension(player, dimension, teleporter);
		} else {
			newWorld.getMinecraftServer().getPlayerList().transferEntityToWorld(entity, oldDimension, (WorldServer) entity.worldObj, newWorld, teleporter);
			// oldWorld.removeEntity(entity);
		}
		teleportToPos(entity, pos);
		if (oldDimension == 1) {
			oldWorld.theProfiler.startSection("placing");
			if (entity.isEntityAlive()) {
				teleporter.placeInPortal(entity, 0);
				newWorld.spawnEntityInWorld(entity);
				newWorld.updateEntityWithOptionalForce(entity, false);
			}
			oldWorld.theProfiler.endSection();
		}
		entity.fallDistance = 0F;
		if (entity instanceof EntityPlayerMP) {
			NBTTagCompound nbt = new NBTTagCompound();
			PacketHandler.sendTo(new TeleportMessage(nbt), (EntityPlayerMP) entity);
		}
	}

	public static void teleportEntity(EntityPlayer player, WorldServer target, BlockPos pos) {
		teleportEntity(player, target.provider.getDimension(), pos);
	}

	public static void teleportToPos(Entity e, BlockPos pos) {
		e.setPositionAndUpdate(pos.getX() + .5, pos.getY() + .09, pos.getZ() + .5);
	}

	public static boolean canTeleport(Entity e) {
		return e != null && !e.worldObj.isRemote && e.isEntityAlive() && !e.isBeingRidden() && e.isNonBoss() && !e.isRiding() && (e instanceof EntityLivingBase || e instanceof EntityItem);
	}

	/** nicked from brandonscore */
	public static void teleportEntity(Entity entity, int dimension, BlockPos pos) {
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

		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.closeScreen();
			player.addExperienceLevel(0);
			player.dimension = newWorld.provider.getDimension();
			player.connection.sendPacket(new SPacketRespawn(player.dimension, player.worldObj.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
			oldWorld.getPlayerChunkMap().removePlayer(player);

			oldWorld.playerEntities.remove(player);
			oldWorld.updateAllPlayersSleepingFlag();
			int i = entity.chunkCoordX;
			int j = entity.chunkCoordZ;
			if ((entity.addedToChunk) && (oldWorld.getChunkFromChunkCoords(i, j)).isPopulated()) {
				oldWorld.getChunkFromChunkCoords(i, j).removeEntity(entity);
				oldWorld.getChunkFromChunkCoords(i, j).setModified(true);
			}
			oldWorld.loadedEntityList.remove(entity);
			oldWorld.onEntityRemoved(entity);
		}

		teleportToPos(entity, pos);

		newWorld.getChunkProvider().loadChunk(pos.getX() >> 4, pos.getZ() >> 4);

		newWorld.theProfiler.startSection("placing");
		if (!(entity instanceof EntityPlayer)) {
			NBTTagCompound entityNBT = new NBTTagCompound();
			entity.isDead = false;
			entityNBT.setString("id", EntityList.getEntityString(entity));
			entity.writeToNBT(entityNBT);
			entity.isDead = true;
			entity = EntityList.createEntityFromNBT(entityNBT, newWorld);
			if (entity == null) {
				throw new IllegalArgumentException("Failed to teleport entity to new location");
			}
			entity.dimension = newWorld.provider.getDimension();
		}
		newWorld.spawnEntityInWorld(entity);
		entity.setWorld(newWorld);
		teleportToPos(entity, pos);

		// destinationWorld.updateEntityWithOptionalForce(entity, false);
		// entity.setLocationAndAngles(destination.xCoord, destination.yCoord,
		// destination.zCoord, destination.yaw, entity.rotationPitch);

		if ((entity instanceof EntityPlayerMP)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.mcServer.getPlayerList().preparePlayer(player, newWorld);
			player.connection.setPlayerLocation(pos.getX() + .5, pos.getY() + .09, pos.getZ() + .5, player.rotationYaw, player.rotationPitch);
		}

		newWorld.updateEntityWithOptionalForce(entity, false);

		if (((entity instanceof EntityPlayerMP))) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.interactionManager.setWorld(newWorld);
			player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
			player.mcServer.getPlayerList().syncPlayerInventory(player);

			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potionEffect));
			}

			player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
			FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldWorld.provider.getDimension(), newWorld.provider.getDimension());
		}
		teleportToPos(entity, pos);
		newWorld.theProfiler.endSection();
		entity.fallDistance = 0;
		if (entity instanceof EntityPlayerMP) {
			NBTTagCompound nbt = new NBTTagCompound();
			PacketHandler.sendTo(new TeleportMessage(nbt), (EntityPlayerMP) entity);
		}
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
