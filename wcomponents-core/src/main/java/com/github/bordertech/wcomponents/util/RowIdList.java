package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WRepeater;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * This implementation of the List interface is not intended for general use, it exists solely to provide memory
 * efficient support for {@link WRepeater#getBeanList()}.</p>
 *
 * <p>
 * This list is immutable; any attempts to call a mutator method will result in an {@link UnsupportedOperationException}
 * being thrown.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @deprecated No longer required as {@link WDataTable} is deprecated.
 */
@Deprecated
public final class RowIdList implements List<Integer>, Serializable {

	/**
	 * Message for the UnsupportedOperationExceptions thrown for mutator methods.
	 */
	private static final String IMMUTABLE_MESSAGE = "DataModelRowIdList is immutable";

	/**
	 * The first row id.
	 */
	private final int startIndex;
	/**
	 * The last row id.
	 */
	private final int endIndex;

	/**
	 * Creates a RowIdList, which will "contain" consecutive Integer values, starting with <code>startIndex</code> and
	 * ending with <code>endIndex</code>.
	 *
	 * @param startIndex the first id
	 * @param endIndex the last id
	 */
	public RowIdList(final int startIndex, final int endIndex) {
		if (startIndex < 0) {
			throw new IllegalArgumentException("Start index must not be negative.");
		}

		if (endIndex < startIndex) {
			throw new IllegalArgumentException("End index must not be less than start index.");
		}

		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param location ignored
	 * @param value ignored
	 */
	@Override
	public void add(final int location, final Integer value) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param value ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public boolean add(final Integer value) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param collection ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public boolean addAll(final Collection collection) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param location ignored
	 * @param collection ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public boolean addAll(final int location, final Collection collection) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final Object object) {
		if (object instanceof Integer) {
			int index = (Integer) object;
			return index >= startIndex && index <= endIndex;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(final Collection collection) {
		for (Object object : collection) {
			if (!contains(object)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer get(final int location) {
		if (location < 0 || location >= size()) {
			throw new ArrayIndexOutOfBoundsException(location);
		}

		return startIndex + location;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(final Object object) {
		if (object instanceof Integer) {
			int value = (Integer) object;

			if (value >= startIndex && value <= endIndex) {
				return value - startIndex;
			}
		}

		return -1;
	}

	/**
	 * @return false - a RowIdList is never empty.
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private int index = 0;

			@Override
			public void remove() {
				throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
			}

			@Override
			public Integer next() {
				if (!hasNext()) {
					throw new NoSuchElementException(String.valueOf(index));
				}

				return get(index++);
			}

			@Override
			public boolean hasNext() {
				return index < size();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int lastIndexOf(final Object object) {
		return indexOf(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<Integer> listIterator() {
		return listIterator(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<Integer> listIterator(final int location) {
		return new ListIterator<Integer>() {
			private int index = location;

			@Override
			public void set(final Integer object) {
				throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
			}

			@Override
			public int previousIndex() {
				return index - 1;
			}

			@Override
			public Integer previous() {
				if (!hasPrevious()) {
					throw new NoSuchElementException(String.valueOf(index));
				}

				return get(--index);
			}

			@Override
			public int nextIndex() {
				return index + 1;
			}

			@Override
			public Integer next() {
				if (!hasNext()) {
					throw new NoSuchElementException(String.valueOf(index));
				}

				return get(index++);
			}

			@Override
			public boolean hasPrevious() {
				return index > 0;
			}

			@Override
			public boolean hasNext() {
				return index < size();
			}

			@Override
			public void add(final Integer object) {
				throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
			}
		};
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param location ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public Integer remove(final int location) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param object ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public boolean remove(final Object object) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param collection ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public boolean removeAll(final Collection collection) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param collection ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public boolean retainAll(final Collection collection) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException, as this list is immutable.
	 *
	 * @param location ignored
	 * @param value ignored
	 * @return nothing, an exception is always thrown
	 */
	@Override
	public Integer set(final int location, final Integer value) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return endIndex - startIndex + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> subList(final int start, final int end) {
		if (start < 0) {
			throw new ArrayIndexOutOfBoundsException(start);
		} else if (end > size()) {
			throw new ArrayIndexOutOfBoundsException(end);
		}

		return new RowIdList(get(start), get(end - 1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer[] toArray() {
		final Integer[] indices = new Integer[size()];

		for (int i = 0; i < indices.length; i++) {
			indices[i] = get(i);
		}

		return indices;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer[] toArray(final Object[] array) {
		Integer[] indices = (Integer[]) array;
		final int size = size();

		if (indices.length >= size()) {
			for (int i = 0; i < size; i++) {
				indices[i] = get(i);
			}

			return indices;
		} else {
			return toArray();
		}
	}
}
