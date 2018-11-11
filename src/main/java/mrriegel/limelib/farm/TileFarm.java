package mrriegel.limelib.farm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileFarm extends TileEntity implements ITickable {

	Set<Farmer> farmers = Collections.newSetFromMap(new IdentityHashMap<>());

	@Override
	public NBTTagCompound getUpdateTag() {
		return serializeNBT();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1337, serializeNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		deserializeNBT(pkt.getNbtCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList lis = compound.getTagList("lis", 10);
		for (NBTBase n : lis) {
			Farmer f = new Farmer(world, 0, 0, 0, this, "");
			f.deserializeNBT((NBTTagCompound) n);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList lis = new NBTTagList();
		for (Farmer f : farmers)
			lis.appendTag(f.serializeNBT());
		compound.setTag("lis", lis);
		return super.writeToNBT(compound);
	}

	@Override
	public void update() {
		if (farmers.isEmpty()) {
			farmers.add(new Farmer(world, pos.getX() + 1.5, pos.getY() + .5, pos.getZ() + .5, this, "axe"));
			//			farmers.add(new Farmer(world, pos.getX() - .5, pos.getY() + .5, pos.getZ() + .5, this, "hoe"));
		}
		for (Farmer f : farmers)
			f.update();
	}

	protected IItemHandler getInv() {
		TileEntity t = world.getTileEntity(pos.up());
		if (t != null && t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
			return t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		return null;
	}

	static class Farmer extends PseudoEntity {

		TileFarm tile;
		String clas;
		ItemStack s;
		Predicate<BlockPos> harvestPred;
		BiConsumer<BlockPos, IItemHandler> worker;

		public Farmer(World world, double posX, double posY, double posZ, TileFarm tile, String clas) {
			super(world, posX, posY, posZ);
			this.tile = tile;
			setClas(clas);
		}

		@Override
		public void update() {
			super.update();
			if (!world.isRemote && world.getTotalWorldTime() % 10 == 0 && mover == null) {
				if (true) {
					move(Stream.of(tile.pos.add(6, 6, 6), tile.pos.add(-6, 3, -6)).sorted((p1, p2) -> Double.compare(getPosition().distanceTo(new Vec3d(p2)), getPosition().distanceTo(new Vec3d(p1)))).findFirst().orElse(null), 2);
					if (mover != null)
						FarmMod.INSTANCE.snw.sendToDimension(new MessageToC(tile), world.provider.getDimension());
					return;
				}
				BlockPos pp1 = tile.pos.add(6, 10, 6), pp2 = tile.pos.add(-6, 6, -6);
				List<BlockPos> valids = new ArrayList<>();
				for (BlockPos p : BlockPos.getAllInBox(tile.pos.add(-11, -11, -11), tile.pos.add(11, 11, 11))) {
					if (harvestPred.test(p)) {
						valids.add(p);
					}
				}
				valids.sort((p2, p1) -> Double.compare(getPosition().distanceTo(new Vec3d(p2)), getPosition().distanceTo(new Vec3d(p1))));
				if (!valids.isEmpty()) {
					move(valids.get(0), 2);
					if (mover != null)
						FarmMod.INSTANCE.snw.sendToDimension(new MessageToC(tile), world.provider.getDimension());
				}
			}
		}

		@Override
		protected void onArrival() {
			if (world.isRemote)
				return;
			BlockPos p = new BlockPos(getPosition());
			IItemHandler inv = tile.getInv();
			if (harvestPred.test(p) && inv != null) {
				worker.accept(p, inv);
			}

		}

		public void setClas(String clas) {
			this.clas = clas;
			if (clas.equals("axe")) {
				s = new ItemStack(Items.DIAMOND_AXE);
				harvestPred = p -> {
					IBlockState state = tile.world.getBlockState(p);
					return state.getBlock() == Blocks.LOG || state.getBlock() == Blocks.LOG2 || state.getBlock().getMaterial(state) == Material.LEAVES;
				};
				worker = (p, inv) -> {
					IBlockState state = world.getBlockState(p);
					List<ItemStack> drops = Lists.newLinkedList(state.getBlock().getDrops(world, p, state, 0));
					world.destroyBlock(p, false);
					for (ItemStack s : drops) {
						Block.spawnAsEntity(world, p, ItemHandlerHelper.insertItem(inv, s, false));
					}
					BlockPos soil = p.down();
					state = world.getBlockState(soil);
					for (int i = 0; i < inv.getSlots(); i++) {
						ItemStack ss = inv.getStackInSlot(i);
						if (Block.getBlockFromItem(ss.getItem()) instanceof IPlantable) {
							if (state.getBlock().canSustainPlant(state, world, soil, EnumFacing.UP, (IPlantable) Block.getBlockFromItem(ss.getItem()))) {
								ss = inv.extractItem(i, 1, false);
								if (!ss.isEmpty()) {
									if (world.setBlockState(p, ((IPlantable) Block.getBlockFromItem(ss.getItem())).getPlant(world, p)))
										break;
								}
							}
						}
					}
				};
				worker = (p, inv) -> {
				};
				harvestPred = p -> world.rand.nextDouble() < .002;
			} else if (clas.equals("hoe")) {
				s = new ItemStack(Items.DIAMOND_HOE);
				harvestPred = p -> {
					IBlockState state = tile.world.getBlockState(p);
					return state.getBlock() instanceof BlockCrops && ((BlockCrops) state.getBlock()).isMaxAge(state);
				};
				worker = (p, inv) -> {
					IBlockState state = world.getBlockState(p);
					BlockCrops crop = (BlockCrops) state.getBlock();
					List<ItemStack> drops = Lists.newLinkedList(crop.getDrops(world, p, state, 0));
					IBlockState neww = Blocks.AIR.getDefaultState();
					Iterator<ItemStack> it = drops.iterator();
					while (it.hasNext()) {
						ItemStack s = it.next();
						if (s.getItem() instanceof IPlantable) {
							IPlantable plant = (IPlantable) s.getItem();
							if ( //plant.getPlantType(world, pos) == EnumPlantType.Crop &&
							plant.getPlant(world, p) != null && plant.getPlant(world, p).getBlock() == crop) {
								neww = plant.getPlant(world, p);
								it.remove();
								break;
							}
						}
					}
					world.destroyBlock(p, false);
					world.setBlockState(p, neww);
					for (ItemStack s : drops) {
						Block.spawnAsEntity(world, p, ItemHandlerHelper.insertItem(inv, s, false));
					}
				};
			}
		}

		@Override
		public NBTTagCompound serializeNBT() {

			NBTTagCompound n = super.serializeNBT();
			n.setString("clas", clas);
			return n;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			super.deserializeNBT(nbt);
			setClas(nbt.getString("clas"));
		}

	}
}
