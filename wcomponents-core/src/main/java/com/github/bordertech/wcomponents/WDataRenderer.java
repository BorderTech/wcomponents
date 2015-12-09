package com.github.bordertech.wcomponents;

/**
 * This class is a convenience WComponent that is bound to a data object. It keeps hold of the data object supplied via
 * the setData method, and returns that same instance in the getData method. The data object is kept in sync with the
 * users actions every request/response cycle.
 *
 * @author Martin Shevchenko
 */
public class WDataRenderer extends WContainer {

	/**
	 * Subclasses must override this method if they need to keep the data object bound to this wcomponent in sync with
	 * changes entered by the user. Extensions of this class that act as editors will need to override this method.
	 *
	 * <p>
	 * In terms of the MVC pattern this method copies data from the View into the Model. Note that this method is the
	 * reverse of updateComponent.
	 * </p>
	 *
	 * <p>
	 * NOTE: This method will only be called if {@link #getData()} does not return null.
	 * </p>
	 *
	 * <pre>
	 *   public void updateData(Object data)
	 *   {
	 *   	SomeDataObject data = (SomeDataObject)data;
	 *   	data.setX(wcompX.getText());
	 *   	data.setY(wcompY.getText());
	 *   }
	 * </pre>
	 *
	 * @param data the data to update.
	 */
	public void updateData(final Object data) {
		// NOP
	}

	/**
	 * Subclasses must override this method if they need to keep this wcomponent in sync with the data object. The data
	 * object may have been modified by other wcomponents, or control logic.
	 *
	 * <p>
	 * In terms of the MVC pattern this method copies data from the Model into the View. Note that this method is the
	 * reverse of updateData.
	 * <p>
	 *
	 * <p>
	 * NOTE: This method will only be called if {@link #getData()} does not return null.
	 * </p>
	 * <pre>
	 *   public void updateComponent(Object data)
	 *   {
	 *      SomeDataObject data = (SomeDataObject)data;
	 *      wcompX.setText(data.getX());
	 *      wcompY.setText(data.getY());
	 *   }
	 * </pre>
	 *
	 * @param data the data to set on the component.
	 */
	public void updateComponent(final Object data) {
		// NOP
	}

	/**
	 * The handleRequest method has been overridden to keep the data object bound to this wcomponent in sync with any
	 * changes the user has entered.
	 *
	 * @param request the Request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		// Let the wcomponent gather data from the request.
		super.handleRequest(request);

		Object data = getData();

		if (data != null) {
			// Now update the data object (bound to this wcomponent) by copying
			// values from this wcomponent and its children into the data object.
			updateData(data);
		}
	}

	/**
	 * The preparePaintComponent method has been overridden to keep the data object bound to this wcomponent in sync
	 * with any changes that control logic (Action implementations) or other wcomponents have made to the data.
	 *
	 * @param request the Request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		// Let the wcomponent do its own preparation first, if any.
		super.preparePaintComponent(request);

		Object data = getData();

		if (data != null) {
			// Now update this wcomponent and its children with the data contained
			// in the data object.
			updateComponent(data);
		}
	}
}
