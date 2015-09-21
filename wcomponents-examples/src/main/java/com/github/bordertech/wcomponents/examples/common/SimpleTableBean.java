package com.github.bordertech.wcomponents.examples.common;

import java.io.Serializable;

/**
 * Simple class used to hold data for a row in the table examples.
 *
 * @author Adam Millard
 */
public class SimpleTableBean implements Serializable {

	/**
	 * The bean's "name" attribute.
	 */
	private String name;

	/**
	 * The bean's "type" attribute.
	 */
	private String type;

	/**
	 * The bean's "thing" attribute.
	 */
	private String thing;

	/**
	 * Creates a SimpleDataBean with all attributes set to null.
	 */
	public SimpleTableBean() {
	}

	/**
	 * Creates a SimpleDataBean with the given attributes.
	 *
	 * @param name the name.
	 * @param type the type.
	 * @param thing the thing.
	 */
	public SimpleTableBean(final String name, final String type, final String thing) {
		this.name = name;
		this.type = type;
		this.thing = thing;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return Returns the thing.
	 */
	public String getThing() {
		return thing;
	}

	/**
	 * @param thing The thing to set.
	 */
	public void setThing(final String thing) {
		this.thing = thing;
	}

	/**
	 * @return the name.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Indicates whether this SimpleTableBean is equal to another object. Two SimpleTableBeans are considered equal if
	 * they have the same name.
	 *
	 * @param obj the object to test for equality.
	 * @return true if the object is a SimpleTableBean and is equal to this one, otherwise false.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SimpleTableBean)) {
			return false;
		}

		if (name == null) {
			return false;
		}

		return name.equals(((SimpleTableBean) obj).name);
	}

	/**
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}
}
