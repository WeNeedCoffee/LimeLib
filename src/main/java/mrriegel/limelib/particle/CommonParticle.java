package mrriegel.limelib.particle;

import java.util.Random;

import javax.vecmath.Tuple4f;

import mrriegel.limelib.LimeLib;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CommonParticle extends Particle {

	protected double flouncing = 0;
	protected int visibleRange = 32;

	public CommonParticle(double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(LimeLib.proxy.getClientWorld(), xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.motionX = xSpeedIn;
		this.motionY = ySpeedIn;
		this.motionZ = zSpeedIn;
		setTexture(ParticleHelper.roundParticle);
	}

	public CommonParticle(double posXIn, double posYIn, double posZIn) {
		this(posXIn, posYIn, posZIn, 0, 0, 0);
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

		if (this.isCollided) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}
	}

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		Tuple4f color = getColor();
		this.particleRed = color.x;
		this.particleGreen = color.y;
		this.particleBlue = color.z;
		this.particleAlpha = color.w;
		if (entityIn.getDistance(posX, posY, posZ) <= visibleRange)
			super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}

	public CommonParticle setColor(int color, int diff) {
		if (diff > 0) {
			color += new Random().nextInt(diff) - (diff / 2f);

		}
		color &= 0xffffff;
		this.particleRed = ColorHelper.getRed(color) / 255f;
		this.particleGreen = ColorHelper.getGreen(color) / 255f;
		this.particleBlue = ColorHelper.getBlue(color) / 255f;
		this.particleAlpha = ColorHelper.getAlpha(color) / 255f;
		return this;
	}

	@SuppressWarnings("serial")
	protected Tuple4f getColor() {
		return new Tuple4f(particleRed, particleGreen, particleBlue, particleAlpha) {
		};
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

	// public CommonParticle disableDepth() {
	// this.depth = false;
	// return this;
	// }

}
