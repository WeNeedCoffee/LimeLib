package kdp.limelib;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import kdp.limelib.helper.NBTBuilder;
import net.minecraftforge.fml.common.Mod;

@Mod("limelib")
public class LimeLib {
//TODO ProtoNBT - builder & wrapper for nbttagcompound 
	public LimeLib() {
		StringBuilder sb=new StringBuilder();
		for(String s:Arrays.asList("char","byte","short","int","long","float","double")) {
			sb.append("public NBTBuilder set(String key,"+s+" value) {\n" + 
					"		nbt.set"+StringUtils.capitalize(s)+"(key, value);\n" + 
					"		return this;\n" + 
					"	}");
		}
		System.out.println(sb);
		throw new IllegalStateException("s");
	}
}
