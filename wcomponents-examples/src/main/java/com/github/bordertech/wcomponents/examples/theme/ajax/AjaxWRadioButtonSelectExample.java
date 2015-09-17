package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * In this example the {@link WRadioButtonSelect} is made AJAX aware to update the content of a {@link WPanel}.
 *
 * @since 1.0.0
 * @author Jonathan Austin
 */
public class AjaxWRadioButtonSelectExample extends WPanel {

	/**
	 * First option for the RadioButtonSelect.
	 */
	private static final String OPTION_CONTENT1 = "Content1";

	/**
	 * Second option for the RadioButtonSelect.
	 */
	private static final String OPTION_CONTENT2 = "Content2";

	/**
	 * Second option for the RadioButtonSelect.
	 */
	private static final String OPTION_CONTENT3 = "Content3";

	/**
	 * WRadioButtonSelect that is AJAX aware.
	 */
	private final WRadioButtonSelect rbSelect = new WRadioButtonSelect();

	/**
	 * WPanel that is the AJAX target.
	 */
	private final WPanel panel = new WPanel();

	/**
	 * Text area on the panel that will display different content depending on the radio button selected.
	 */
	private final WText content = new WText();

	/**
	 * Construct the WRadioButtonSelect AJAX example.
	 */
	public AjaxWRadioButtonSelectExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 6));

		rbSelect.setAccessibleText("Content selection");
		rbSelect.setFrameless(true);

		// Setup the radio button select options
		rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		rbSelect.setOptions(new String[]{OPTION_CONTENT1, OPTION_CONTENT2, OPTION_CONTENT3});
		// Make the radio button select AJAX aware by setting the AJAX target.
		rbSelect.setAjaxTarget(panel);

		// Setup the panel
		panel.setLayout(new FlowLayout(Alignment.VERTICAL));
		panel.add(content);

		rbSelect.setToolTip("Choose one");
		add(rbSelect);
		add(panel);
	}

	/**
	 * Set the content of the text field depending on the selected option in the radio button select.
	 *
	 * @param request the request being processed
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (OPTION_CONTENT1.equals(rbSelect.getSelected())) {
			content.setText("This is content 1");
		} else if (OPTION_CONTENT2.equals(rbSelect.getSelected())) {
			content.setText("This is content 2");
		} else if (OPTION_CONTENT3.equals(rbSelect.getSelected())) {
			content.setText("This is content 3");
		} else {
			content.setText(null);
		}
	}
}
