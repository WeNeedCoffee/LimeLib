package mrriegel.limelib.helper;

import mrriegel.limelib.LimeLib;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.collect.Sets;

public class TeleportationHelper {

	public static void teleportEntity(EntityPlayer player, WorldServer target, BlockPos pos) {
		teleportEntity(player, target.provider.getDimension(), pos);
	}

	public static void teleportToPosAndUpdate(Entity e, BlockPos pos) {
		e.setPositionAndUpdate(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5);
	}

	public static void teleportToPos(Entity e, BlockPos pos) {
		e.setPositionAndRotation(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5, e.rotationYaw, e.rotationPitch);
	}

	public static boolean canTeleport(Entity e) {
		return e != null && !e.world.isRemote && e.isEntityAlive() && !e.isBeingRidden() && e.isNonBoss() && !e.isRiding() && (e instanceof EntityLivingBase || e instanceof EntityItem);
	}

	/** nicked from brandonscore */
	@Deprecated
	public static void teleportEntity(Entity entity, int dimension, BlockPos pos) {
		if (!canTeleport(entity))
			return;
		int oldDimension = entity.world.provider.getDimension();
		if (oldDimension == dimension)
			return;
		MinecraftServer server = entity.world.getMinecraftServer();
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
			player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
			oldWorld.getPlayerChunkMap().removePlayer(player);
			oldWorld.playerEntities.remove(player);
			oldWorld.updateAllPlayersSleepingFlag();
			int i = player.chunkCoordX;
			int j = player.chunkCoordZ;
			if ((player.addedToChunk) && (oldWorld.getChunkFromChunkCoords(i, j)).isPopulated()) {
				oldWorld.getChunkFromChunkCoords(i, j).removeEntity(player);
				oldWorld.getChunkFromChunkCoords(i, j).setModified(true);
			}
			oldWorld.loadedEntityList.remove(player);
			oldWorld.onEntityRemoved(player);
		}

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
		boolean flag = entity.forceSpawn;
		entity.forceSpawn = true;
		int entsize = newWorld.loadedEntityList.size(), plsize = newWorld.playerEntities.size();
		newWorld.spawnEntity(entity);
		System.out.println("new ent: " + (newWorld.loadedEntityList.size() - entsize));
		System.out.println("new pl: " + (newWorld.playerEntities.size() - plsize));
		entity.forceSpawn = flag;
		entity.setWorld(newWorld);
		teleportToPosAndUpdate(entity, pos);

		if ((entity instanceof EntityPlayerMP)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.mcServer.getPlayerList().preparePlayer(player, newWorld);
			player.connection.setPlayerLocation(pos.getX() + .5, pos.getY() + .1, pos.getZ() + .5, player.rotationYaw, player.rotationPitch);
		}

		newWorld.updateEntityWithOptionalForce(entity, false);

		if (entity instanceof EntityPlayerMP) {
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
		teleportToPosAndUpdate(entity, pos);
		newWorld.theProfiler.endSection();
		// if (!newWorld.loadedEntityList.contains(entity)) {
		// System.out.println("no1");
		newWorld.loadEntities(Sets.newHashSet(entity));
		// if (!newWorld.loadedEntityList.contains(entity)) {
		// System.out.println("no2");
		// newWorld.loadedEntityList.add(entity);
		// }
		// System.out.println("finally? "+newWorld.loadedEntityList.contains(entity));
		// }
		entity.fallDistance = 0;
		if (entity instanceof EntityPlayerMP) {
			PacketHandler.sendTo(new TeleportMessage(), (EntityPlayerMP) entity);
		}
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
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			WorldServer fromDim = server.worldServerForDimension(from);
			WorldServer toDim = server.worldServerForDimension(targetDim);
			Teleporter teleporter = new TeleporterEIO(toDim);
			// play sound at the dimension we are leaving for others to hear
			if (player != null) {
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
					// Throwables.propagate(e);
					LimeLib.log.error("serverTeleport: Error creating a entity to be created in new dimension.");
					return false;
				}
			}
		}

		// Force the chunk to load
		if (!entity.world.isBlockLoaded(pos)) {
			entity.world.getChunkFromBlockCoords(pos);
		}

		if (player != null) {
			player.connection.setPlayerLocation(x + 0.5, y + 1.1, z + 0.5, player.rotationYaw, player.rotationPitch);
		} else {
			entity.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);
		}

		entity.fallDistance = 0;
		if (entity instanceof EntityPlayerMP) {
			PacketHandler.sendTo(new TeleportMessage(), (EntityPlayerMP) entity);
		}
		// play sound

		return true;
	}

	public static class TeleporterEIO extends Teleporter {

		public TeleporterEIO(WorldServer p_i1963_1_) {
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
