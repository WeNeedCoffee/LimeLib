package kdp.limelib.helper;

import java.util.Map;
import java.util.stream.Stream;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class TagHelper {

	private static Map<Item, Stream<Tag<Item>>> itemCache = new Reference2ObjectOpenHashMap<>();

	//TODO call in reload event
	public static void clearCache() {
		itemCache.clear();
	}

	public static Stream<Tag<Item>> getTagsFor(Item item) {
		Stream<Tag<Item>> result = itemCache.computeIfAbsent(item,
				k -> ItemTags.getCollection().getTagMap().values().stream().filter(t -> t.contains(k)));
		return Stream.concat(result, Stream.empty());
	}

}
