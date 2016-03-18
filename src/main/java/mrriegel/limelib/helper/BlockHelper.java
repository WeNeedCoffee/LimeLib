package mrriegel.limelib.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.sun.jna.platform.win32.WinDef.HDC;

import mrriegel.limelib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.oredict.OreDictionary;

public class BlockHelper {
	public static boolean isOreInDictionary(Block block) {
		for (String ore : OreDictionary.getOreNames()) {
			if (ore.startsWith("ore"))
				for (ItemStack s : OreDictionary.getOres(ore))
					if (Block.getBlockFromItem(s.getItem()) == block)
						return true;
		}
		return false;
	}

	public static boolean isGeneralValuableBlock(Block block) {
		Block[] blocks = new Block[] { Blocks.dirt, Blocks.stone, Blocks.grass,
				Blocks.sand, Blocks.sandstone, Blocks.cobblestone,
				Blocks.mycelium, Blocks.gravel, Blocks.planks,
				Blocks.netherrack, Blocks.soul_sand, Blocks.end_stone,
				Blocks.bedrock };
		for (Block b : blocks)
			if (b == block || block.getMaterial().isLiquid())
				return false;
		return true;
	}

	public static boolean breakWithFortune(EntityPlayer player, World world,
			BlockPos pos, int fortune, boolean event) {
		IBlockState state = world.getBlockState(pos);
		if (world.isAirBlock(pos) || world.isRemote) {
			return false;
		} else {
			BlockEvent.BreakEvent b = new BlockEvent.BreakEvent(world, pos,
					state, player);
			if (event)
				MinecraftForge.EVENT_BUS.post(b);
			if (player == null)
				player = FakePlayerFactory.getMinecraft((WorldServer) world);
			if (b.isCanceled())
				return false;
			BlockEvent.HarvestDropsEvent h = new BlockEvent.HarvestDropsEvent(
					world, pos, state, fortune, 1F, state.getBlock().getDrops(
							world, pos, state, fortune), player, false);
			if (event)
				MinecraftForge.EVENT_BUS.post(h);
			if (world.getTileEntity(pos) instanceof IInventory) {
				IInventory inv = (IInventory) world.getTileEntity(pos);
				for (int i = 0; i < inv.getSizeInventory(); i++)
					if (inv.getStackInSlot(i) != null)
						h.drops.add(inv.getStackInSlot(i).copy());
			}
			for (ItemStack s : h.drops) {
				if (world.rand.nextFloat() <= h.dropChance)
					WorldHelper.spawnItemStack(world, pos, s);
			}
			state.getBlock().dropXpOnBlockBreak(world, pos, b.getExpToDrop());
			world.playAuxSFXAtEntity(player, 2001, pos, 0);
			world.setBlockToAir(pos);
			if(world.getTileEntity(pos)!=null)
				world.removeTileEntity(pos);
			return true;
		}
	}

	public static boolean breakWithSilk(EntityPlayer player, World world,
			BlockPos pos, boolean event) {
		IBlockState state = world.getBlockState(pos);
		if (world.isAirBlock(pos) || world.isRemote) {
			return false;
		} else {
			if (!state.getBlock().canSilkHarvest(world, pos, state, player))
				return false;
			BlockEvent.BreakEvent b = new BlockEvent.BreakEvent(world, pos,
					state, player);
			if (event)
				MinecraftForge.EVENT_BUS.post(b);
			if (player == null)
				player = FakePlayerFactory.getMinecraft((WorldServer) world);
			if (b.isCanceled())
				return false;
			BlockEvent.HarvestDropsEvent h = new BlockEvent.HarvestDropsEvent(
					world, pos, state, 0, 1F, Arrays.asList(new ItemStack(Item
							.getItemFromBlock(state.getBlock()), 1, state
							.getBlock().getMetaFromState(state))), player, true);
			if (event)
				MinecraftForge.EVENT_BUS.post(h);
			if (world.getTileEntity(pos) instanceof IInventory) {
				IInventory inv = (IInventory) world.getTileEntity(pos);
				for (int i = 0; i < inv.getSizeInventory(); i++)
					if (inv.getStackInSlot(i) != null)
						h.drops.add(inv.getStackInSlot(i).copy());
			}
			for (ItemStack s : h.drops) {
				if (world.rand.nextFloat() <= h.dropChance)
					WorldHelper.spawnItemStack(world, pos, s);
			}
			world.playAuxSFXAtEntity(player, 2001, pos, 0);
			world.setBlockToAir(pos);
			if(world.getTileEntity(pos)!=null)
				world.removeTileEntity(pos);
			return true;
		}
	}

	public static List<ItemStack> breakBlockWithFortune(EntityPlayer player,
			World world, BlockPos pos, int fortune, boolean event) {
		IBlockState state = world.getBlockState(pos);
		List<ItemStack> lis = Lists.newArrayList();
		if (world.isAirBlock(pos) || world.isRemote) {
			return lis;
		} else {
			if (player == null)
				player = FakePlayerFactory.getMinecraft((WorldServer) world);
			BlockEvent.BreakEvent b = new BlockEvent.BreakEvent(world, pos,
					state, player);
			if (event)
				MinecraftForge.EVENT_BUS.post(b);
			if (b.isCanceled())
				return lis;
			BlockEvent.HarvestDropsEvent h = new BlockEvent.HarvestDropsEvent(
					world, pos, state, fortune, 1F, state.getBlock().getDrops(
							world, pos, state, fortune), player, false);
			if (event)
				MinecraftForge.EVENT_BUS.post(h);
			if (world.getTileEntity(pos) instanceof IInventory) {
				IInventory inv = (IInventory) world.getTileEntity(pos);
				for (int i = 0; i < inv.getSizeInventory(); i++)
					if (inv.getStackInSlot(i) != null)
						h.drops.add(inv.getStackInSlot(i).copy());
			}
			for (ItemStack s : h.drops) {
				if (world.rand.nextFloat() <= h.dropChance)
					lis.add(s);
			}
			state.getBlock().dropXpOnBlockBreak(world, pos, b.getExpToDrop());
			world.playAuxSFXAtEntity(player, 2001, pos, 0);
			world.setBlockToAir(pos);
			if(world.getTileEntity(pos)!=null)
				world.removeTileEntity(pos);
			return lis;
		}
	}

	public static List<ItemStack> breakBlockWithSilk(EntityPlayer player,
			World world, BlockPos pos, boolean event) {
		IBlockState state = world.getBlockState(pos);
		List<ItemStack> lis = Lists.newArrayList();
		if (world.isAirBlock(pos) || world.isRemote) {
			return lis;
		} else {
			if (!state.getBlock().canSilkHarvest(world, pos, state, player))
				return lis;
			BlockEvent.BreakEvent b = new BlockEvent.BreakEvent(world, pos,
					state, player);
			if (event)
				MinecraftForge.EVENT_BUS.post(b);
			if (player == null)
				player = FakePlayerFactory.getMinecraft((WorldServer) world);
			if (b.isCanceled())
				return lis;
			BlockEvent.HarvestDropsEvent h = new BlockEvent.HarvestDropsEvent(
					world, pos, state, 0, 1F, Arrays.asList(new ItemStack(Item
							.getItemFromBlock(state.getBlock()), 1, state
							.getBlock().getMetaFromState(state))), player, true);
			if (event)
				MinecraftForge.EVENT_BUS.post(h);
			if (world.getTileEntity(pos) instanceof IInventory) {
				IInventory inv = (IInventory) world.getTileEntity(pos);
				for (int i = 0; i < inv.getSizeInventory(); i++)
					if (inv.getStackInSlot(i) != null)
						h.drops.add(inv.getStackInSlot(i).copy());
			}
			for (ItemStack s : h.drops) {
				if (world.rand.nextFloat() <= h.dropChance)
					lis.add(s);
			}
			world.playAuxSFXAtEntity(player, 2001, pos, 0);
			world.setBlockToAir(pos);
			if(world.getTileEntity(pos)!=null)
				world.removeTileEntity(pos);
			return lis;
		}
	}
}
