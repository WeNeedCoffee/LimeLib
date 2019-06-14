package kdp.limelib.util;

import java.io.IOException;
import java.util.function.Supplier;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

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
            T value = defaultValue().get();
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

        protected abstract CompoundNBT serialize(T value);

        protected abstract T deserialize(CompoundNBT nbt);

        protected abstract Supplier<T> defaultValue();

    }

    public static class ItemStackAdapter extends JSONAdapter<ItemStack> {

        @Override
        protected CompoundNBT serialize(ItemStack value) {
            return value.serializeNBT();
        }

        @Override
        protected ItemStack deserialize(CompoundNBT nbt) {
            return ItemStack.read(nbt);
        }

        @Override
        protected Supplier<ItemStack> defaultValue() {
            return () -> ItemStack.EMPTY;
        }
    }
}
