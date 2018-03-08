package mrriegel.limelib.stacks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import mrriegel.limelib.block.CommonBlockContainer;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.tile.CommonTile;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

public class BlockIngots extends CommonBlockContainer<CommonTile> {
	//	public static final IUnlistedProperty<TileIngots> prop = new IUnlistedProperty<BlockIngots.TileIngots>() {
	//
	//		@Override
	//		public String getName() {
	//			return "tile";
	//		}
	//
	//		@Override
	//		public boolean isValid(TileIngots value) {
	//			return true;
	//		}
	//
	//		@Override
	//		public Class<TileIngots> getType() {
	//			return TileIngots.class;
	//		}
	//
	//		@Override
	//		public String valueToString(TileIngots value) {
	//			return value.toString();
	//		}
	//	};

	public static final IUnlistedProperty<Long> prop = new IUnlistedProperty<Long>() {

		@Override
		public String getName() {
			return "tile";
		}

		@Override
		public boolean isValid(Long value) {
			return true;
		}

		@Override
		public Class<Long> getType() {
			return Long.class;
		}

		@Override
		public String valueToString(Long value) {
			return value + "";
		}

	};

	public BlockIngots() {
		super(Material.IRON, "ingots");
		setHardness(3f);
		setDefaultState(((IExtendedBlockState) getDefaultState()).withProperty(prop, 1L));
	}

	@Override
	protected Class<? extends CommonTile> getTile() {
		return TileIngots.class;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		state = ((IExtendedBlockState) state).withProperty(prop, pos.toLong());
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] { prop });
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileIngots) {
				playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, ItemHandlerHelper.insertItemStacked(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), playerIn.inventory.getCurrentItem(), false));
				return true;
			}
			return false;
		}
	}

	public static class TileIngots extends CommonTile {
		public IItemHandler handler = new IItemHandler() {

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (!Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName).anyMatch(s -> s.startsWith("ingot")))
					return stack;
				return back.insertItem(slot, stack, simulate);
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				return back.getStackInSlot(slot);
			}

			@Override
			public int getSlots() {
				return back.getSlots();
			}

			@Override
			public int getSlotLimit(int slot) {
				return back.getSlotLimit(slot);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (!getStackInSlot(Math.min(slot + 1, getSlots() - 1)).isEmpty())
					return ItemStack.EMPTY;
				return back.extractItem(slot, amount, simulate);
			}
		};
		ItemStackHandler back = new ItemStackHandler(TheMod.perX * TheMod.perY * TheMod.perZ) {
			@Override
			protected void onContentsChanged(int slot) {
				markForSync();
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			super.onDataPacket(net, pkt);
			if (world != null && world.isRemote)
				world.markBlockRangeForRenderUpdate(pos, pos);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			NBTHelper.getSafe(compound, "handler", NBTTagCompound.class).ifPresent(n -> back.deserializeNBT(n));
			super.readFromNBT(compound);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			NBTHelper.set(compound, "handler", back.serializeNBT());
			return super.writeToNBT(compound);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return (T) handler;
			return super.getCapability(capability, facing);
		}

		@Override
		public List<ItemStack> getDroppingItems() {
			return IntStream.range(0, handler.getSlots()).mapToObj(handler::getStackInSlot).collect(Collectors.toList());
		}
	}
}
