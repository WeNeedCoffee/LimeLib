package mrriegel.limelib.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.collect.Lists;

public class WorldHelper {
	public static void spawnItemStack(World worldIn, BlockPos pos,
			ItemStack stack) {
		if (!worldIn.isRemote
				&& worldIn.getGameRules().getBoolean("doTileDrops")
				&& !worldIn.restoringBlockSnapshots) {
			float f = 0.5F;
			double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0,
					pos.getY() + d1, pos.getZ() + d2, stack);
			entityitem.setDefaultPickupDelay();
			worldIn.spawnEntityInWorld(entityitem);
		}
	}

	public static List<BlockPos> getNeighbors(BlockPos pos) {
		List<BlockPos> lis = new ArrayList<BlockPos>();
		lis.add(pos.up());
		lis.add(pos.down());
		lis.add(pos.east());
		lis.add(pos.west());
		lis.add(pos.north());
		lis.add(pos.south());
		return lis;
	}

	public static List<BlockPos> getPos(World world, Chunk c) {
		List<BlockPos> lis = Lists.newArrayList();
		for (int i = 256; i >= 0; i--)
			for (int j = c.xPosition * 16; j < c.xPosition * 16 + 16; j++)
				for (int k = c.zPosition * 16; k < c.zPosition * 16 + 16; k++)
					lis.add(new BlockPos(j, i, k));
		return lis;

	}

	public static Entity teleportPlayer(EntityPlayerMP player, World start,
			World end, BlockPos exitLoc) {
		WorldServer startWorld = (WorldServer) start, endWorld = (WorldServer) end;
		boolean dimensionalTransport = startWorld.provider.getDimensionId() != endWorld.provider
				.getDimensionId();
		float yaw = player.rotationYaw;
		ServerConfigurationManager config = null;
		double exitX = exitLoc.getX();
		double exitY = exitLoc.getY();
		double exitZ = exitLoc.getZ();

		player.closeScreen();

		if (dimensionalTransport) {
			config = player.mcServer.getConfigurationManager();
			player.dimension = endWorld.provider.getDimensionId();
			player.playerNetServerHandler.sendPacket(new S07PacketRespawn(
					player.dimension, player.worldObj.getDifficulty(), endWorld
							.getWorldInfo().getTerrainType(),
					player.theItemInWorldManager.getGameType()));

			startWorld.removeEntity(player);
			player.isDead = false;
			player.setLocationAndAngles(exitX, exitY, exitZ, yaw,
					player.rotationPitch);
			endWorld.spawnEntityInWorld(player);
			player.setWorld(endWorld);
			config.preparePlayer(player, startWorld);
			player.playerNetServerHandler.setPlayerLocation(exitX, exitY,
					exitZ, yaw, player.rotationPitch);
			player.theItemInWorldManager.setWorld(endWorld);

			config.updateTimeAndWeatherForPlayer(player, endWorld);
			config.syncPlayerInventory(player);

			player.worldObj.theProfiler.endSection();
			startWorld.resetUpdateEntityTick();
			endWorld.resetUpdateEntityTick();
			player.worldObj.theProfiler.endSection();

			for (Iterator<PotionEffect> potion = player
					.getActivePotionEffects().iterator(); potion.hasNext();)
				player.playerNetServerHandler
						.sendPacket(new S1DPacketEntityEffect(player
								.getEntityId(), potion.next()));

			player.playerNetServerHandler
					.sendPacket(new S1FPacketSetExperience(player.experience,
							player.experienceTotal, player.experienceLevel));

			FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player,
					startWorld.provider.getDimensionId(), player.dimension);
		} else {
			player.rotationYaw = yaw;
			player.setPositionAndUpdate(exitX, exitY, exitZ);
			player.worldObj.updateEntityWithOptionalForce(player, false);
		}

		return player;
	}
}
