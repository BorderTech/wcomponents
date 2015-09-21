package com.github.bordertech.wcomponents.examples.validation.repeater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Example data bean.
 *
 * @author Adam Millard
 */
public class MyDataBean implements Serializable {

	private String name;
	private List<SomeDataBean> myBeans = new ArrayList<>();

	/**
	 * @return the list of beans.
	 */
	public List<SomeDataBean> getMyBeans() {
		return myBeans;
	}

	/**
	 * Sets the list of beans.
	 *
	 * @param myBeans the beans to set.
	 */
	public void setMyBeans(final List<SomeDataBean> myBeans) {
		this.myBeans = myBeans;
	}

	/**
	 * Adds a SomeDataBean to the list of beans.
	 *
	 * @param bean the bean to add.
	 */
	public void addBean(final SomeDataBean bean) {
		myBeans.add(bean);
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
