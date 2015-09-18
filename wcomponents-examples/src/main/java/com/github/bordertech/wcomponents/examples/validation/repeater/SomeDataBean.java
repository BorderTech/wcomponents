package com.github.bordertech.wcomponents.examples.validation.repeater;

import java.io.Serializable;

/**
 * Example data bean.
 *
 * @author Adam Millard
 */
public class SomeDataBean implements Serializable {

	private String field1;
	private String field2;

	/**
	 * Creates a SomeDataBean.
	 */
	public SomeDataBean() {
	}

	/**
	 * Creates a SomeDataBean with the given data.
	 *
	 * @param field1 the value for field1.
	 * @param field2 the value for field2.
	 */
	public SomeDataBean(final String field1, final String field2) {
		this.field1 = field1;
		this.field2 = field2;
	}

	/**
	 * @return Returns the field1.
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @param field1 The field1 to set.
	 */
	public void setField1(final String field1) {
		this.field1 = field1;
	}

	/**
	 * @return Returns the field2.
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * @param field2 The field2 to set.
	 */
	public void setField2(final String field2) {
		this.field2 = field2;
	}
}
