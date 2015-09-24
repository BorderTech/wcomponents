/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import java.util.Date;

/**
 * <p>
 * This is a test of multiple pollers with exactly the same poll interval. It is
 * <strong>only</strong> used for WComponent framework testing.</p>
 *
 * @author exbtma
 */
public class MultiPollingExample extends WPanel {

	private final WDateField date1;
	private final WDateField date2;
	private static final int DELAY = 1500;

	/**
	 * Construct example.
	 */
	public MultiPollingExample() {
		setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 12));
		add(new WText(
				"This example is for framework testing only. It is not to be used as an example of setting up a polling region."));

		WPanel panel1 = new WPanel();
		add(panel1);
		WAjaxControl ctrl1 = new WAjaxControl(null, panel1);
		ctrl1.setDelay(DELAY);
		panel1.add(ctrl1);
		date1 = new WDateField();
		date1.setToolTip("Example date field");
		panel1.add(date1);

		WPanel panel2 = new WPanel();
		add(panel2);
		WAjaxControl ctrl2 = new WAjaxControl(null, panel2);
		ctrl2.setDelay(3500);
		panel2.add(ctrl2);
		date2 = new WDateField();
		date2.setReadOnly(true);
		date2.setToolTip("Example read only date field");
		panel2.add(date2);

		add(new MyPanel());
		add(new MyPanel());
	}

	@Override
	public void preparePaintComponent(final Request request) {
		date1.setDate(new Date());
		date2.setDate(new Date());
	}

	/**
	 * Panel class with an AJAX control.
	 */
	private static final class MyPanel extends WPanel {

		/**
		 * Construct panel.
		 */
		private MyPanel() {
			WAjaxControl ctrl1 = new WAjaxControl(null, this);
			ctrl1.setDelay(DELAY);
			add(ctrl1);
			WText text = new WText() {
				@Override
				public String getText() {
					return new Date().toString();
				}
			};
			add(text);

			WDateField date = new WDateField();
			date.setDate(new Date());
			date.setToolTip("another example date field");

			add(date);

			WPanel eager = new WPanel();
			eager.setMode(PanelMode.EAGER);
			eager.add(new WText("Eager content"));
			add(eager);
		}

	}
}
