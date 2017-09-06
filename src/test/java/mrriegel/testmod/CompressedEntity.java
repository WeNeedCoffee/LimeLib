package mrriegel.testmod;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class CompressedEntity<T extends EntityLiving> extends EntityLiving{

	public CompressedEntity(World worldIn, T entity) {
		super(worldIn);
	}

	
}
