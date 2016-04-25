package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAbbrText;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.ListLayout;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.LookupTable;

/**
 * <p>
 * This example shows usage of WAbbrText.
 * </p>
 * <p>
 * Hovering over each of the texts will display the tool tip
 * </p>
 *
 * @author Kishan Bisht
 * @since 1.0.0
 */
public final class WAbbrTextExample extends WContainer {

	/**
	 * Displays a set of WAbbrTexts containing Crt entry codes/descriptions.
	 */
	private final WPanel crtSexPanel = new WPanel();

	/**
	 * Displays a set of WAbbrTexts containing Crt entry codes/descriptions.
	 */
	private final WPanel crtIcaoPanel = new WPanel();

	/**
	 * Creates a WAbbrTextExample.
	 */
	public WAbbrTextExample() {
		add(new WHeading(HeadingLevel.H2, "Abreviation created from strings"));
		WAbbrText at1 = new WAbbrText("App Id", "Identification number of the application");
		add(at1);

		add(new WHeading(HeadingLevel.H2,
				"Abreviation created from lookup tables using the code as the text"));
		crtIcaoPanel.setLayout(new ListLayout(ListLayout.Type.STACKED, ListLayout.Alignment.LEFT,
				ListLayout.Separator.DOT, false));
		add(crtIcaoPanel);

		add(new WHeading(HeadingLevel.H2,
				"Abreviation created from lookup tables using the description as the text"));

		add(new ExplanatoryText("This example shows the dangers of doing code-set conversion and confusing the code and"
				+ " description. Obviously the abbreviation here is NOT the abbreviation we want. We would normally"
				+ " expect the reverse as in the example above."));
		crtSexPanel.setLayout(new ListLayout(ListLayout.Type.STACKED, ListLayout.Alignment.LEFT,
				ListLayout.Separator.DOT, false));
		add(crtSexPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			LookupTable lookup = Factory.newInstance(LookupTable.class);

			for (Object entry : lookup.getTable("sex")) {
				WAbbrText abbr = new WAbbrText();
				abbr.setTextWithDesc(entry);
				crtSexPanel.add(abbr);
			}

			for (Object entry : lookup.getTable("icao")) {
				WAbbrText abbr = new WAbbrText();
				abbr.setTextWithCode(entry);
				crtIcaoPanel.add(abbr);
			}

			setInitialised(true);
		}
	}
}
