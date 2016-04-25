package com.github.bordertech.wcomponents;

import java.io.Serializable;

/**
 * The margins to be used on a component.
 * <p>
 * A default margin size can be set for "all" sides of the panel, or the specific margin sizes can be set for each side
 * of the panel.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Margin implements Serializable {

	/**
	 * The size of the margins on all sides of the panel.
	 */
	private final int all;
	/**
	 * The size of the north margin.
	 */
	private final int north;
	/**
	 * The size of the east margin.
	 */
	private final int east;
	/**
	 * The size of the south margin.
	 */
	private final int south;
	/**
	 * The size of the west margin.
	 */
	private final int west;

	/**
	 * @param all the size of the margin to be used on all sides of the panel.
	 */
	public Margin(final int all) {
		if (all < 0) {
			throw new IllegalArgumentException("All must be greater than or equal to zero");
		}
		this.all = all;
		this.north = -1;
		this.east = -1;
		this.south = -1;
		this.west = -1;
	}

	/**
	 * The margin sizes to be used on each side of the panel.
	 *
	 * @param north the size of the north margin.
	 * @param east the size of the east margin.
	 * @param south the size of the south margin.
	 * @param west the size of the west margin.
	 */
	public Margin(final int north, final int east, final int south, final int west) {
		if (north < 0) {
			throw new IllegalArgumentException("North must be greater than or equal to zero");
		}

		if (east < 0) {
			throw new IllegalArgumentException("East must be greater than or equal to zero");
		}

		if (south < 0) {
			throw new IllegalArgumentException("South must be greater than or equal to zero");
		}

		if (west < 0) {
			throw new IllegalArgumentException("West must be greater than or equal to zero");
		}

		this.all = -1;
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
	}

	/**
	 * @return the size of the margin to be used on all sides of the panel, or -1 if it has not been set.
	 */
	public int getAll() {
		return all;
	}

	/**
	 * @return the size of the north margin, or -1 if it has not been set.
	 */
	public int getNorth() {
		return north;
	}

	/**
	 * @return the size of the east margin, or -1 if it has not been set.
	 */
	public int getEast() {
		return east;
	}

	/**
	 * @return the size of the south margin, or -1 if it has not been set.
	 */
	public int getSouth() {
		return south;
	}

	/**
	 * @return the size of the west margin, or -1 if it has not been set.
	 */
	public int getWest() {
		return west;
	}
}
