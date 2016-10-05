package mrriegel.limelib.book;

import java.util.List;

import com.google.common.collect.Lists;

public class Chapter {

	protected String name;
	protected List<Chapter> subChapters = Lists.newArrayList();
	protected String text;

	public Chapter(String name) {
		this.name = name;
	}

	public Chapter setText(String text) {
		this.text = text;
		return this;
	}

}
