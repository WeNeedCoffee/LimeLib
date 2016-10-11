package mrriegel.limelib.book;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SubChapter {
	protected String text;
	protected ItemStack stack;
	protected String name;
	protected int index;

	public SubChapter(String name) {
		this.name = name;
	}

	public SubChapter(String name, ItemStack stack) {
		this(name);
		this.stack = stack;
	}

	public SubChapter(String name, Item item) {
		this(name);
		this.stack = new ItemStack(item);
	}

	public SubChapter(String name, Block block) {
		this(name);
		this.stack = new ItemStack(block);
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
