package mrriegel.testmod;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.block.CommonBlock;
import mrriegel.limelib.datapart.DataPart;
import mrriegel.limelib.datapart.DataPartRegistry;
import mrriegel.limelib.datapart.RenderRegistry;
import mrriegel.limelib.helper.ParticleHelper;
import mrriegel.limelib.item.CommonItem;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.particle.CommonParticle;
import mrriegel.limelib.recipe.RecipeItemHandler;
import mrriegel.limelib.tile.CommonTileInventory;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import com.google.common.collect.Lists;

//@Mod(modid = "lalal", name = "kohle", version = "${version}")
public class TestMod implements IGuiHandler {

	@Mod.Instance("lalal")
	public static TestMod mod;

	public static final CommonBlock block = new TestBlock();
	public static final CommonItem item = new TestItem();
	public static Fluid alcohol;
	public static Block alcoholBlock;

	//	public TestBook book = new TestBook();

	public static final boolean ENABLE = !false;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (!ENABLE)
			return;
		//		block.registerBlock();
		//		block.initModel();
		//		item.registerItem();
		//		item.initModel();
		Block bb = new BB();
		GameRegistry.register(bb);
		GameRegistry.register(new ItemBlock(bb).setRegistryName(bb.getRegistryName()));
		GameRegistry.registerTileEntity(BB.TT.class, "tt");
		alcohol = new Fluid("alcohol", new ResourceLocation("lalal", "fluid/alcohol_still"), new ResourceLocation("lalal", "fluid/alcohol_flowing"));
		//		FluidRegistry.registerFluid(alcohol);
		FluidRegistry.addBucketForFluid(alcohol);
		alcoholBlock = new BlockFluidClassic(alcohol, Material.WATER);
		alcoholBlock.setRegistryName("alcohol");
		alcoholBlock.setUnlocalizedName(alcoholBlock.getRegistryName().toString());
		if (LimeLib.proxy.getSide().isClient())
			ModelLoader.setCustomStateMapper(alcoholBlock, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
		//		GameRegistry.register(alcoholBlock);
		//		Part part = new Part();
		//		part.setRegistryName("party");
		//		GameRegistry.register(part);
	}

	static class BB extends Block implements ITileEntityProvider {

		public BB() {
			super(Material.CLAY);
			setRegistryName("suppe");
			setUnlocalizedName(getRegistryName().toString());
		}

		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new TT();
		}

		@Override
		public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
			System.out.println("sieb");
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}

		static class TT extends TileEntity {

			public Item item;
			public int ticksRemaining;

			@Override
			public NBTTagCompound writeToNBT(NBTTagCompound compound) {
				super.writeToNBT(compound);

				NBTTagCompound stackTag = new NBTTagCompound();
				(item == null ? ItemStack.EMPTY : new ItemStack(item)).writeToNBT(stackTag);

				compound.setTag("ItemStack", stackTag);
				compound.setInteger("TicksRemaining", ticksRemaining);

				return compound;
			}

			@Override
			public void readFromNBT(NBTTagCompound compound) {
				super.readFromNBT(compound);

				ItemStack stack = new ItemStack((NBTTagCompound) compound.getTag("ItemStack"));
				if (stack.isEmpty())
					item = null;
				else
					item = stack.getItem();
				ticksRemaining = compound.getInteger("TicksRemaining");
			}

			@Override
			@Nullable
			public SPacketUpdateTileEntity getUpdatePacket() {
				return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
			}

			@Override
			public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
				this.readFromNBT(pkt.getNbtCompound());
			}

			@Override
			public NBTTagCompound getUpdateTag() {
				return this.writeToNBT(new NBTTagCompound());
			}
		}

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (!ENABLE)
			return;
		MinecraftForge.EVENT_BUS.register(this);
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, this);
		PacketHandler.registerMessage(TestMessage.class, Side.CLIENT);
		DataPartRegistry.register("theo", TestPart.class);
		RenderRegistry.register(TestPart.class, new RenderRegistry.RenderDataPart<TestPart>() {
			@Override
			public void render(TestPart part, double x, double y, double z, float partialTicks) {
				ItemStack inputStack = new ItemStack(Items.DIAMOND_PICKAXE);

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
				GlStateManager.translate(0.5, 1.5, 0.5);
				EntityItem entityitem = new EntityItem(part.getWorld(), 0.0D, 0.0D, 0.0D, inputStack);
				entityitem.getEntityItem().setCount(1);
				entityitem.hoverStart = 0.0F;
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();

				float rotation = (float) (4720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

				GlStateManager.rotate(rotation, 0.0F, 1.0F, 0);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GlStateManager.pushAttrib();
				RenderHelper.enableStandardItemLighting();
				itemRenderer.renderItem(entityitem.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popAttrib();

				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
				GlStateManager.popMatrix();
			}
		});
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	class R extends RecipeItemHandler {

		public R(NonNullList<ItemStack> output, boolean order, Object... input) {
			super(output, order, input);
		}

		@Override
		protected NonNullList<ItemStack> getIngredients(IItemHandler object) {
			NonNullList<ItemStack> hotbar = NonNullList.create();
			for (int i = 0; i < 9; i++)
				hotbar.add(((PlayerMainInvWrapper) object).getInventoryPlayer().getStackInSlot(i));
			hotbar.removeAll(Collections.singleton(null));
			return hotbar;
		}

	}

	@SubscribeEvent
	public void jump(LivingJumpEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntityLiving();
			ItemStack held = player.getHeldItemMainhand();
			if (held != null)
				held.canEditBlocks();
			//			book.init();
			//			if (!player.world.isRemote && !player.isSneaking()) {
			//				if (!held.isEmpty()) {
			//					book.openGuiAt(held.getItem(), true);
			//				}
			//			}
			if (!player.world.isRemote) {
				PlayerMainInvWrapper pmiw = new PlayerMainInvWrapper(player.inventory);
				NonNullList<ItemStack> nnl = NonNullList.create();
				nnl.addAll(Lists.newArrayList(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.IRON_INGOT, 4)));
				R r = new R(nnl, true, Items.APPLE, Blocks.COAL_BLOCK, new ItemStack(Blocks.BOOKSHELF));
				if (r.match(pmiw) && r.removeIngredients(pmiw, false)) {
					for (ItemStack s : r.getResult(pmiw))
						ItemHandlerHelper.insertItemStacked(pmiw, s.copy(), false);

				}
				//			if (!player.world.isRemote)
				//				PacketHandler.sendTo(new TestMessage(player.getEntityData()), (EntityPlayerMP) player);

			} else {
			}
			//			DataPartRegistry r = DataPartRegistry.get(player.world);
			//			if (r != null) {
			//				System.out.println(r.getParts());
			//			}
			if (!"".isEmpty()) {
				BlockPos p = new BlockPos(-108, 72, 234);
				DataPartRegistry reg = DataPartRegistry.get(player.world);
				if (reg != null) {
					DataPart dp = reg.getDataPart(p);
					if (dp == null) {
						TestPart part = new TestPart();
						if (!player.world.isRemote) {
							reg.addDataPart(p, part, false);
							player.sendMessage(new TextComponentString("start quarry"));
						}
					} else {
						if (!player.world.isRemote) {
							reg.removeDataPart(p);
							player.sendMessage(new TextComponentString("stop"));
						}
						//						TestPart part = (TestPart) col.stream().collect(Collectors.toList()).get(0);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void update(final LivingUpdateEvent e) {
		if (!(e.getEntity() instanceof EntityPlayer) && e.getEntity().world.isRemote && (e.getEntity().world.getTotalWorldTime() + new BlockPos(e.getEntity()).hashCode()) % 10 == 0 && !GuiScreen.isCtrlKeyDown()) {
			BlockPos pos = new BlockPos(e.getEntity());
			int range = 8;
			List<EntityLivingBase> lis = e.getEntity().world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)), input -> e.getEntity().getClass().equals(input.getClass()));
			lis.remove(e.getEntity());
			for (EntityLivingBase ent : lis)
				for (Vec3d v : ParticleHelper.getVecsForLine(e.getEntity().posX, e.getEntity().posY + e.getEntity().height - .1, e.getEntity().posZ, ent.posX, ent.posY + ent.height - .1, ent.posZ, 3))
					LimeLib.proxy.renderParticle(new CommonParticle(v.xCoord, v.yCoord, v.zCoord).setMaxAge2(10).setTexture(ParticleHelper.sparkleParticle));
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0)
			return new TestContainer(player.inventory, (CommonTileInventory) world.getTileEntity(new BlockPos(x, y, z)));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0)
			return new TestGui(new TestContainer(player.inventory, (CommonTileInventory) world.getTileEntity(new BlockPos(x, y, z))));
		else if (ID == 1)
			//			if (x == -1)
			//				return new GuiBook(book);
			//			else {
			//				return new GuiBook(book, x, y);
			//			}
			;
		return null;
	}

	public static class Part extends Impl<Part> {
		public static IForgeRegistry<Part> PARTREGISTRY = new RegistryBuilder<Part>().setName(new ResourceLocation("lalal" + ":partss")).setType(Part.class).create();

	}
}
