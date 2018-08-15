package mrriegel.limelib.helper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;

public class BakedQuadBuilder {
	private final VertexFormat format;
	private final TextureAtlasSprite sprite;
	private int vertCount = 0;
	private Vertex[] vertices = new Vertex[4];
	private int tint = -1;
	private EnumFacing face;
	private boolean diffuse;

	public BakedQuadBuilder(VertexFormat format, TextureAtlasSprite sprite) {
		this.format = Objects.requireNonNull(format);
		this.sprite = Objects.requireNonNull(sprite);
	}

	public BakedQuad build() {
		Validate.isTrue(vertCount == 4, "too few vertices");
		ByteBuffer bb = ByteBuffer.allocate(format.getSize() * 4);
		Vector3f normal = Vector3f.cross(Vector3f.sub(vertices[2].pos, vertices[1].pos, null), Vector3f.sub(vertices[0].pos, vertices[1].pos, null), null).normalise(null);
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = vertices[i];
			for (VertexFormatElement element : format.getElements()) {
				switch (element.getUsage()) {
				case COLOR:
					if (element.getSize() == 4)
						bb.putInt(v.color);
					else
						nope();
					break;
				case NORMAL:
					if (element.getSize() == 3) {
						int x = ((byte) Math.round(normal.x * 127)) & 0xFF;
						int y = ((byte) Math.round(normal.y * 127)) & 0xFF;
						int z = ((byte) Math.round(normal.z * 127)) & 0xFF;
						int normalI = x | (y << 0x08) | (z << 0x10);
						ByteBuffer bb2 = ByteBuffer.allocate(4);
						bb2.putInt(normalI);
						byte[] bs = Arrays.copyOfRange(bb2.array(), 1, 4);
						for (int j = 0; j < bs.length; j++)
							bb.put(bs[j]);
					} else
						nope();
					break;
				case PADDING:
					bb.put(v.padding);
					break;
				case POSITION:
					bb.putFloat(v.pos.x);
					bb.putFloat(v.pos.y);
					bb.putFloat(v.pos.z);
					break;
				case UV:
					if (element.getIndex() == 0) {
						bb.putFloat(sprite.getInterpolatedU(v.uf));
						bb.putFloat(sprite.getInterpolatedV(v.vf));
					} else {
						bb.putShort(v.us);
						bb.putShort(v.vs);
					}
					break;
				default:
					break;
				}
			}
		}
		bb.clear();
		IntBuffer ib = bb.asIntBuffer();
		int[] ints = new int[ib.remaining()];
		ib.get(ints);
		return new BakedQuad(ints, tint, face, Objects.requireNonNull(sprite, "texture required"), diffuse, format);
	}

	private static void nope() {
		throw new RuntimeException("nope");
	}

	public BakedQuadBuilder addVertex(Vertex vertex) {
		Validate.isTrue(vertCount < 4, "too many vertices");
		vertices[vertCount++] = vertex;
		return this;
	}

	public BakedQuadBuilder setQuadTint(int tint) {
		this.tint = tint;
		return this;
	}

	public BakedQuadBuilder setQuadOrientation(EnumFacing orientation) {
		this.face = orientation;
		return this;
	}

	public BakedQuadBuilder setApplyDiffuseLighting(boolean diffuse) {
		this.diffuse = diffuse;
		return this;
	}

	public static class Vertex {
		int color = -1;
		Vector3f pos;
		short us, vs;
		float uf, vf;
		byte padding;

		public static class Builder {
			private Vertex vertex = new Vertex();

			public Builder setColor(int color) {
				vertex.color = color;
				return this;
			}

			public Builder setPos(Vector3f pos) {
				vertex.pos = pos;
				return this;
			}

			public Builder setUVshort(short u, short v) {
				vertex.us = u;
				vertex.vs = v;
				return this;
			}

			public Builder setUVfloat(float u, float v) {
				vertex.uf = u;
				vertex.vf = v;
				return this;
			}

			public Builder setPadding(byte padding) {
				vertex.padding = padding;
				return this;
			}

			public Vertex build() {
				
				Vertex v = new Vertex();
				v.color = vertex.color;
				v.pos = Objects.requireNonNull(vertex.pos, "pos required");
				v.us = vertex.us;
				v.vs = vertex.vs;
				v.uf = vertex.uf;
				v.vf = vertex.vf;
				v.padding = vertex.padding;
				return v;
			}
		}

	}

}
