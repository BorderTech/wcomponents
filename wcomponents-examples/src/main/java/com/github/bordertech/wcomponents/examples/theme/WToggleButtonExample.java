package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WToggleButton;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;

/**
 * Shows the various properties of WToggleButton.
 *
 * @author Mark Reeves
 * @since 1.2.2
 */
public class WToggleButtonExample extends WPanel {

	/**
	 * Construct example.
	 */
	public WToggleButtonExample() {
		setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL, 8));
		WToggleButton toggle;

		add(new ExplanatoryText("Simple toggle button, no action."));
		add(new WToggleButton("show"));
		add(new ExplanatoryText("Selected toggle button, no action."));
		add(new WToggleButton("show", true));


		add(new ExplanatoryText("Toggle button as Subordinate trigger."));
		toggle = new WToggleButton("Controller");
		WPanel target = new WPanel(WPanel.Type.FEATURE);
		target.setMargin(new Margin(Size.SMALL, null, null, null));
		target.add(new WText("Subordinate target"));
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(toggle, String.valueOf(true));
		builder.whenTrue().show(target);
		builder.whenFalse().hide(target);
		WContainer container = new WContainer();
		container.add(toggle);
		container.add(target);
		container.add(builder.build());
		add(container);


		// WToggleButton as AjaxTrigger
		add(new ExplanatoryText("Toggle button as Ajax trigger."));
		final WToggleButton ajaxToggle = new WToggleButton("Controller");
		target = new WPanel();
		final WPanel innerTarget = new WPanel(WPanel.Type.FEATURE);

		innerTarget.setVisible(false);
		ajaxToggle.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				innerTarget.setVisible(ajaxToggle.isSelected());
			}
		});
		innerTarget.setMargin(new Margin(Size.SMALL, null, null, null));
		innerTarget.add(new ExplanatoryText("Ajax target", WStyledText.Type.EMPHASISED));
		target.add(innerTarget);
		container = new WContainer();
		container.add(ajaxToggle);
		container.add(target);
		container.add(new WAjaxControl(ajaxToggle, target));
		add(container);

		add(new ExplanatoryText("Toggle button exposing WCheckBox properties with labels."));
		WFieldLayout layout = new WFieldLayout();
		add(layout);


		// the regular (check-boxy) parts of WToggleButton.

		layout.addField("Normal toggle button", new WToggleButton());
		layout.addField("Checked toggle button", new WToggleButton(true));

		toggle = new WToggleButton();
		toggle.setDisabled(true);
		layout.addField("Disabled toggle button", toggle);

		toggle = new WToggleButton(true);
		toggle.setDisabled(true);
		layout.addField("Disabled checked toggle button", toggle);

		toggle = new WToggleButton();
		toggle.setMandatory(true);
		layout.addField("Mandatory toggle button", toggle);

		toggle = new WToggleButton();
		toggle.setReadOnly(true);
		layout.addField("Read only unchecked toggle button", toggle);

		toggle = new WToggleButton(true);
		toggle.setReadOnly(true);
		layout.addField("Read only checked toggle button", toggle);



	}

}
