package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WProgressBar;
import com.github.bordertech.wcomponents.WProgressBar.ProgressBarType;
import com.github.bordertech.wcomponents.WProgressBar.UnitType;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.io.Serializable;
import java.util.Random;

/**
 * Example showing how to use the {@link WProgressBar} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WProgressBarExample extends WPanel {

	/**
	 * Creates a WProgressBarExample.
	 */
	public WProgressBarExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 1));

		add(new WText("Default, 100 max"));
		WProgressBar progressBar = new WProgressBar(100);
		add(new ProgressBarWithButtons(progressBar));

		add(new WText("Default, 100 max, custom text"));
		progressBar = new WProgressBar(100);
		progressBar.setText("Demo in progress!");
		add(new ProgressBarWithButtons(progressBar));

		add(new WText("Percent, 4 max"));
		progressBar = new WProgressBar(ProgressBarType.NORMAL, UnitType.PERCENTAGE, 4);
		add(new ProgressBarWithButtons(progressBar));

		add(new WText("Small, 10 max, custom text"));
		progressBar = new WProgressBar(ProgressBarType.SMALL, UnitType.PERCENTAGE, 10);
		progressBar.setText("Small text!");
		add(new ProgressBarWithButtons(progressBar));

		add(new WText("Small, fraction, 33 max, bound to a random value"));
		progressBar = new WProgressBar(ProgressBarType.SMALL, UnitType.FRACTION, 33);
		progressBar.setBeanProperty("value");
		progressBar.setBeanProvider(new BeanProvider() {
			private final RandomValueGenerator bean = new RandomValueGenerator(33);

			@Override
			public Object getBean(final BeanProviderBound beanProviderBound) {
				return bean;
			}
		});

		add(progressBar);
		add(new WButton("Refresh"));
	}

	/**
	 * A dummy bean that provides random values.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class RandomValueGenerator implements Serializable {

		/**
		 * The random number generator to use.
		 */
		private static final Random RANDOM = new Random();

		/**
		 * The maximum random value which can be returned by this RandomBean.
		 */
		private final int max;

		/**
		 * Creates a RandomBean that generates values between 0 and max (inclusive).
		 *
		 * @param max the maximum random value.
		 */
		public RandomValueGenerator(final int max) {
			this.max = max;
		}

		/**
		 * @return a random integer, between 0 and {@link #max}.
		 */
		public int getValue() {
			return RANDOM.nextInt(max);
		}
	}

	//private static final int[] EXAMPLE_COLUMN_WIDTHS = {20,80};
	/**
	 * A convenience class that displays increment/decrement buttons next to a progress bar.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class ProgressBarWithButtons extends WPanel {

		/**
		 * Creates a ProgressBarWithButtons.
		 *
		 * @param bar the progress bar.
		 */
		private ProgressBarWithButtons(final WProgressBar bar) {
			setLayout(new FlowLayout(Alignment.LEFT, 6, 0));
			//setLayout(new ColumnLayout(EXAMPLE_COLUMN_WIDTHS, 6, 0));
			add(bar);
			//WPanel buttonPanel = new WPanel();
			// buttonPanel.setLayout(new FlowLayout(Alignment.LEFT, 3, 0));
			WButton increment = new WButton("+");
			increment.setToolTip("increment progress bar");
			add(increment);

			add(new WText("/"));

			WButton decrement = new WButton("-");
			decrement.setToolTip("decrement progress bar");
			add(decrement);
			increment.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					bar.setValue(bar.getValue() + 1);
				}
			});

			decrement.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					bar.setValue(bar.getValue() - 1);
				}
			});

			increment.setAjaxTarget(bar);
			decrement.setAjaxTarget(bar);
		}
	}
}
