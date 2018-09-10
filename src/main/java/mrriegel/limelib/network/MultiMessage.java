package mrriegel.limelib.network;

import java.util.function.BiConsumer;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class MultiMessage extends AbstractMessage {

	private static final String act = "_|Ii()\\";

	public static enum Act {
		TEST((os, n) -> {
			for (Object o : os)
				NBTHelper.set(n, o.hashCode() + "", o);
		}, (p, n) -> {
			System.out.println(n);
		});

		private Act(BiConsumer<Object[], NBTTagCompound> constructor, BiConsumer<EntityPlayer, NBTTagCompound> consumer) {
			this.constructor = constructor;
			this.consumer = consumer;
		}

		BiConsumer<Object[], NBTTagCompound> constructor;
		BiConsumer<EntityPlayer, NBTTagCompound> consumer;
	}

	public MultiMessage() {
	}

	private MultiMessage(Act act) {
		NBTHelper.set(nbt, MultiMessage.act, act);
	}

	public MultiMessage(Act act, Object... vals) {
		this(act);
		act.constructor.accept(vals, nbt);
	}

	@Override
	public void handleMessage(EntityPlayer player, NBTTagCompound nbt, Side side) {
		Act act = NBTHelper.get(nbt, MultiMessage.act, Act.class);
		act.consumer.accept(player, nbt);
	}

}
