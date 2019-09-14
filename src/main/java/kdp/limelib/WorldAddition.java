package kdp.limelib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
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

import kdp.limelib.helper.nbt.NBTBuilder;

//@EventBusSubscriber(modid = "limelib")
public class WorldAddition {

    @CapabilityInject(WorldAddition.class)
    public static Capability<WorldAddition> CAP = null;
    private static final ResourceLocation LOCATION = new ResourceLocation("limelib:wa");

    public static void register() {
        CapabilityManager.INSTANCE.register(WorldAddition.class, new IStorage<WorldAddition>() {

            @Override
            public INBT writeNBT(Capability<WorldAddition> capability, WorldAddition instance, Direction side) {
                return NBTBuilder.of().build();
            }

            @Override
            public void readNBT(Capability<WorldAddition> capability, WorldAddition instance, Direction side,
                    INBT nbt) {
            }
        }, WorldAddition::new);
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<World> event) {
        event.addCapability(LOCATION, new ICapabilitySerializable<CompoundNBT>() {

            WorldAddition wa = CAP.getDefaultInstance();

            {
                wa.world = event.getObject();
            }

            LazyOptional<WorldAddition> lo = LazyOptional.of(() -> wa);

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                if (cap != CAP)
                    return LazyOptional.empty();
                return lo.cast();
            }

            @Override
            public CompoundNBT serializeNBT() {
                return (CompoundNBT) CAP.getStorage().writeNBT(CAP, wa, null);
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
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
