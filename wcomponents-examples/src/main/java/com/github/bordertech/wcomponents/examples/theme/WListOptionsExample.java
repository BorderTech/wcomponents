package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WList;
import com.github.bordertech.wcomponents.WList.Separator;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.SimpleTableBean;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This component shows the various configurations of a {@link WList} component.
 * </p>
 * <p>
 * <b>note: there are several configurations that just wont render correctly because the combination does not make sense
 * such as</b>
 * </p>
 * <ul>
 * <li>field layout with a flat list</li>
 * <li>stacked or striped with bars on a flow layout</li>
 * </ul>
 * <p>
 * also note: as at WComponents 6 the hidden tag has not been implemented in WList this has been raised but as it is has
 * not been raised by a project it has been placed on the todo pile.
 * </p>
 *
 * @author Steve Harney
 * @since 1.0.0
 */
public class WListOptionsExample extends WPanel {

	/**
	 * a container for holding the {@link WList} so it can be replaced without upsetting the whole page.
	 */
	private final WContainer container = new WContainer();

	/**
	 * the drop down for setting the type of the {@link WList}.
	 */
	private final WDropdown ddType = new WDropdown(WList.Type.values());

	/**
	 * the drop down for setting the separator on the {@link WList}.
	 */
	private final WDropdown ddSeparator = new WDropdown(WList.Separator.values());

	/**
	 * the check box for enabling/disabling the border on the {@link WList}.
	 */
	private final WCheckBox cbRenderBorder = new WCheckBox();

	/**
	 * the check box for setting the visibility on the {@link WList}.
	 */
	private final WCheckBox cbVisible = new WCheckBox(true);

	/**
	 * The check box for enabling/disabling a the field layout.
	 */
	private final WCheckBox cbRenderUsingFieldLayout = new WCheckBox();

	/**
	 * The check box group for the bean fields.
	 */
	private final WCheckBoxSelect cgBeanFields;

	/**
	 * Creates a WListExample.
	 */
	public WListOptionsExample() {
		super(Type.BLOCK);
		cgBeanFields = getBeanFieldsCheckBoxSelect();
		add(getListControls());
		add(new WHorizontalRule());
		add(container);
		add(new WHorizontalRule());
	}

	/**
	 * This builds and returns a WCheckbox group containing the list of fields on the {@link SimpleTableBean}.
	 *
	 * @return a WCheckBoxSelect
	 */
	private WCheckBoxSelect getBeanFieldsCheckBoxSelect() {
		List<String> options = new ArrayList<>();
		options.add("Name");
		options.add("Type");
		options.add("Thing");
		List<String> defaults = new ArrayList<>();
		defaults.add("Name");

		WCheckBoxSelect cgproperties = new WCheckBoxSelect(options);
		cgproperties.setSelected(defaults);

		return cgproperties;
	}

	/**
	 * build the list controls field set.
	 *
	 * @return a field set for the controls.
	 */
	private WFieldSet getListControls() {
		// Options Layout
		WFieldSet fieldSet = new WFieldSet("List configuration");
		WFieldLayout layout = new WFieldLayout();

		// options.
		layout.addField("Type", ddType);
		layout.addField("Separator", ddSeparator);
		layout.addField("Render fields", cgBeanFields);
		layout.addField("Render Border", cbRenderBorder);
		layout.addField("Render using field layout", cbRenderUsingFieldLayout);
		layout.addField("Visible", cbVisible);

		// Apply Button
		WButton apply = new WButton("Apply");
		apply.setAction(new com.github.bordertech.wcomponents.Action() {
			@Override
			public void execute(final ActionEvent event) {
				applySettings();

			}
		});

		fieldSet.add(layout);
		fieldSet.add(apply);
		return fieldSet;
	}

	/**
	 * Apply settings is responsible for building the list to be displayed and adding it to the container.
	 */
	private void applySettings() {
		container.reset();
		WList list = new WList((com.github.bordertech.wcomponents.WList.Type) ddType.getSelected());

		List<String> selected = (List<String>) cgBeanFields.getSelected();
		SimpleListRenderer renderer = new SimpleListRenderer(selected, cbRenderUsingFieldLayout.
				isSelected());

		list.setRepeatedComponent(renderer);

		list.setSeparator((Separator) ddSeparator.getSelected());

		list.setRenderBorder(cbRenderBorder.isSelected());
		container.add(list);
		List<SimpleTableBean> items = new ArrayList<>();
		items.add(new SimpleTableBean("A", "none", "thing"));
		items.add(new SimpleTableBean("B", "some", "thing2"));
		items.add(new SimpleTableBean("C", "little", "thing3"));
		items.add(new SimpleTableBean("D", "lots", "thing4"));
		list.setData(items);

		list.setVisible(cbVisible.isSelected());
	}

	/**
	 * Simple render implementation for the list.
	 *
	 * @author Steve Harney
	 */
	public static class SimpleListRenderer extends WBeanContainer {

		/**
		 * Creates a SimpleListRenderer.
		 *
		 * @param fields the list of fields to be rendered.
		 * @param useFieldLayout if a field layout is to be used to render the list
		 */
		public SimpleListRenderer(final List<String> fields, final boolean useFieldLayout) {
			WFieldLayout fieldLayout = null;

			if (useFieldLayout) {
				fieldLayout = new WFieldLayout();
				add(fieldLayout);
			}

			for (String field : fields) {
				addRenderField(fieldLayout, field);
			}
		}

		/**
		 * Adds a field to render.
		 *
		 * @param fields the {@link WFieldLayout} if a field layout is to be used
		 * @param label the label for the field.
		 */
		private void addRenderField(final WFieldLayout fields, final String label) {
			WText field = new WText();
			field.setBeanProperty(label.toLowerCase());
			if (fields != null) {
				fields.addField(label, field);
			} else {
				WText space = new WText(" ");
				add(space);
				add(field);
			}
		}
	}

	/**
	 * Override preparePaintComponent to perform initialisation the first time through.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			applySettings();

			setInitialised(true);
		}
	}
}
