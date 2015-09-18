package com.github.bordertech.wcomponents.examples.repeater.link;

import java.io.Serializable;

/**
 * Example data bean, containing "name" and "count" fields.
 *
 * @author Adam Millard
 */
public class MyData implements Serializable {

	/**
	 * An example "name" field.
	 */
	private String name;
	/**
	 * An example "count" field.
	 */
	private int count;

	/**
	 * Creates a MyData bean.
	 */
	public MyData() {
		this.count = 0;
	}

	/**
	 * Creates a MyData bean with the specified name.
	 *
	 * @param name the name.
	 */
	public MyData(final String name) {
		this();
		this.name = name;
	}

	/**
	 * @return the count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count the count to set.
	 */
	public void setCount(final int count) {
		this.count = count;
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set.
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
