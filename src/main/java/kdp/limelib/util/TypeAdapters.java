package kdp.limelib.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class TypeAdapters {
	private abstract static class JSONAdapter<T> extends TypeAdapter<T> {

		@Override
		public final void write(JsonWriter out, T value) throws IOException {
			out.beginObject();
			out.name("ÑBT").value(serialize(value).toString());
			out.endObject();
		}

		@Override
		public final T read(JsonReader in) throws IOException {
			T value = null;
			in.beginObject();
			if (in.hasNext() && in.nextName().equals("ÑBT"))
				try {
					value = deserialize(JsonToNBT.getTagFromJson(in.nextString()));
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
				}
			in.endObject();
			return value;
		}

		protected abstract NBTTagCompound serialize(T value);

		protected abstract T deserialize(NBTTagCompound nbt);

	}
}
