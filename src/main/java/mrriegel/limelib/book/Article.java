package mrriegel.limelib.book;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class Article {
	protected String text;
	protected NonNullList<ItemStack> stacks = NonNullList.create();
	protected String name;
	protected int index;

	static final int maxItems = 10;

	public Article(String name) {
		this.name = name;
	}

	public Article(String name, ItemStack... stacks) {
		this(name);
		for (ItemStack stack : stacks)
			if (!stack.isEmpty() && this.stacks.size() < maxItems)
				this.stacks.add(stack);
	}

	public Article(String name, Impl<?>... impls) {
		this(name);
		for (Impl<?> impl : impls) {
			if (impl instanceof Item && this.stacks.size() < maxItems)
				this.stacks.add(new ItemStack((Item) impl));
			else if (impl instanceof Block && this.stacks.size() < maxItems)
				this.stacks.add(new ItemStack((Block) impl));
		}
	}

	public Article setText(String text) {
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
		Article other = (Article) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
