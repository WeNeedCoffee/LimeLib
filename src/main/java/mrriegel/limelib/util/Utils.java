package mrriegel.limelib.util;

import java.util.Comparator;
import java.util.List;

public class Utils {
	public static <E> boolean contains(List<E> list, E e,
			Comparator<? super E> c) {
		for (E a : list)
			if (c.compare(a, e) == 0)
				return true;
		return false;
	}

}
