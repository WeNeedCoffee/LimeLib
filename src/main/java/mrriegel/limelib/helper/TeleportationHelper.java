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
		newWorld.spawnEntityInWorld(entity);
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
			NBTTagCompound nbt = new NBTTagCompound();
			PacketHandler.sendTo(new TeleportMessage(nbt), (EntityPlayerMP) entity);
		}
	}
}
