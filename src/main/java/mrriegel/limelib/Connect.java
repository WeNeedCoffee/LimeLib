package mrriegel.limelib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import mrriegel.limelib.block.CommonBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;

public class Connect {
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");

	public static final CommonBlock con = new CommonBlock(Material.ROCK, "connector") {

		{
			setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
			setDefaultState(blockState.getBaseState().withProperty(UP, false).withProperty(DOWN, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
		}

		@Override
		public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
			return true;
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return 0;
		}

		@Override
		protected BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, new IProperty[] { UP, DOWN, NORTH, EAST, WEST, SOUTH });
		}

		@Override
		public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
			return state.//
			withProperty(UP, worldIn.getBlockState(pos.up()).getBlock() == this).//
			withProperty(DOWN, worldIn.getBlockState(pos.down()).getBlock() == this).//
			withProperty(NORTH, worldIn.getBlockState(pos.north()).getBlock() == this).//
			withProperty(EAST, worldIn.getBlockState(pos.east()).getBlock() == this).//
			withProperty(SOUTH, worldIn.getBlockState(pos.south()).getBlock() == this).//
			withProperty(WEST, worldIn.getBlockState(pos.west()).getBlock() == this);
		}
	};

	public static void preInit() {
		con.registerBlock();
		con.initModel();
		ModelLoaderRegistry.registerLoader(new Loader());
		ModelLoader.setCustomStateMapper(con, new StateMapperBase() {

			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(con.getRegistryName().toString());
			}
		});
		//		ModelLoader.setCustomModelResourceLocation(con.getItemBlock(), 0, new ModelResourceLocation(con.getRegistryName(), "inventory"));
	}

	public static void init() {
	}

	static class Bake implements IBakedModel {

		static ResourceLocation tex = new ResourceLocation(LimeLib.MODID, "blocks/four");
		TextureAtlasSprite tas;
		VertexFormat format;

		private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, float x, float y, float z, float u, float v) {
			for (int e = 0; e < format.getElementCount(); e++) {
				switch (format.getElement(e).getUsage()) {
				case POSITION:
					builder.put(e, x, y, z, 1.0f);
					break;
				case COLOR:
					builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
					break;
				case UV:
					if (format.getElement(e).getIndex() == 0) {
						u = tas.getInterpolatedU(u);
						v = tas.getInterpolatedV(v);
						builder.put(e, u, v, 0f, 1f);
						break;
					}
				case NORMAL:
					builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
					break;
				default:
					builder.put(e);
					break;
				}
			}
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList<>();

			tas = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.BRICK_BLOCK.getDefaultState()).getParticleTexture();
			//			if ("".isEmpty())
			//				return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.CRAFTING_TABLE.getDefaultState()).getQuads(state, side, rand);
			//north
			UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
			builder.setTexture(tas);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 0f, 16, 16);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 0f, 16, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 0f, 0, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 0f, 0, 16);
			quads.add(builder.build());
			//west
			builder = new UnpackedBakedQuad.Builder(format);
			builder.setTexture(tas);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 1f, 16, 16);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 1f, 16, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 0f, 0, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 0f, 0, 16);
			quads.add(builder.build());
			//south
			builder = new UnpackedBakedQuad.Builder(format);
			builder.setTexture(tas);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 1f, 16, 16);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 1f, 16, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 1f, 0, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 1f, 0, 16);
			quads.add(builder.build());
			//east
			builder = new UnpackedBakedQuad.Builder(format);
			builder.setTexture(tas);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 0f, 16, 16);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 0f, 16, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 1f, 0, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 1f, 0, 16);
			quads.add(builder.build());
			quads.clear();
			//top
			FaceBakery fb = new FaceBakery();
			if ("".isEmpty()) {
				quads.add(fb.makeBakedQuad(new Vector3f(0f, 16f, 16f), new Vector3f(16f, 16f, 0f), new BlockPartFace(null, -1, tex.toString(), new BlockFaceUV(new float[] { 16, 16, 0, 0 }, 0)), tas, EnumFacing.DOWN, ModelRotation.X0_Y0, null, true, true));
			} else {
				builder = new UnpackedBakedQuad.Builder(format);
				builder.setTexture(tas);
				putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 1f, 0, 16);
				putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 1f, 16, 16);
				putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 0f, 16, 0);
				putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 0f, 0, 0);
				quads.add(builder.build());
			}
			//bottom
			builder = new UnpackedBakedQuad.Builder(format);
			builder.setTexture(tas);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 0f, 0, 16);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 0f, 16, 16);
			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 1f, 16, 0);
			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 1f, 0, 0);
			quads.add(builder.build());

			//			quads = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.STONE_BUTTON.getDefaultState()).getQuads(state, side, rand);
			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return tas;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}

	}

	static class Mod implements IModel {

		@Override
		public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			Bake b = new Bake();
			b.tas = bakedTextureGetter.apply(Bake.tex);
			b.format = format;
			return b;
		}

		@Override
		public Collection<ResourceLocation> getTextures() {
			return Lists.newArrayList(Bake.tex);
		}

		@Override
		public IModelState getDefaultState() {
			return IModel.super.getDefaultState();
			//			return null;
		}

	}

	static class Loader implements ICustomModelLoader {

		final Mod mod = new Mod();

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return con.getRegistryName().equals(modelLocation);
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception {
			return new Mod();
		}

	}
}
