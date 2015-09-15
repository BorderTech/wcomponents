package com.github.bordertech.wcomponents.util;

/**
 * A type-safe generic collection of three items.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * @param <T1> the type of the first item
 * @param <T2> the type of the second item
 * @param <T3> the type of the third item
 */
public class Triplet<T1, T2, T3> {

	/**
	 * The first item.
	 */
	private T1 first;

	/**
	 * The second item.
	 */
	private T2 second;

	/**
	 * The third item.
	 */
	private T3 third;

	/**
	 * Creates an empty duplet.
	 */
	public Triplet() {
	}

	/**
	 * Creates a duplet with the specified items.
	 *
	 * @param first the first item.
	 * @param second the second item.
	 * @param third the third item.
	 */
	public Triplet(final T1 first, final T2 second, final T3 third) {
		this.first = first;
		this.second = second;
		this.third = third;
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
	 * @return Returns the third item.
	 */
	public T3 getThird() {
		return third;
	}

	/**
	 * @param third The third item to set.
	 */
	public void setThird(final T3 third) {
		this.third = third;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = (first == null ? 0 : first.hashCode());
		hash = hash * 31 + (second == null ? 0 : second.hashCode());
		hash = hash * 31 + (third == null ? 0 : third.hashCode());

		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Triplet)) {
			return false;
		}

		Triplet other = (Triplet) obj;

		return Util.equals(first, other.first)
				&& Util.equals(second, other.second)
				&& Util.equals(third, other.third);
	}
}
