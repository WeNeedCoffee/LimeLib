package mrriegel.limelib.book;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

public class SubChapter {
	protected String text;
	protected List<ItemStack> stacks = Lists.newArrayList();
	protected String name;
	protected int index;

	public SubChapter(String name) {
		this.name = name;
	}

	public SubChapter(String name, ItemStack... stacks) {
		this(name);
		for (ItemStack stack : stacks)
			if (stack != null && this.stacks.size() < 10)
				this.stacks.add(stack);
	}

	public SubChapter(String name, Impl<?>... impls) {
		this(name);
		for (Impl<?> impl : impls) {
			if (impl instanceof Item && this.stacks.size() < 10)
				this.stacks.add(new ItemStack((Item) impl));
			else if (impl instanceof Block && this.stacks.size() < 10)
				this.stacks.add(new ItemStack((Block) impl));
		}
	}

	public SubChapter setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubChapter other = (SubChapter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
