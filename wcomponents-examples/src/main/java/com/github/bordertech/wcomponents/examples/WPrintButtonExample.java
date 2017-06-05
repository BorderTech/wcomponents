package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPrintButton;
import com.github.bordertech.wcomponents.layout.FlowLayout;

/**
 *
 * @author Mark Reeves
 */
public class WPrintButtonExample extends WPanel {

	private static final String PRINT_LABEL = "Print";

	/**
	 * Simple examples of WPrintButton.
	 */
	public WPrintButtonExample() {
		setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL, Size.SMALL));
		add(new WPrintButton(PRINT_LABEL));

		WPrintButton button = new WPrintButton(PRINT_LABEL);
		button.setImage("/image/printer-w.png");
		add(button);

		button = new WPrintButton(PRINT_LABEL);
		button.setImage("/image/printer-w.png");
		button.setImagePosition(WButton.ImagePosition.NORTH);
		add(button);

		button = new WPrintButton(PRINT_LABEL);
		button.setImage("/image/printer-w.png");
		button.setImagePosition(WButton.ImagePosition.EAST);
		add(button);

		button = new WPrintButton(PRINT_LABEL);
		button.setImage("/image/printer-w.png");
		button.setImagePosition(WButton.ImagePosition.SOUTH);
		add(button);

		button = new WPrintButton(PRINT_LABEL);
		button.setImage("/image/printer-w.png");
		button.setImagePosition(WButton.ImagePosition.WEST);
		add(button);

		button = new WPrintButton("Disabled");
		button.setDisabled(true);
		add(button);
	}



}
