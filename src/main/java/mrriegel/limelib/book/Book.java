package mrriegel.limelib.book;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public abstract class Book {

	protected List<Chapter> chapters = Lists.newArrayList();
	public Chapter lastChapter;
	public SubChapter lastSubChapter;
	public int lastPage;

	public void addChapter(Chapter c) {
		c.index = chapters.size();
		chapters.add(c);
	}

	public Pair<Integer, Integer> getPage(Impl impl) {
		for (Chapter c : chapters) {
			if (c.implMap.get(impl) != null) {
				return ImmutablePair.<Integer, Integer> of(c.index, c.implMap.get(impl).index);
			} else if (impl instanceof Item && Block.getBlockFromItem((Item) impl) != null && c.implMap.get(Block.getBlockFromItem((Item) impl)) != null) {
				return ImmutablePair.<Integer, Integer> of(c.index, c.implMap.get(Block.getBlockFromItem((Item) impl)).index);
			}
		}
		return null;
	}

	public abstract void openGUI();

	public abstract void openGUI(int chapter, int subchapter);

}
