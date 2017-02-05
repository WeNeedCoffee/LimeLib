package mrriegel.limelib.util;

import java.lang.reflect.Type;

import mrriegel.limelib.helper.StackHelper;
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
			return null;
		}

		@Override
		public Item deserialize(NBTTagCompound nbt, JsonDeserializationContext context) {
			return Item.REGISTRY.getObjectById(nbt.getInteger("item"));
		}

	}

	public static class ItemStackLizer implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {

		@Override
		public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
			json.addProperty("ITEMSTACK", StackHelper.stackToString(src, false));
			json.add("NBT", context.serialize(src.getTagCompound()));
			return json;
		}

		@Override
		public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			ItemStack stack = StackHelper.stringToStack(json.getAsJsonObject().get("ITEMSTACK").getAsString());
			if (!stack.isEmpty())
				stack.setTagCompound((NBTTagCompound) context.deserialize(json.getAsJsonObject().get("NBT"), NBTTagCompound.class));
			return stack;
		}
	}

	public static class NBTLizer implements JsonDeserializer<NBTTagCompound>, JsonSerializer<NBTTagCompound> {

		@Override
		public JsonElement serialize(NBTTagCompound src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
			json.addProperty("NBT", src.toString());
			return json;
		}

		@Override
		public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String n = json.getAsJsonObject().get("NBT").getAsString();
			NBTTagCompound nbt = null;
			if (n != null)
				try {
					nbt = JsonToNBT.getTagFromJson(n);
				} catch (NBTException e) {
					e.printStackTrace();
				}
			return nbt;
		}
	}
}
