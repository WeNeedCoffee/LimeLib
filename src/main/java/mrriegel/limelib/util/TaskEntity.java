package mrriegel.limelib.util;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public abstract class TaskEntity extends Entity {

	protected static final DataParameter<NBTTagCompound> DATA = EntityDataManager.createKey(TaskEntity.class, new DataSerializer<NBTTagCompound>() {

		@Override
		public void write(PacketBuffer buf, NBTTagCompound value) {
			ByteBufUtils.writeTag(buf, value);
		}

		@Override
		public NBTTagCompound read(PacketBuffer buf) throws IOException {
			return ByteBufUtils.readTag(buf);
		}

		@Override
		public DataParameter<NBTTagCompound> createKey(int id) {
			return new DataParameter<NBTTagCompound>(id, this);
		}
	});

	public TaskEntity(World worldIn) {
		super(worldIn);
		this.setSize(.5F, .5F);
		this.noClip = true;
		this.isImmuneToFire = true;
		this.firstUpdate = false;
	}

	public NBTTagCompound getData() {
		return this.dataManager.get(DATA);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(DATA, new NBTTagCompound());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.dataManager.set(DATA, compound.getCompoundTag("DATA"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setTag("DATA", this.dataManager.get(DATA));
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
