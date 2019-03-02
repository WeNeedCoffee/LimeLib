package kdp.limelib.helper.nbt;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;

public class ModifiableCollection<E> implements Collection<E> {

	private final NBTTagCompound collectionTag;
	private final INBTConverter<E> converter;

	public ModifiableCollection(NBTTagCompound collectionTag, INBTConverter<E> converter) {
		this.collectionTag = collectionTag;
		this.converter = converter;
	}

	@Override
	public int size() {
		int result = -1;
		if (collectionTag.contains(NBTHelper.NULL_AMOUNT, 3))
			result = collectionTag.getInt(NBTHelper.NULL_AMOUNT);
		else {

		}
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
