package kdp.limelib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kdp.limelib.helper.nbt.NBTBuilder;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = "limelib")
public class WorldAddition {

	@CapabilityInject(WorldAddition.class)
	public static Capability<WorldAddition> CAP = null;
	private static final ResourceLocation LOCATION = new ResourceLocation("limelib:wa");

	public static void register() {
		CapabilityManager.INSTANCE.register(WorldAddition.class, new IStorage<WorldAddition>() {

			@Override
			public INBTBase writeNBT(Capability<WorldAddition> capability, WorldAddition instance, EnumFacing side) {
				return NBTBuilder.of().build();
			}

			@Override
			public void readNBT(Capability<WorldAddition> capability, WorldAddition instance, EnumFacing side,
					INBTBase nbt) {
			}
		}, WorldAddition::new);
	}

	@SubscribeEvent
	public static void attach(AttachCapabilitiesEvent<World> event) {
		event.addCapability(LOCATION, new ICapabilitySerializable<NBTTagCompound>() {

			WorldAddition wa = CAP.getDefaultInstance();
			{
				wa.world = event.getObject();
			}
			LazyOptional<WorldAddition> lo = LazyOptional.of(() -> wa);

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, EnumFacing side) {
				if (cap != CAP)
					return LazyOptional.empty();
				return lo.cast();
			}

			@Override
			public NBTTagCompound serializeNBT() {
				return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, wa, null);
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt) {
				CAP.getStorage().readNBT(CAP, wa, null, nbt);
			}
		});
	}

	public static WorldAddition getWorldAddition(World world) {
		return world.getCapability(CAP).orElseThrow(NullPointerException::new);
	}

	private World world;
	private Map<BlockPos, DataPart> partMap = new HashMap<>();
	private Set<PseudoEntity> pseudoEntities = new HashSet<>();
	private int ID = 0;

	public DataPart getDataPart(BlockPos pos) {
		world.getChunk(pos);
		return partMap.get(pos);
	}

	public boolean addEntity(PseudoEntity ent) {
		/*if (!PARTS.inverse().containsKey(ent.getClass())) {
			LimeLib.log.error(ent.getClass() + " not registered.");
			return false;
		}
		Validate.isTrue(!world.isRemote, "don't add on client");
		Validate.isTrue(world == ent.world, "worlds not equal (" + world + " " + ent.world + ")");
		ent.id = ID++;
		entMap.put(ent.id, ent);*/
		//sync
		return true;
	}

}
