package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example used for Selenium testing {@link WMultiSelectPair}.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class WMultiSelectPairTestingExample extends WContainer {

	/**
	 * A set of shape options.
	 */
	private static final String[] SHAPES = new String[]{"Circle", "Oval", "Rectangle", "Square", "Triangle"};
	/**
	 * A set of day options.
	 */
	private static final String[] DAYS = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
	/**
	 * A List of option groups.
	 */
	private final List<OptionGroup> groups;

	/**
	 * Creates a WMultiSelectPairExample.
	 */
	public WMultiSelectPairTestingExample() {
		groups = new ArrayList<>();
		groups.add(new OptionGroup("Shapes", Arrays.asList(SHAPES)));
		groups.add(new OptionGroup("Days", Arrays.asList(DAYS)));

		WFieldLayout layout = new WFieldLayout();
		add(layout);

		// simple WMultiSelectPair - no selection
		layout.addField("Simple", buildMSP(SHAPES));

		// simple WMultiSelectPair - with all selected
		WMultiSelectPair select = buildMSP(SHAPES);
		select.setSelected(select.getOptions());
		layout.addField("All selected", select);

		// simple with some selected
		select = buildMSP(SHAPES);
		List<?> options = select.getOptions();
		List<Object> selected = new ArrayList();
		int i = 0;
		for (Object option : options) {
			if (i++ % 2 == 0) {
				selected.add(option);
			}
		}
		select.setSelected(selected);
		layout.addField("Some selected", select);

		// disabled - no selection
		select = buildMSP(SHAPES);
		select.setDisabled(true);
		layout.addField("Disabled no selection", select);
		// disabled - with selection (note: this is the same as disabled with no selection if the control is not enabled in the client).
		select = buildMSP(SHAPES);
		select.setSelected(select.getOptions());
		select.setDisabled(true);
		layout.addField("Disabled with apparent selection", select);

		// readonly - no selection
		select = buildMSP(SHAPES);
		select.setReadOnly(true);
		layout.addField("Read-only no selection", select);
		// readonly - with selection
		select = buildMSP(SHAPES);
		select.setSelected(select.getOptions());
		select.setReadOnly(true);
		layout.addField("Read-only with selection", select);
		// readonly - with one selection
		select = buildMSP(SHAPES);
		selected = new ArrayList();
		selected.add(select.getOptions().get(0));
		select.setSelected(selected);
		select.setReadOnly(true);
		layout.addField("Read-only with single selection", select);

		// mandatory
		select = buildMSP(SHAPES);
		select.setMandatory(true);
		layout.addField("Mandatory", select);

		// hidden
		select = buildMSP(SHAPES);
		select.setHidden(true);
		layout.addField("Hidden", select);

		// anti-pattern: interactive no options
		layout.addField("No options", new WMultiSelectPair());
	}

	/**
	 * Build a WMultiSelectPair from a String array.
	 * @param options the options for the WMultiSelectPair
	 * @return a WMultiDelectPair
	 */
	private WMultiSelectPair buildMSP(final List options) {
		return new WMultiSelectPair(options);
	}

	/**
	 * Build a WMultiSelectPair from a String array.
	 * @param options the options for the WMultiSelectPair
	 * @return a WMultiDelectPair
	 */
	private WMultiSelectPair buildMSP(final String[] options) {
		return buildMSP(Arrays.asList(options));
	}

	/**
	 * @return make the tests a bit easier by sharing the groups.
	 */
	public static String[] getShapes() {
		return SHAPES;
	}

}
