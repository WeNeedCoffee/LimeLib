package mrriegel.limelib.util;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.mojang.authlib.GameProfile;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.util.TypeAdapters.ItemLizer;
import mrriegel.limelib.util.TypeAdapters.ItemStackLizer;
import mrriegel.limelib.util.TypeAdapters.NBTLizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Utils {

	private static GsonBuilder GSONBUILDER;

	public static void init() {
	}

	public static Gson getGSON() {
		if (GSONBUILDER == null)
			registerDefaultAdapters();
		return GSONBUILDER.create();
	}

	private static void registerDefaultAdapters() {
		GSONBUILDER = new GsonBuilder().setPrettyPrinting().//
				registerTypeAdapter(NBTTagCompound.class, new NBTLizer()).//
				registerTypeAdapter(Item.class, new ItemLizer()).//
				registerTypeAdapter(ItemStack.class, new ItemStackLizer());
	}

	public static void registerGsonAdapter(Type type, Object adapter) {
		Preconditions.checkArgument(adapter instanceof JsonSerializer<?> || adapter instanceof JsonDeserializer<?> || adapter instanceof InstanceCreator<?> || adapter instanceof TypeAdapter<?>);
		getGSON();
		GSONBUILDER.registerTypeAdapter(type, adapter);
	}

	public static String getCurrentModID() {
		return new Item().setRegistryName("dummy").getRegistryName().getResourceDomain();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Enum> getEnums(Class<? extends Enum> clazz) {
		List<Enum> lis = Lists.newArrayList();
		EnumSet enums = EnumSet.allOf(clazz);
		Map<Integer, Enum> map = Maps.newHashMap();
		for (Object o : enums) {
			Enum e = (Enum) o;
			map.put(e.ordinal(), e);
		}
		for (int i = 0; i < map.size(); i++) {
			lis.add(map.get(i));
		}
		return lis;
	}

	public static List<Integer> split(int ii, int splits) {
		List<Integer> ints = Lists.newArrayList();
		for (int i = 0; i < splits; i++)
			ints.add(ii / splits);
		for (int i = 0; i < ii % splits; i++)
			ints.set(i, ints.get(i) + 1);
		return ints;
	}

	public static String formatNumber(long value) {
		if (Math.abs(value) < 1000)
			return String.valueOf(value);
		else if (Math.abs(value) < 1000000)
			return String.valueOf((int) (Math.round(value) / 1000D)) + "K";
		else if (Math.abs(value) < 1000000000)
			return String.valueOf((int) (Math.round(value / 1000) / 1000D)) + "M";
		else
			return String.valueOf((int) (Math.round(value / 1000000) / 1000D)) + "G";
	}

	public static FakePlayer getFakePlayer(WorldServer world) {
		UUID uu = UUID.fromString("672ec311-27a5-449e-925c-69a55980d378");
		while (world.getEntityFromUuid(uu) != null)
			uu = UUID.randomUUID();
		return FakePlayerFactory.get(world, new GameProfile(uu, LimeLib.MODID + "_fake_player"));
	}

	public static FakePlayer getFakePlayerWithItem(WorldServer world, ItemStack stack) {
		FakePlayer player = getFakePlayer(world);
		player.inventory.mainInventory.set((player.inventory.currentItem = 0), stack);
		return player;
	}

	public static RayTraceResult rayTrace(Entity entity, double distance) {
		Vec3d vec3d = entity.getPositionEyes(0);
		Vec3d vec3d1 = entity.getLook(0);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
		return entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	public static RayTraceResult rayTrace(EntityPlayer player) {
		return rayTrace(player, player.capabilities.isCreativeMode ? 5F : 4.5F);
	}

	public static String getModID(IForgeRegistryEntry<?> registerable) {
		final String modID = registerable.getRegistryName().getResourceDomain();
		ModContainer mod = Loader.instance().getIndexedModList().get(modID);
		if (mod == null) {
			for (String s : Loader.instance().getIndexedModList().keySet()) {
				if (s.equalsIgnoreCase(modID)) {
					mod = Loader.instance().getIndexedModList().get(s);
					break;
				}
			}
		}
		return mod != null ? mod.getModId() : "minecraft";
	}

	public static String getModName(IForgeRegistryEntry<?> registerable) {
		ModContainer m = Loader.instance().getIndexedModList().get(getModID(registerable));
		if (m != null)
			return m.getName();
		else
			return "Minecraft";
	}

	public static EntityPlayerMP getRandomPlayer() {
		List<WorldServer> lis = Lists.newArrayList(FMLCommonHandler.instance().getMinecraftServerInstance().worlds);
		if (lis.isEmpty())
			return null;
		Collections.shuffle(lis);
		for (WorldServer world : lis) {
			EntityPlayerMP player = getRandomPlayer(world);
			if (player != null)
				return player;
		}
		return null;
	}

	public static EntityPlayerMP getRandomPlayer(WorldServer world) {
		if (world.playerEntities.isEmpty())
			return null;
		return (EntityPlayerMP) world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));
	}

	public static int storeBytes(byte a, byte b, byte c, byte d) {
		return (a << 24) ^ (b << 16) ^ (c << 8) ^ d;
	}

	public static byte[] loadBytes(int a) {
		return new byte[] { (byte) (a >> 24 & 0xFF), (byte) (a >> 16 & 0xFF), (byte) (a >> 8 & 0xFF), (byte) (a & 0xFF) };
	}

	public static int storeShorts(short a, short b) {
		return (a << 16) ^ (b);
	}

	public static short[] loadShorts(int a) {
		return new short[] { (short) (a >> 16 & 0xFFFF), (short) (a & 0xFFFF) };
	}

	public static String toASCII(String text) {
		return Base64.getEncoder().encodeToString(text.getBytes());
	}

	public static String fromASCII(String ascii) {
		return new String(Base64.getDecoder().decode(ascii.getBytes()));
	}
}
