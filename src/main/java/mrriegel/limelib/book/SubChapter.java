package mrriegel.limelib.book;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SubChapter {
	protected String text;
	protected ItemStack stack;
	protected String name;

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

}
