package com.github.bordertech.wcomponents;

/**
 * The ComponentModel for components implementing the {@link DataBound} interface.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DataBoundComponentModel extends ComponentModel {

	/**
	 * The component data held by this model.
	 */
	private Object data;

	/**
	 * @return Returns the component data.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data The component data to set.
	 */
	public void setData(final Object data) {
		this.data = data;
	}

	/**
	 * Resets the data to the default value. Only has an effect for session component models (ie. those with a shared
	 * model set).
	 */
	public void resetData() {
		ComponentModel sharedModel = getSharedModel();

		if (sharedModel instanceof DataBoundComponentModel) {
			Object sharedData = ((DataBoundComponentModel) sharedModel).getData();
			setData(copyData(sharedData));

			// clear the user data flag
			setFlags(getFlags() & ~USER_DATA_SET);
		}
	}
}
