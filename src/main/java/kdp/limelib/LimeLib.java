package kdp.limelib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableMap;

import kdp.limelib.helper.nbt.NBTBuilder;
import kdp.limelib.helper.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;

@Mod("limelib")
public class LimeLib {
	public LimeLib() throws InstantiationException, IllegalAccessException {
		NBTHelper.init();
		NBTTagCompound nbt = new NBTTagCompound();
		hashCode();
		List<String> keys = new ArrayList<>();
		Random ran = new Random();
		for (int i = 0; i < 150; i++) {
			keys.add(RandomStringUtils.randomNumeric(8));
		}
		Map<String, Object> map = new HashMap<>();
		for (String k : keys) {
			long[] ar = new long[ran.nextInt(30) + 1];
			IntStream.range(0, ar.length).forEach(i -> ar[i] = ran.nextLong());
			NBTHelper.set(nbt, k, ar);
			map.put(k, ar);
		}
		System.out.println(nbt);
		for (String k : keys) {
			//Objects.equals(NBTHelper.get(nbt, k, short[].class), map.get(k));
			String a = Arrays.toString((long[]) map.get(k));
			String b = Arrays.toString(NBTHelper.get(nbt, k, long[].class));
			Validate.isTrue(a.equals(b), a + " " + b);
			System.out.println(a + " " + b);

		}
		nbt = NBTBuilder.of().set("aw", (Object) '€').build();
		NBTHelper.set(nbt, "aa", 'o');
		System.out.println(NBTHelper.get(nbt, "aa", char.class) + " _ " + NBTHelper.get(nbt, "aa", Character.class));
		System.out.println(NBTHelper.get(nbt, "aw", char.class) + " _ " + NBTHelper.get(nbt, "aw", Character.class));
		NBTHelper.setMap(nbt, "wurst", ImmutableMap.builder().put("hi", 4).put("milf", 12).put("3", -1).build());
		System.out.println(NBTHelper.getMap(nbt, "wurst", String.class, int.class));
		System.out.println(nbt);

		System.exit(0);
	}
}
