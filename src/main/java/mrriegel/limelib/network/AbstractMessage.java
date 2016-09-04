package mrriegel.limelib.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

public abstract class AbstractMessage<T extends AbstractMessage<T>> implements IMessage, IMessageHandler<T, IMessage> {

	protected NBTTagCompound nbt;

	public AbstractMessage() {
	}

	public AbstractMessage(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	public AbstractMessage(Object... os) {
		NBTTagCompound n = new NBTTagCompound();
		List<NBTBase> lis = NBTHelper.getNBTs(os);
		n.setInteger("size", lis.size());
		for (int i = 0; i < lis.size(); i++)
			n.setTag("" + i, lis.get(i));
		this.nbt = n;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}

	public abstract void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side);

	// public static class Handler<T extends AbstractMessage<T>> implements
	// IMessageHandler<T, IMessage> {
	//
	// @Override
	// public IMessage onMessage(final T message, final MessageContext ctx) {
	// Runnable run = new Runnable() {
	// @Override
	// public void run() {
	// message.handleMessage(ctx.side.isClient() ?
	// Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity,
	// message.nbt);
	// }
	// };
	// (ctx.side.isClient() ? Minecraft.getMinecraft() :
	// ctx.getServerHandler().playerEntity.getServerWorld()).addScheduledTask(run);
	// return null;
	// }
	//
	// }

	@Override
	public IMessage onMessage(final T message, final MessageContext ctx) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				message.handleMessage(ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : ctx.getServerHandler().playerEntity, message.nbt, ctx.side);
			}
		};
		(ctx.side.isClient() ? Minecraft.getMinecraft() : ctx.getServerHandler().playerEntity.getServerWorld()).addScheduledTask(run);
		return null;
	}

}
