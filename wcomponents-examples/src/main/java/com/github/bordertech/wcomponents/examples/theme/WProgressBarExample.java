package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WProgressBar;
import com.github.bordertech.wcomponents.WProgressBar.ProgressBarType;
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
		setLayout(new FlowLayout(Alignment.VERTICAL, Size.SMALL));

		add(new WText("Default, 10 max"));
		WProgressBar progressBar = new WProgressBar(10);
		progressBar.setToolTip("Default progress bar type");
		add(new ProgressBarWithButtons(progressBar));

		add(new WText("Normal, 10 max"));
		progressBar = new WProgressBar(ProgressBarType.NORMAL, 10);
		progressBar.setToolTip("Page progress");
		add(new ProgressBarWithButtons(progressBar));

		add(new WText("Small, 4 max"));
		progressBar = new WProgressBar(ProgressBarType.SMALL, 4);
		progressBar.setToolTip("Progress out of 4");
		add(new ProgressBarWithButtons(progressBar));

		progressBar = new WProgressBar(ProgressBarType.SMALL, 33);
		progressBar.setToolTip("Progress out of 33");
		progressBar.setBeanProvider(new BeanProvider() {
			private final RandomValueGenerator bean = new RandomValueGenerator(33);

			@Override
			public Object getBean(final BeanProviderBound beanProviderBound) {
				return bean.getValue();
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
			setLayout(new FlowLayout(Alignment.LEFT, Size.MEDIUM));
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
