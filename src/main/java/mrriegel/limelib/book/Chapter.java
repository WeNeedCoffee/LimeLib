package mrriegel.limelib.book;

import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Chapter {

	protected String name;
	protected final Map<Impl<?>, SubChapter> implMap = Maps.newHashMap();
	protected List<SubChapter> subChapters = Lists.newArrayList();
	protected int index;

	public Chapter(String name) {
		this.name = name;
	}

	public void addSubChapter(SubChapter c) {
		c.index = subChapters.size();
		subChapters.add(c);
		for (ItemStack s : c.stacks)
			if (s.getItem() != null)
				implMap.put(Block.getBlockFromItem(s.getItem()) != null ? Block.getBlockFromItem(s.getItem()) : s.getItem(), c);
	}

	public Chapter(String name, List<SubChapter> subChapters) {
		this.name = name;
		for (SubChapter c : subChapters)
			addSubChapter(c);
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
		Chapter other = (Chapter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
