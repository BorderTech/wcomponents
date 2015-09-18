package com.github.bordertech.wcomponents.examples.repeater.link;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WRepeater;

/**
 * An example component showing use of repeated links, where the row data is used in the link's action.
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class RepeaterLinkComponent extends WDataRenderer {

	/**
	 * The repeater used to render the list of data.
	 */
	private final WRepeater repeater = new WRepeater();

	/**
	 * Creates a RepeaterLinkComponent.
	 */
	public RepeaterLinkComponent() {
		//setLayout(new FlowLayout(FlowLayout.VERTICAL));

		WFieldSet fieldset = new WFieldSet("Group");
		add(fieldset);
		fieldset.add(repeater);
	}

	/**
	 * Sets the action to execute when the "name" link is invoked.
	 *
	 * @param action the action for the name link.
	 */
	public void setNameAction(final Action action) {
		LinkComponent linkComponent = new LinkComponent();
		linkComponent.setNameAction(action);
		repeater.setRepeatedComponent(linkComponent);
	}

	/**
	 * Override to set the data on the repeater itself.
	 *
	 * @param data a list of {@link MyData} beans.
	 */
	@Override
	public void setData(final Object data) {
		repeater.setData(data);
	}
}
