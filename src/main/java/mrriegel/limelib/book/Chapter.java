package mrriegel.limelib.book;

import java.util.List;

import com.google.common.collect.Lists;

public class Chapter {

	protected String name;
	protected List<SubChapter> subChapters = Lists.newArrayList();

	public Chapter(String name) {
		this.name = name;
	}

	public Chapter(String name, List<SubChapter> subChapters) {
		this.name = name;
		this.subChapters = subChapters;
	}

}
