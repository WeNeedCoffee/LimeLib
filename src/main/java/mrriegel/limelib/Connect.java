package mrriegel.limelib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.lwjgl.util.vector.Vector3f;

import mrriegel.limelib.block.CommonBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
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
		if (!false)
			return;
		con.registerBlock();
		con.initModel();
		ModelLoaderRegistry.registerLoader(new Loader());
		ModelLoader.setCustomStateMapper(con, new StateMapperBase() {

			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(con.getRegistryName().toString());
			}
		});
	}

	public static void init() {
	}

	static class Bake implements IBakedModel {

		TextureAtlasSprite zero, one, twos, two_c, two_s, three, four;
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
						u = zero.getInterpolatedU(u);
						v = zero.getInterpolatedV(v);
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

			//			//north
			//			UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
			//			builder.setTexture(tas);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 0f, 16, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 0f, 16, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 0f, 0, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 0f, 0, 16);
			//			quads.add(builder.build());
			//			//west
			//			builder = new UnpackedBakedQuad.Builder(format);
			//			builder.setTexture(tas);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 1f, 16, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 1f, 16, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 0f, 0, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 0f, 0, 16);
			//			quads.add(builder.build());
			//			//south
			//			builder = new UnpackedBakedQuad.Builder(format);
			//			builder.setTexture(tas);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 1f, 16, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 1f, 16, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 1f, 0, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 1f, 0, 16);
			//			quads.add(builder.build());
			//			//east
			//			builder = new UnpackedBakedQuad.Builder(format);
			//			builder.setTexture(tas);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 0f, 16, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 0f, 16, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 1f, 0, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 1f, 0, 16);
			//			quads.add(builder.build());
			//			//top
			//			builder = new UnpackedBakedQuad.Builder(format);
			//			builder.setTexture(tas);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 1f, 0, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 1f, 16, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 1f, 0f, 16, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 1f, 0f, 0, 0);
			//			quads.add(builder.build());
			//			//bottom
			//			builder = new UnpackedBakedQuad.Builder(format);
			//			builder.setTexture(tas);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 0f, 0, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 0f, 16, 16);
			//			putVertex(builder, new Vec3d(0, 0, 0), 1f, 0f, 1f, 16, 0);
			//			putVertex(builder, new Vec3d(0, 0, 0), 0f, 0f, 1f, 0, 0);
			//			quads.add(builder.build());
			//			tas = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.getDefaultState()).getParticleTexture();
			if (side != null)
				return Collections.emptyList();
			boolean up = state.getValue(UP), down = state.getValue(DOWN), north = state.getValue(NORTH), east = state.getValue(EAST), south = state.getValue(SOUTH), west = state.getValue(WEST);
			//north
			TextureAtlasSprite tex = null;
			quads.add(quad(EnumFacing.NORTH, new Vector3f(0f, 16f, 0f), new Vector3f(16f, 0f, 0f), 0, null, zero));
			//east
			quads.add(quad(EnumFacing.EAST, new Vector3f(16f, 16f, 0f), new Vector3f(16f, 0f, 16f), 0, null, one));
			//south
			quads.add(quad(EnumFacing.SOUTH, new Vector3f(0f, 16f, 16f), new Vector3f(16f, 0f, 16f), 0, null, two_s));
			//west
			quads.add(quad(EnumFacing.WEST, new Vector3f(0f, 16f, 0f), new Vector3f(0f, 0f, 16f), 0, null, two_c));
			//top
			quads.add(quad(EnumFacing.UP, new Vector3f(16f, 16f, 0f), new Vector3f(0f, 16f, 16f), 0, null, three));
			//bottom
			quads.add(quad(EnumFacing.DOWN, new Vector3f(16f, 0f, 0f), new Vector3f(0f, 0f, 16f), 0, null, four));
			return quads;
		}

		BakedQuad quad(EnumFacing face, Vector3f from, Vector3f to, int rotation, Axis mirror, TextureAtlasSprite tex) {
			FaceBakery fb = new FaceBakery();
			int[] ar = new int[] { 16, 16, 0, 0 };
			if (mirror != null) {
				switch (face.getAxis()) {
				case X:
					if (mirror == Axis.Z)
						ar = new int[] { 16, 0, 0, 16 };//z
					else if (mirror == Axis.Y)
						ar = new int[] { 0, 16, 16, 0 };//y
					break;
				case Y:
					if (mirror == Axis.X)
						ar = new int[] { 16, 0, 0, 16 };//x
					else if (mirror == Axis.Z)
						ar = new int[] { 0, 16, 16, 0 };//z
					break;
				case Z:
					if (mirror == Axis.X)
						ar = new int[] { 16, 0, 0, 16 };//x
					else if (mirror == Axis.Y)
						ar = new int[] { 0, 16, 16, 0 };//y
					break;
				}
			}
			return fb.makeBakedQuad(from, to, new BlockPartFace(null, -1, null, new BlockFaceUV(new float[] { ar[0], ar[1], ar[2], ar[3] }, rotation)), tex, face.getOpposite(), ModelRotation.X0_Y0, null, false, true);
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
			return zero;
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
			System.out.println("zip: " + format + " " + format.getElements());
			for (Field f : DefaultVertexFormats.class.getDeclaredFields()) {
				try {
					if (format == f.get(null))
						System.out.println(f.getName());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			b.zero = bakedTextureGetter.apply(new ResourceLocation(LimeLib.MODID, "blocks/zero"));
			b.one = bakedTextureGetter.apply(new ResourceLocation(LimeLib.MODID, "blocks/one"));
			b.two_c = bakedTextureGetter.apply(new ResourceLocation(LimeLib.MODID, "blocks/two_c"));
			b.two_s = bakedTextureGetter.apply(new ResourceLocation(LimeLib.MODID, "blocks/two_s"));
			b.three = bakedTextureGetter.apply(new ResourceLocation(LimeLib.MODID, "blocks/three"));
			b.four = bakedTextureGetter.apply(new ResourceLocation(LimeLib.MODID, "blocks/four"));
			b.format = format;
			return b;
		}

		@Override
		public Collection<ResourceLocation> getTextures() {
			return Collections.emptyList();
		}

		@Override
		public IModelState getDefaultState() {
			return IModel.super.getDefaultState();
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
