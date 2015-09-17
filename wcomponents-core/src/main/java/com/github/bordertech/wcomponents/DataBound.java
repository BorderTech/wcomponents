package com.github.bordertech.wcomponents;

/**
 * <p>
 * As a general concept all WComponents can be looked upon as being editors for data structures that are entirely
 * independent of the user interface. Thus, potentially implementing this interface. However, the most WComponents deal
 * with concrete data structures (eg, <code>WTextField</code> is backed by a <code>java.util.String</code>) so in these
 * cases, implementing this interface is a bit redundant. Where it becomes a benefit is for components that can deal
 * with an unknown data structure (eg. a reusable panel).
 * </p><p>
 * In terms of the MVC pattern, the WComponent represents the View and the data structure/bean represents the Model.
 * </p><p>
 * Any <code>WComponent</code> implementing this interface and making use of a Velocity template for layout will have
 * access to the stored data in the template via the variable <em>$bean</em>.
 * </p><p>
 * NOTE: The object returned by {@link #getData()} may not necessarily be the same instance as the one supplied in the
 * {@link #setData(Object)} call. This is dependant on the implementation.
 * </p>
 *
 * @author Adam Millard
 */
public interface DataBound {

	/**
	 * Sets the data that this component displays/edits.
	 *
	 * @param data the data to set.
	 */
	void setData(Object data);

	/**
	 * Retrieves the data for the given context.
	 *
	 * @return the data for the given context.
	 */
	Object getData();
}
