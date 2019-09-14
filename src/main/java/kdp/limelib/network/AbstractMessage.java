package kdp.limelib.network;

import java.io.DataInputStream;
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

import org.apache.commons.lang3.Validate;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import kdp.limelib.ClientHelper;

public abstract class AbstractMessage {
    protected CompoundNBT nbt = new CompoundNBT();

    public AbstractMessage() {
    }

    public AbstractMessage(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public final void encode(PacketBuffer buffer) {
        ByteBufOutputStream bbos = new ByteBufOutputStream(buffer);
        try {
            CompressedStreamTools.write(nbt, bbos);
        } catch (IOException e) {
        }
    }

    public final void decode(PacketBuffer buffer) {
        ByteBufInputStream bbis = new ByteBufInputStream(buffer);
        DataInputStream dis = new DataInputStream(bbis);
        try {
            nbt = CompressedStreamTools.read(dis);
        } catch (IOException e) {
        }
    }

    public final void handleMessage(AbstractMessage message, Context context) {
        nbt = message.nbt;
        context.enqueueWork(() -> {
            Validate.isTrue(context.getDirection().getReceptionSide() != context.getDirection().getOriginationSide(),
                    "Why from %s to %s?", context.getDirection().getOriginationSide().toString(),
                    context.getDirection().getReceptionSide().toString());
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
