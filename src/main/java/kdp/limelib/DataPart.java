package kdp.limelib;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DataPart extends ForgeRegistryEntry<DataPart> {

    private static final AxisAlignedBB FULL = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    protected BlockPos pos;
    protected World world;
    public int ticksExisted;

    public void update() {

    }

    public void onAdded() {

    }

    public void onRemoved() {

    }

    public boolean onRightClicked(PlayerEntity player, Hand hand) {
        return false;
    }

    public boolean onLeftClicked(PlayerEntity player, Hand hand) {
        return false;
    }

    public boolean clientValid() {
        return true;
    }

    public AxisAlignedBB getHighlightBox() {
        return FULL;
    }

    public final void readDataFromNBT(CompoundNBT compound) {
        pos = BlockPos.func_218283_e(compound.getLong("poS"));
        readFromNBT(compound);
    }

    public final CompoundNBT writeDataToNBT(CompoundNBT compound) {
        writeToNBT(compound);
        //TODO compound.setString("id", DataPartRegistry.PARTS.inverse().get(getClass()));
        compound.putLong("poS", pos.func_218275_a());
        return compound;
    }

    public void readFromNBT(CompoundNBT compound) {
    }

    public CompoundNBT writeToNBT(CompoundNBT compound) {
        return compound;
    }

    protected final WorldAddition getWorldAddition() {
        return WorldAddition.getWorldAddition(world);
    }
}
