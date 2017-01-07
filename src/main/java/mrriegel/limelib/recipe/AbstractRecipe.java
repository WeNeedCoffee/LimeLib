package mrriegel.limelib.recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mrriegel.limelib.helper.StackHelper;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

/**
 * @param <S>
 *            type of output
 * @param <T>
 *            type of interacting object
 */
public abstract class AbstractRecipe<S, T> {
	protected final List<S> output;
	protected final boolean order;
	protected final List<Object> input;

	public AbstractRecipe(List<S> output, boolean order, Object... input) {
		if (output.contains(null))
			throw new IllegalArgumentException("output contains null");
		this.output = Collections.unmodifiableList(output);
		this.order = order;
		this.input = Collections.unmodifiableList(Arrays.asList(input));
	}

	protected abstract List<ItemStack> getIngredients(T object);

	public abstract void removeIngredients(T object);

	public abstract List<ItemStack> getResult(T object);

	public List<S> getOutput() {
		return output;
	}

	public boolean isOrder() {
		return order;
	}

	public List<Object> getInput() {
		return input;
	}

	public boolean match(T object) {
		List<ItemStack> list = getIngredients(object);
		if (list.size() != input.size())
			return false;
		if (order) {
			for (int i = 0; i < input.size(); i++)
				if (!match(list.get(i), input.get(i)))
					return false;
			return true;
		} else {
			List<Object> foo = Lists.newArrayList(input);
			for (ItemStack stack : list) {
				if (stack != null) {
					boolean flag = false;
					for (int i = 0; i < foo.size(); i++) {
						Object o = foo.get(i);
						if (match(stack, o)) {
							flag = true;
							foo.remove(i);
							break;
						}
					}
					if (!flag) {
						return false;
					}
				}
			}
			return foo.isEmpty();
		}
	}

	protected boolean match(ItemStack stack, Object o) {
		return StackHelper.match(stack, o);
	}
}
