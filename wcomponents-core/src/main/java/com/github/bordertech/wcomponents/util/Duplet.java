package com.github.bordertech.wcomponents.util;

/**
 * A type-safe generic collection of two items.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @param <T1> the type of the first item
 * @param <T2> the type of the second item
 */
public class Duplet<T1, T2> {

	/**
	 * The first item.
	 */
	private T1 first;

	/**
	 * The second item.
	 */
	private T2 second;

	/**
	 * Creates an empty duplet.
	 */
	public Duplet() {
	}

	/**
	 * Creates a duplet with the specified items.
	 *
	 * @param first the first item.
	 * @param second the second item.
	 */
	public Duplet(final T1 first, final T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return Returns the first item.
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * @param first The first item to set.
	 */
	public void setFirst(final T1 first) {
		this.first = first;
	}

	/**
	 * @return Returns the second item.
	 */
	public T2 getSecond() {
		return second;
	}

	/**
	 * @param second The second item to set.
	 */
	public void setSecond(final T2 second) {
		this.second = second;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = (first == null ? 0 : first.hashCode());
		hash = hash * 31 + (second == null ? 0 : second.hashCode());

		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Duplet)) {
			return false;
		}

		Duplet other = (Duplet) obj;

		return Util.equals(first, other.first)
				&& Util.equals(second, other.second);
	}
}
