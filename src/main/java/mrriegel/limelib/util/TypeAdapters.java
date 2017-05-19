package mrriegel.limelib.util;

import java.lang.reflect.Type;

import mrriegel.limelib.helper.NBTHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TypeAdapters {

	private abstract static class JsonLizer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

		@Override
		public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
			json.addProperty("NBTNBT", serialize(src, context).toString());
			return json;
		}

		@Override
		public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return deserialize(JsonToNBT.getTagFromJson(json.getAsJsonObject().get("NBTNBT").getAsString()), context);
			} catch (NBTException e) {
				e.printStackTrace();
			}
			return null;
		}

		public abstract NBTTagCompound serialize(T t, JsonSerializationContext context);

		public abstract T deserialize(NBTTagCompound nbt, JsonDeserializationContext context);

	}

	public static class ItemLizer extends JsonLizer<Item> {

		@Override
		public NBTTagCompound serialize(Item t, JsonSerializationContext context) {
			NBTTagCompound n = new NBTTagCompound();
			n.setInteger("item", Item.REGISTRY.getIDForObject(t));
			return n;
		}

		@Override
		public Item deserialize(NBTTagCompound nbt, JsonDeserializationContext context) {
			return Item.REGISTRY.getObjectById(nbt.getInteger("item"));
		}

	}

	public static class ItemStackLizer extends JsonLizer<ItemStack> {

		@Override
		public NBTTagCompound serialize(ItemStack t, JsonSerializationContext context) {
			return t.writeToNBT(new NBTTagCompound());
		}

		@Override
		public ItemStack deserialize(NBTTagCompound nbt, JsonDeserializationContext context) {
			return new ItemStack(nbt);
		}
	}

	public static class NBTLizer extends JsonLizer<NBTTagCompound> {

		@Override
		public NBTTagCompound serialize(NBTTagCompound t, JsonSerializationContext context) {
			return NBTHelper.setTag(new NBTTagCompound(), "nnbbtt", t);
		}

		@Override
		public NBTTagCompound deserialize(NBTTagCompound nbt, JsonDeserializationContext context) {
			return NBTHelper.getTag(nbt, "nnbbtt");
		}

	}
}
