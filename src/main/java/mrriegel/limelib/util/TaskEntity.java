package mrriegel.limelib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class TaskEntity extends Entity {

	public NBTTagCompound nbt;

	public TaskEntity(World worldIn) {
		super(worldIn);
		this.setSize(.5F, .5F);
		this.noClip = true;
		this.isImmuneToFire = true;
		this.firstUpdate = false;
		nbt = new NBTTagCompound();
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		nbt = compound.getCompoundTag("DATA");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setTag("DATA", nbt);
	}

	@Override
	public boolean func_189652_ae() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void onKillCommand() {
		isDead = true;
	}

	@Override
	protected void kill() {
	}

	@Override
	public void setDead() {
	}

	@Override
	public boolean isInvisible() {
		return !true;
	}

	@Override
	public boolean isSilent() {
		return true;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void doBlockCollisions() {
	}

	@Override
	public Entity changeDimension(int dimensionIn) {
		return this;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return true;
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
	}

	public boolean canBeHitWithPotion() {
		return false;
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	protected void setBeenAttacked() {
	}

	protected abstract boolean canRun();

	protected abstract void run();

	protected abstract boolean done();

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (canRun())
			run();
		if (done())
			isDead = true;
	}

}
