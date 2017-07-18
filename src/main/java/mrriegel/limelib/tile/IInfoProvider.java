package mrriegel.limelib.tile;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mrriegel.limelib.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "waila"), @Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoProvider", modid = "theoneprobe") })
public interface IInfoProvider<T extends TileEntity> extends IWailaDataProvider, IProbeInfoProvider {

	Class<T> getTileClass();

	default ItemStack getStack(T tile) {
		return ItemStack.EMPTY;
	}

	default List<String> getHeadLines(T tile, IBlockState state, EntityPlayer player, ItemStack stack, List<String> currenttip) {
		return currenttip;
	}

	default List<String> getBodyLines(T tile, IBlockState state, EntityPlayer player, ItemStack stack, List<String> currenttip) {
		return currenttip;
	}

	default List<String> getTailLines(T tile, IBlockState state, EntityPlayer player, ItemStack stack, List<String> currenttip) {
		return currenttip;
	}

	@Override
	default ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return getStack((T) accessor.getTileEntity());
	}

	@Override
	default List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return getHeadLines((T) getSyncedTile(accessor.getTileEntity(), accessor), accessor.getBlockState(), accessor.getPlayer(), itemStack, currenttip);
	}

	@Override
	default List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return getBodyLines((T) getSyncedTile(accessor.getTileEntity(), accessor), accessor.getBlockState(), accessor.getPlayer(), itemStack, currenttip);
	}

	@Override
	default List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return getTailLines((T) getSyncedTile(accessor.getTileEntity(), accessor), accessor.getBlockState(), accessor.getPlayer(), itemStack, currenttip);
	}

	static TileEntity getSyncedTile(TileEntity t, IWailaDataAccessor accessor) {
		try {
			TileEntity tile = ConstructorUtils.invokeConstructor(t.getClass());
			tile.handleUpdateTag(accessor.getNBTData());
			tile.setWorld(t.getWorld());
			return tile;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
		}
		return null;
	}

	@Override
	default NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		tag = te.getUpdateTag();
		return tag;
	}

	@Override
	default String getID() {
		return Utils.getCurrentModID();
	}

	@Override
	default void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		if (probeInfo != null && world != null && blockState != null && data != null) {
			BlockPos pos = data.getPos();
			if (pos != null) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if (tileEntity == null || tileEntity.getClass() != getTileClass())
					return;
				T t = (T) tileEntity;
				if (!getStack(t).isEmpty())
					probeInfo.item(getStack(t));
				getHeadLines(t, blockState, player, data.getPickBlock(), Lists.newArrayList()).forEach(s -> probeInfo.text(s));
				getBodyLines(t, blockState, player, data.getPickBlock(), Lists.newArrayList()).forEach(s -> probeInfo.text(s));
				getTailLines(t, blockState, player, data.getPickBlock(), Lists.newArrayList()).forEach(s -> probeInfo.text(s));
			}
		}

	}

	static class Dummy {
		private static List<Pair<IInfoProvider<?>, Class<? extends TileEntity>>> providers = Lists.newArrayList();
	}

	public static void registerProvider(IInfoProvider<?> provider, Class<? extends TileEntity> clazz) {
		Dummy.providers.add(Pair.of(provider, clazz));
	}

	public static List<Pair<IInfoProvider<?>, Class<? extends TileEntity>>> getProviders() {
		return Collections.unmodifiableList(Dummy.providers);
	}

}
