package mrriegel.limelib.particle;

import java.util.Random;

import mrriegel.limelib.helper.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;

public class CommonParticle extends Particle {

	protected double flouncing = 0;

	public CommonParticle(double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(Minecraft.getMinecraft().theWorld, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.motionX = xSpeedIn;
		this.motionY = ySpeedIn;
		this.motionZ = zSpeedIn;
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
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
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

}
