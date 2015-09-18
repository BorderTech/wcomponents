package com.github.bordertech.wcomponents.examples.repeater.link;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * A basic component which displays a {@link MyData} object's name and count attributes.
 *
 * @author Adam Millard
 * @since 1.0.0
 */
public class BasicComponent extends WDataRenderer {

	/**
	 * Use to display the the {@link MyData} bean's name.
	 */
	private final WText name = new WText();
	/**
	 * Use to display the the {@link MyData} bean's count.
	 */
	private final WText count = new WText();

	/**
	 * Use to increment the the {@link MyData} bean's count.
	 */
	private final WButton incBtn = new WButton("Increment");
	/**
	 * Use to decrement the the {@link MyData} bean's count.
	 */
	private final WButton decBtn = new WButton("Decrement");

	/**
	 * Creates a BasicComponent.
	 */
	public BasicComponent() {
		WPanel panel = new WPanel();
		add(panel);
		panel.setLayout(new FlowLayout(Alignment.LEFT));

		panel.add(name);
		panel.add(count);
		panel.add(incBtn);
		panel.add(decBtn);

		incBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				increment();
			}
		});

		decBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				decrement();
			}
		});

		panel.add(new WText("<br/>"));
	}

	/**
	 * Increments the {@link MyData} bean's count by 1. Called by the action on {@link #incBtn}.
	 */
	private void increment() {
		MyData data = (MyData) getData();
		data.setCount(data.getCount() + 1);
	}

	/**
	 * Increments the {@link MyData} bean's count by 1. Called by the action on {@link #decBtn}.
	 */
	private void decrement() {
		MyData data = (MyData) getData();
		int dataCount = data.getCount();

		if (dataCount > 0) {
			dataCount--;
		}

		data.setCount(dataCount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateComponent(final Object data) {
		MyData bean = (MyData) data;
		name.setText(bean.getName());
		count.setText(String.valueOf(bean.getCount()));
	}
}
