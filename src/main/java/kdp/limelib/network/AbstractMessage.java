package kdp.limelib.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import kdp.limelib.ClientHelper;

public abstract class AbstractMessage {
    protected CompoundNBT nbt = new CompoundNBT();

    public AbstractMessage() {
    }

    public AbstractMessage(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public final void encode(PacketBuffer buffer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            CompressedStreamTools.write(nbt, dos);
        } catch (IOException e) {
        }
        buffer.writeByteArray(baos.toByteArray());
    }

    public final void decode(PacketBuffer buffer) {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.readByteArray());
        DataInputStream dis = new DataInputStream(bais);
        try {
            nbt = CompressedStreamTools.read(dis);
        } catch (IOException e) {
        }
    }

    public final void handleMessage(AbstractMessage message, Context context) {
        nbt = message.nbt;
        context.enqueueWork(() -> {
            PlayerEntity player = context.getDirection().getReceptionSide() == LogicalSide.SERVER ?
                    context.getSender() :
                    DistExecutor.callWhenOn(Dist.CLIENT,
                            () -> () -> Minecraft.getInstance().player) /*getClientPlayer().get().get()*/;
            handleMessage(player);
            context.setPacketHandled(true);
        });
    }

    public abstract void handleMessage(PlayerEntity player);

    @Deprecated
    private static Supplier<Supplier<PlayerEntity>> getClientPlayer() {
        return () -> () -> ClientHelper.getClientPlayer();
    }
}
