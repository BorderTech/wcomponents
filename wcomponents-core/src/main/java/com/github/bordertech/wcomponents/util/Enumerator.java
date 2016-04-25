package com.github.bordertech.wcomponents.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Uitlity class to wraps an <code>Iterator</code>.
 *
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 *
 * @param <T> the type of object returned by the enumerator.
 */
public class Enumerator<T> implements Enumeration<T> {

	/**
	 * The <code>Iterator</code> over which the <code>Enumeration</code> represented by this class actually operates.
	 */
	private final Iterator<T> iterator;

	/**
	 * Return an Enumeration over the values returned by the specified Iterator.
	 *
	 * @param iterator Iterator to be wrapped
	 */
	public Enumerator(final Iterator<T> iterator) {
		this.iterator = iterator;
	}

	/**
	 * Tests if this enumeration contains more elements.
	 *
	 * @return <code>true</code> if and only if this enumeration object contains at least one more element to provide,
	 * <code>false</code> otherwise
	 */
	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	/**
	 * Returns the next element of this enumeration if this enumeration has at least one more element to provide.
	 *
	 * @return the next element of this enumeration
	 * @throws NoSuchElementException if no more elements exist
	 */
	@Override
	public T nextElement() {
		return iterator.next();
	}
}
