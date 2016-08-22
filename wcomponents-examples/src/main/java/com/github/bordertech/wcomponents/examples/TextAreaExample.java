package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * <p>
 * A {@link WTextArea} is a wcomponent used to display a html textarea. It is very much like WTextField except that it
 * has multiple lines of input.
 * </p>
 * <p>
 * WTextArea example demonstrates various states of the WTextArea
 * </p>
 * <p>
 * including
 * </p>
 * <ul>
 * <li>Default</li>
 * <li>Size limited</li>
 * <li>Read Only</li>
 * <li>Disabled</li>
 * </ul>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TextAreaExample extends WPanel {

	private final WTextArea ta1;
	private final WTextArea ta2;
	private final WTextArea ta3;
	private final WTextArea ta4;
	private final WTextArea ta5;

	/**
	 * Creates a TextAreaExample.
	 */
	public TextAreaExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		WFieldLayout layout = new WFieldLayout();
		WHeading heading = new WHeading(HeadingLevel.H2, "Default");
		add(heading);

		ta1 = new WTextArea();
		layout.addField("Default", ta1);
		final WTextArea readOnlyReflector1 = new WTextArea();
		readOnlyReflector1.setReadOnly(true);
		layout.addField("Read only reflection of normal WTextArea", readOnlyReflector1);
		WButton showReadOnlyContentButton = new WButton("Copy as read only");
		layout.addField(showReadOnlyContentButton);
		add(layout);

		showReadOnlyContentButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				readOnlyReflector1.setData(ta1.getData());
			}
		});
		add(new WAjaxControl(showReadOnlyContentButton, readOnlyReflector1));
		add(new WHorizontalRule());

		layout = new WFieldLayout();
		heading = new WHeading(HeadingLevel.H2, "Size 40x4, maxlength 200");
		add(heading);
		ta2 = new WTextArea();
		ta2.setColumns(40);
		ta2.setRows(4);
		ta2.setMaxLength(200);
		ta2.setPlaceholder("type here");
		layout.addField("Size and Length Limited", ta2);


		add(layout);
		add(new WHorizontalRule());

		layout = new WFieldLayout();
		heading = new WHeading(HeadingLevel.H2, "Read only");
		add(heading);
		ta3 = new WTextArea();
		ta3.setReadOnly(true);
		layout.addField("Read only", ta3);

		add(layout);
		add(new WHorizontalRule());

		layout = new WFieldLayout();
		heading = new WHeading(HeadingLevel.H2, "Disabled");
		add(heading);

		ta4 = new WTextArea();
		ta4.setDisabled(true);
		layout.addField("Disabled", ta4);

		WButton toggleDisableButton = new WButton("Toggle disable");
		toggleDisableButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				ta4.setDisabled(!ta4.isDisabled());
			}
		});

		add(layout);
		add(toggleDisableButton);
		add(new WHorizontalRule());

		heading = new WHeading(HeadingLevel.H2, "Rich Text");
		add(heading);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);

		ta5 = new WTextArea();
		ta5.setRichTextArea(true);
		layout.addField("Rich Text", ta5);

		final WTextArea richReadOnly = new WTextArea();
		richReadOnly.setReadOnly(true);
		richReadOnly.setRichTextArea(true);

		final Action setDataAction = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				richReadOnly.setData(ta5.getValue());
			}
		};
		WContainer buttonContainer = new WContainer();
		layout.addField((String) null, buttonContainer);
		layout.addField("read only reflection of the rich text area.", richReadOnly);

		// The buttons to show read only versions.
		final WButton rtfButton = new WButton("Round Trip Show Rich Text");
		rtfButton.setAction(setDataAction);
		buttonContainer.add(rtfButton);

		final WButton rtAjaxButton = new WButton("AJAX Show Rich Text");
		buttonContainer.add(rtAjaxButton);
		rtAjaxButton.setAction(setDataAction);

		add(new WAjaxControl(rtAjaxButton, richReadOnly));
	}

	/**
	 * Override preparePaintComponent to test that dynamic attributes are handled correctly.
	 *
	 * @param request the request that triggered the paint.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			ta3.setText("This is read only.");
			ta4.setText("This is disabled.");

			setInitialised(true);
		}
	}
}
