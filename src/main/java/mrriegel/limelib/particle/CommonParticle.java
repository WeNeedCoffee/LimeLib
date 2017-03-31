package mrriegel.limelib.particle;

import java.awt.Color;
import java.util.Random;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.util.vector.Vector4f;

public class CommonParticle extends Particle {

	protected double flouncing = 0;
	protected int visibleRange = 32, brightness = -1;
	protected boolean depth = true, smoothEnd = true;
	protected IColorCall colors;

	public CommonParticle(double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
		super(LimeLib.proxy.getClientWorld(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
		this.motionX = xSpeed;
		this.motionY = ySpeed;
		this.motionZ = zSpeed;
		setTexture(ParticleHelper.roundParticle);
		colors = (CommonParticle par) -> new Vector4f(par.particleRed, par.particleGreen, par.particleBlue, par.particleAlpha);
	}

	public CommonParticle(double xCoord, double yCoord, double zCoord) {
		this(xCoord, yCoord, zCoord, 0, 0, 0);
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionY -= 0.04D * this.particleGravity;
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX += (this.rand.nextDouble() - .5) * flouncing;
		this.motionY += (this.rand.nextDouble() - .5) * flouncing;
		this.motionZ += (this.rand.nextDouble() - .5) * flouncing;

		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
		if (smoothEnd) {
			float percent = (float) particleAge / (float) particleMaxAge;
			if (percent > .7F) {
				particleAlpha /= (2F - percent);
			}
		}
		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}
	}

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		Vector4f color = colors.getColor(this);
		this.particleRed = color.x;
		this.particleGreen = color.y;
		this.particleBlue = color.z;
		this.particleAlpha = color.w;
		if (entityIn.getDistance(posX, posY, posZ) <= visibleRange) {
			if (depth)
				super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
			else {
				Tessellator.getInstance().draw();
				GlStateManager.pushMatrix();
				GlStateManager.disableDepth();
				worldRendererIn.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
				super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
				Tessellator.getInstance().draw();
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();
				worldRendererIn.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			}
		}
	}

	@Override
	public int getBrightnessForRender(float p_189214_1_) {
		if (brightness < 0)
			return super.getBrightnessForRender(p_189214_1_);
		int i = MathHelper.clamp(brightness, 0, 15);
		int j = i;
		return i << 20 | j << 4;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	public CommonParticle setColor(int color, int diff) {
		diff = MathHelper.clamp(diff, 0, 255);
		Random random = new Random();
		if (diff > 0) {
			int red = MathHelper.clamp(ColorHelper.getRed(color) + MathHelper.getInt(random, -diff, diff), 0, 255);
			int green = MathHelper.clamp(ColorHelper.getGreen(color) + MathHelper.getInt(random, -diff, diff), 0, 255);
			int blue = MathHelper.clamp(ColorHelper.getBlue(color) + MathHelper.getInt(random, -diff, diff), 0, 255);
			color = new Color(red, green, blue, ColorHelper.getAlpha(color)).getRGB();
		}
		this.particleRed = ColorHelper.getRed(color) / 255f;
		this.particleGreen = ColorHelper.getGreen(color) / 255f;
		this.particleBlue = ColorHelper.getBlue(color) / 255f;
		this.particleAlpha = ColorHelper.getAlpha(color) / 255f;
		return this;
	}

	public CommonParticle setFlouncing(double flouncing) {
		this.flouncing = flouncing;
		return this;
	}

	public CommonParticle setMaxAge2(int ticks) {
		this.particleMaxAge = ticks;
		return this;
	}

	public CommonParticle setTexture(ResourceLocation texture) {
		setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString()));
		return this;
	}

	public CommonParticle setScale(float scale) {
		this.particleScale = scale;
		return this;
	}

	public CommonParticle setGravity(float gravity) {
		this.particleGravity = gravity;
		return this;
	}

	public CommonParticle setNoClip(boolean noClip) {
		this.canCollide = !noClip;
		return this;
	}

	public CommonParticle setVisibleRange(int visibleRange) {
		this.visibleRange = visibleRange;
		return this;
	}

	public CommonParticle setBrightness(int brightness) {
		this.brightness = brightness;
		return this;
	}

	public CommonParticle setDepth(boolean depth) {
		this.depth = depth;
		return this;
	}

	public CommonParticle setSmoothEnd(boolean smoothEnd) {
		this.smoothEnd = smoothEnd;
		return this;
	}

	public CommonParticle setColors(IColorCall colors) {
		this.colors = colors;
		return this;
	}

	public static interface IColorCall {
		public Vector4f getColor(CommonParticle par);
	}

}
