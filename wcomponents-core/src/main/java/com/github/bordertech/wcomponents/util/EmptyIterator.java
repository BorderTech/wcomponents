package com.github.bordertech.wcomponents.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An empty Iterator, that can be returned for an iterator on a null/empty collection.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @param <T> the type of object returned by the iterator.
 */
public final class EmptyIterator<T> implements Iterator<T>, Serializable {

	/**
	 * Throws an IllegalStateException, as there are no elements to remove.
	 */
	@Override
	public void remove() {
		throw new IllegalStateException("No current element");
	}

	/**
	 * @return false as there are no elements.
	 */
	@Override
	public boolean hasNext() {
		return false;
	}

	/**
	 * Throws a NoSuchElementException, as there are no elements.
	 *
	 * @return nothing - an exception will be thrown.
	 */
	@Override
	public T next() {
		throw new NoSuchElementException("No more elements");
	}
}
