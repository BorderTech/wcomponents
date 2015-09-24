package com.github.bordertech.wcomponents.examples.repeater.link;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.util.Iterator;
import java.util.List;

/**
 * Example using a WRepeater.
 *
 * @author Adam Millard.
 */
public class RepeaterComponent extends WPanel {

	private final WRepeater repeater = new WRepeater();
	private final WButton selectorBtn = new WButton("Show");
	private final WTextArea selectorText = new WTextArea();

	/**
	 * Creates a RepeaterComponent.
	 */
	public RepeaterComponent() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		repeater.setRepeatedComponent(new BasicComponent());

		WFieldSet fieldset = new WFieldSet("Group");
		add(fieldset);
		fieldset.add(repeater);

		selectorBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				show();
			}
		});
		add(selectorBtn);

		selectorText.setRows(10);
		selectorText.setColumns(50);
		selectorText.setReadOnly(true);
		add(selectorText);
	}

	/**
	 * @param beanList the bean list to set on the repeater.
	 */
	public void setBeanList(final List beanList) {
		repeater.setBeanList(beanList);
	}

	/**
	 * Show the current list of beans used by the repeater.
	 */
	public void show() {
		StringBuffer out = new StringBuffer();

		for (Iterator iter = repeater.getBeanList().iterator(); iter.hasNext();) {
			MyData data = (MyData) iter.next();
			out.append(data.getName()).append(" : ").append(data.getCount()).append('\n');
		}

		selectorText.setText(out.toString());
	}
}
