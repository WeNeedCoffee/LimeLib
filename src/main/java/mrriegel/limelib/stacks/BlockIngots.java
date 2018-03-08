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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(modid = TheMod.id)
public class BlockIngots extends CommonBlockContainer<CommonTile> {
	public static final IUnlistedProperty<TileIngots> prop = new IUnlistedProperty<BlockIngots.TileIngots>() {

		@Override
		public String getName() {
			return "tile";
		}

		@Override
		public boolean isValid(TileIngots value) {
			return true;
		}

		@Override
		public Class<TileIngots> getType() {
			return TileIngots.class;
		}

		@Override
		public String valueToString(TileIngots value) {
			return value.toString();
		}
	};

	//	public static final IUnlistedProperty<Long> prop = new IUnlistedProperty<Long>() {
	//
	//		@Override
	//		public String getName() {
	//			return "tile";
	//		}
	//
	//		@Override
	//		public boolean isValid(Long value) {
	//			return true;
	//		}
	//
	//		@Override
	//		public Class<Long> getType() {
	//			return Long.class;
	//		}
	//
	//		@Override
	//		public String valueToString(Long value) {
	//			return value + "";
	//		}
	//
	//	};

	public BlockIngots() {
		super(Material.IRON, "ingots");
		setHardness(4.5f);
		setDefaultState(((IExtendedBlockState) getDefaultState()).withProperty(prop, null));
	}

	@Override
	protected Class<? extends CommonTile> getTile() {
		return TileIngots.class;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntity t = source.getTileEntity(pos);
		if (t instanceof TileIngots) {
			return ((TileIngots) t).getBox();
		} else
			return FULL_BLOCK_AABB;
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
		return ((IExtendedBlockState) state).withProperty(prop, (TileIngots) world.getTileEntity(pos));
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
				playerIn.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemHandlerHelper.insertItemStacked(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), playerIn.getHeldItem(hand), false));
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@SubscribeEvent
	public static void leftclick(LeftClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		TileEntity tile = event.getWorld().getTileEntity(event.getPos());
		if (tile instanceof TileIngots && !(player.getHeldItemMainhand().getItem() instanceof ItemTool)) {
			IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (player.world.isRemote) {
				event.setCanceled(true);
				event.setResult(Result.DENY);
				event.setUseBlock(Result.DENY);
				return;
			} else {
				for (int i = handler.getSlots() - 1; i >= 0; i--) {
					ItemStack s = handler.getStackInSlot(i);
					if (!(s = handler.extractItem(i, 64, false)).isEmpty()) {
						EntityItem ei = new EntityItem(player.world, event.getPos().offset(event.getFace()).getX() + .5, event.getPos().getY() + .3, event.getPos().offset(event.getFace()).getZ() + .5, s);
						player.world.spawnEntity(ei);
						if (ItemHandlerHelper.insertItem(new PlayerMainInvWrapper(player.inventory), ei.getItem(), true).isEmpty()) {
							Vec3d vec = new Vec3d(player.posX - ei.posX, player.posY + .5 - ei.posY, player.posZ - ei.posZ).normalize().scale(1.5);
							ei.motionX = vec.x;
							ei.motionY = vec.y;
							ei.motionZ = vec.z;
						}
						event.setCanceled(true);
						event.setResult(Result.DENY);
						event.setUseBlock(Result.DENY);
						return;
					}
				}
			}
		}
	}

	public static class TileIngots extends CommonTile {
		IItemHandler handler = new IItemHandler() {

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
				if (!getStackInSlot(Math.min(slot + 1, getSlots() - 1)).isEmpty() && slot != getSlots() - 1)
					return ItemStack.EMPTY;
				return back.extractItem(slot, amount, simulate);
			}
		};
		private ItemStackHandler back = new ItemStackHandler(TheMod.perX * TheMod.perY * TheMod.perZ) {
			@Override
			protected void onContentsChanged(int slot) {
				markForSync();
				box = null;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

		};
		private AxisAlignedBB box = null;
		/** client only */
		public boolean changed = true;

		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			super.onDataPacket(net, pkt);
			changed = true;
			box = null;
			if (world != null && world.isRemote)
				world.markBlockRangeForRenderUpdate(pos, pos);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			NBTHelper.getSafe(compound, "handler", NBTTagCompound.class).ifPresent(n -> {
				n.removeTag("Size");
				back.deserializeNBT(n);
			});
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
		public boolean canRenderBreaking() {
			return !false;
		}

		public AxisAlignedBB getBox() {
			if (box != null)
				return box;
			double yy = 1d / TheMod.perY;
			int count = 0;
			boolean an = false;
			for (int i = 0; i < handler.getSlots(); i++) {
				if (handler.getStackInSlot(i).isEmpty()) {
					count = i;
					an = true;
					break;
				}
			}
			if (!an)
				count = handler.getSlots();
			int heigh = (int) Math.ceil((double) count / (TheMod.perX * TheMod.perZ));
			if (heigh == 0)
				heigh = 1;
			return box = new AxisAlignedBB(0, 0, 0, 1, heigh * yy, 1);
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
