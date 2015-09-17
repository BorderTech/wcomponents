package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An example of a bean bound {@link WTable} with column level filtering. This example includes menu based filtering for
 * each column in the table and each filter is case-insensitive but allows the possibility of making them all case
 * sensitive. If a column has no data it gets no filter. There is also a table action to clear all of the filters. This
 * action is disabled if no filter is currently applied and hidden if the table has no visible filter menus. This is
 * possibly a tiny bit more complicated than it needs to be.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class FilterableTableExample extends WContainer {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * The bean properties used to populate the table.
	 */
	private static String[] beanProperties = new String[]{"firstName", "lastName", "dateOfBirth"};

	/**
	 * column index for the first name column.
	 */
	private static final int FIRST_NAME = 0;

	/**
	 * column index for the last name column.
	 */
	private static final int LAST_NAME = 1;

	/**
	 * column index for theDoB column.
	 */
	private static final int DOB = 2;

	/**
	 * The string for showing all rows (ie removing the filter).
	 */
	private static final String CLEAR_ALL = "Show all";

	/**
	 * placeholder for the filter menu if cells are "empty".
	 */
	private static final String EMPTY = "Empty";

	/**
	 * the menu to trigger the filtering by last name.
	 */
	private final WMenu lastNameFilterMenu = new WMenu(WMenu.MenuType.FLYOUT);

	/**
	 * the menu to trigger the filtering by first name.
	 */
	private final WMenu firstNameFilterMenu = new WMenu(WMenu.MenuType.FLYOUT);

	/**
	 * the menu to trigger the filtering by DoB.
	 */
	private final WMenu dobFilterMenu = new WMenu(WMenu.MenuType.FLYOUT);

	/**
	 * filter mode: SINGLE or MULTIPLE.
	 */
	private static final WMenu.SelectMode SELECT_MODE = WMenu.SelectMode.MULTIPLE;

	/**
	 * a button to clear all filters from all columns at once.
	 */
	private final WButton clearAllFiltersButton = new WButton("Clear all filters");

	/**
	 * The format we want to show in the menu - this is the format we show in WComponents.
	 */
	private static final String DATE_FORMAT = "dd MMM yyyy";

	/**
	 * Creates the example.
	 */
	@SuppressWarnings("serial")
	public FilterableTableExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn(buildColumnHeader("Given name", firstNameFilterMenu),
				new WText()));
		table.addColumn(new WTableColumn(buildColumnHeader("Family name", lastNameFilterMenu),
				new WText()));
		table.addColumn(new WTableColumn(buildColumnHeader("DoB", dobFilterMenu), new WDateField()));

		clearAllFiltersButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				firstNameFilterMenu.clearSelectedItems();
				lastNameFilterMenu.clearSelectedItems();
				dobFilterMenu.clearSelectedItems();
				getFilterableTableModel().filterBeanList();
			}
		});

		table.addAction(clearAllFiltersButton);
	}

	/**
	 * Set the filter case sensitivity.
	 *
	 * @param caseInsensitive If true the filter will not be case sensitive so, for example, "Smith" and "smith" will be
	 * equivalent.
	 */
	public void setCaseSensitiveMatch(final Boolean caseInsensitive) {
		FilterableBeanBoundDataModel model = getFilterableTableModel();
		if (model != null) {
			model.setCaseSensitiveMatch(caseInsensitive);
		}
	}

	/**
	 * Helper to create the table column heading's WDecoratedLabel.
	 *
	 * @param text The readable text content of the column header
	 * @param menu The WMenu we want in this column header.
	 * @return WDecoratedLabel used to create a column heading.
	 */
	private WDecoratedLabel buildColumnHeader(final String text, final WMenu menu) {
		WDecoratedLabel label = new WDecoratedLabel(null, new WText(text), menu);
		return label;
	}

	/**
	 * Builds the menu content for each column heading's filter menu.
	 */
	private void buildFilterMenus() {
		buildFilterSubMenu(firstNameFilterMenu, FIRST_NAME);
		if (firstNameFilterMenu.getChildCount() == 0) {
			firstNameFilterMenu.setVisible(false);
		}

		buildFilterSubMenu(lastNameFilterMenu, LAST_NAME);
		if (lastNameFilterMenu.getChildCount() == 0) {
			lastNameFilterMenu.setVisible(false);
		}

		buildFilterSubMenu(dobFilterMenu, DOB);
		if (dobFilterMenu.getChildCount() == 0) {
			dobFilterMenu.setVisible(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			// Setup model
			FilterableBeanBoundDataModel model = new FilterableBeanBoundDataModel(beanProperties);
			table.setTableModel(model);
			// Set the data as the bean on the table
			table.setBean(ExampleDataUtil.createExampleData());

			buildFilterMenus();

			String caption = "Use the menus in the column headers to show only the rows where the column value matches the value"
					+ ((SELECT_MODE == WMenu.SelectMode.MULTIPLE) ? "s " : " ")
					+ " of the selected option"
					+ ((SELECT_MODE == WMenu.SelectMode.MULTIPLE) ? "s." : ".");
			if (getFilterableTableModel().isCaseInsensitiveMatch()) {
				caption += " The case of the text is ignored.";
			}

			table.setCaption(caption);

			setInitialised(true);
		}
		setUpClearAllAction();
	}

	/**
	 * Sets the state of the clearAllActions button based on the visibility of the filter menus and if the button is
	 * visible sets its disabled state if nothing is filtered.
	 *
	 * This is usability sugar, it is not necessary for the functionality of the filters.
	 */
	private void setUpClearAllAction() {
		/* if one or fewer of the filter menus are visible then we don't need the clear all menus button */
		int visibleMenus = 0;
		if (firstNameFilterMenu.isVisible()) {
			visibleMenus++;
		}
		if (lastNameFilterMenu.isVisible()) {
			visibleMenus++;
		}
		if (dobFilterMenu.isVisible()) {
			visibleMenus++;
		}
		clearAllFiltersButton.setVisible(visibleMenus > 1);

		/* enable/disable the clear all filters action:
* if we have not initialised the lists then we do not need the button (though in this case it will not be visible);
 * otherwise if the size of the full list is the same as the size of the filtered list then we have not filtered the
 *  list so we do not need to clear the filters and the button can be disabled;
 * otherwise enable the button because we have applied at least one filter.
		 */
		if (clearAllFiltersButton.isVisible()) {
			List<?> fullList = getFilterableTableModel().getFullBeanList();
			List<?> filteredList = getFilterableTableModel().getBeanList();
			clearAllFiltersButton.setDisabled(fullList == null || filteredList == null || fullList.
					size() == filteredList.size());
		}
	}

	/**
	 * Helper to get the table model.
	 *
	 * @return the table model.
	 */
	private FilterableBeanBoundDataModel getFilterableTableModel() {
		return (FilterableBeanBoundDataModel) table.getTableModel();
	}

	/**
	 * Creates and populates the sub-menu for each filter menu.
	 *
	 * @param menu The WMenu we are currently populating.
	 * @param column The column index of the table column the menu is in. This is used to get the data off the table's
	 * Bean to put text content into the menu's items.
	 */
	private void buildFilterSubMenu(final WMenu menu, final int column) {
		List<?> beanList = getFilterableTableModel().getFullBeanList();
		int rows = (beanList == null) ? 0 : beanList.size();

		if (rows == 0) {
			return;
		}

		final List<String> found = new ArrayList<>();

		final WImage filterImage = new WImage("/image/view-filter.png",
				"Filter table using this column");
		filterImage.setCacheKey("filterImage");
		final WDecoratedLabel filterSubMenuLabel = new WDecoratedLabel(filterImage);
		final WSubMenu submenu = new WSubMenu(filterSubMenuLabel);
		submenu.setSelectMode(SELECT_MODE);
		menu.add(submenu);

		WMenuItem item = new WMenuItem(CLEAR_ALL, new ClearFilterAction());
		submenu.add(item);
		item.setActionObject(item);
		item.setSelectable(false);
		add(new WAjaxControl(item, table));

		Object cellObject;
		String cellContent, cellContentMatch;
		Object bean;

		for (int i = 0; i < rows; ++i) {
			bean = beanList.get(i);

			cellObject = getFilterableTableModel().getBeanPropertyValueFullList(
					beanProperties[column], bean);
			if (cellObject == null) {
				continue; //nothing to add to the sub menu
			}
			if (cellObject instanceof Date) {
				cellContent = new SimpleDateFormat(DATE_FORMAT).format((Date) cellObject);
			} else {
				cellContent = cellObject.toString();
			}

			if ("".equals(cellContent)) {
				cellContent = EMPTY;
			}

			cellContentMatch = (getFilterableTableModel().isCaseInsensitiveMatch()) ? cellContent.
					toLowerCase() : cellContent;

			if (found.indexOf(cellContentMatch) == -1) {
				item = new WMenuItem(cellContent, new FilterAction());
				submenu.add(item);
				add(new WAjaxControl(item, table));
				found.add(cellContentMatch);
			}
		}
	}

	/**
	 * Convenience class to add the same action to a bunch of menu items. This Action will apply all current filters to
	 * a table.
	 *
	 * @author Mark Reeves
	 */
	@SuppressWarnings("serial")
	private class FilterAction implements Action {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute(final ActionEvent event) {
			getFilterableTableModel().filterBeanList();
		}
	}

	/**
	 * Convenience class to add the same action to a bunch of menu items. This Action will clear all current filters
	 * from a table.
	 *
	 * @author Mark Reeves
	 */
	@SuppressWarnings("serial")
	private class ClearFilterAction implements Action {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute(final ActionEvent event) {
			if (event.getActionObject() == null) {
				return;
			}
			WMenuItem target = (WMenuItem) event.getActionObject();
			WMenu menu = WebUtilities.getAncestorOfClass(WMenu.class, target);
			menu.clearSelectedItems();
			getFilterableTableModel().filterBeanList();
		}
	}

	/**
	 * A simple table model which provides a bean list filter to filter by String value on each column in the table.
	 *
	 * @author Mark Reeves
	 * @since 1.0.0
	 */
	@SuppressWarnings("serial")
	private final class FilterableBeanBoundDataModel extends SimpleBeanBoundTableModel {

		/**
		 * The full bean list for the table.
		 */
		private List<?> fullList;

		/**
		 * the bean list used to populate the table.
		 */
		private List<?> filteredList;

		/**
		 * Indicates whether filter should be case sensitive.
		 */
		private Boolean caseInsensitiveMatch = true;

		/**
		 * @param columnBeanProperties the column bean properties
		 */
		private FilterableBeanBoundDataModel(final String[] columnBeanProperties) {
			super(columnBeanProperties);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<?> getBeanList() {
			if (fullList == null) {
				fullList = (List<?>) super.getBean();
				filteredList = fullList;
			}
			return filteredList;
		}

		/**
		 * @return the full list of beans
		 */
		public List<?> getFullBeanList() {
			if (fullList == null) {
				fullList = (List<?>) super.getBean();
				filteredList = fullList;
			}
			return fullList;
		}

		/**
		 * Helper to get a bean property value from the unfiltered bean list. Basically it just makes
		 * getBeanPropertyValue public.
		 *
		 * @param property The bean property to retrieve
		 * @param bean The bean to interrogate
		 * @return The bean property value.
		 */
		public Object getBeanPropertyValueFullList(final String property, final Object bean) {
			return getBeanPropertyValue(property, bean);
		}

		/**
		 * Filter the table's bean list when a filter is applied/changed.
		 */
		public void filterBeanList() {
			if (fullList == null || fullList.isEmpty()) {
				return;
			}

			List<WComponent> selectedItems = new ArrayList<>();
			List<WComponent> menuSelectedItems;

			if (firstNameFilterMenu.isVisible()) {
				menuSelectedItems = firstNameFilterMenu.getSelectedItems();
				if (menuSelectedItems != null && !menuSelectedItems.isEmpty()) {
					selectedItems.addAll(menuSelectedItems);
				}
			}
			if (lastNameFilterMenu.isVisible()) {
				menuSelectedItems = lastNameFilterMenu.getSelectedItems();
				if (menuSelectedItems != null && !menuSelectedItems.isEmpty()) {
					selectedItems.addAll(menuSelectedItems);
				}
			}
			if (dobFilterMenu.isVisible()) {
				menuSelectedItems = dobFilterMenu.getSelectedItems();
				if (menuSelectedItems != null && !menuSelectedItems.isEmpty()) {
					selectedItems.addAll(menuSelectedItems);
				}
			}

			// no filter applied or filters removed
			if (selectedItems.isEmpty()) {
				filteredList = fullList;
			} else {
				filteredList = filterBeanList(fullList);
			}
		}

		/**
		 * Sets the case sensitivity flag for filtering the table.
		 *
		 * @param caseInsensitive If true the filter will not be case sensitive so, for example, "Smith" and "smith"
		 * will be equivalent.
		 */
		public void setCaseSensitiveMatch(final Boolean caseInsensitive) {
			this.caseInsensitiveMatch = caseInsensitive;
		}

		/**
		 * Gets the case sensitivity setting of the filter.
		 *
		 * @return true if the filter is not case sensitive.
		 */
		public Boolean isCaseInsensitiveMatch() {
			return caseInsensitiveMatch;
		}

		/**
		 * Applies filters to a bean list.
		 *
		 * @param beanList the bean list to be filtered
		 * @return A bean list which may have been filtered.
		 */
		private List<?> filterBeanList(final List<?> beanList) {
			List<Object> list = new ArrayList<>();

			for (int i = 0; i < beanList.size(); ++i) {
				Object bean = beanList.get(i);

				if (filterBean(bean) != null) {
					list.add(bean);
				}
			}

			return list;
		}

		/**
		 * Determines if a given bean matches any applied filters.
		 *
		 * @param bean the bean to test
		 * @return the bean if it matches all applied filters or null if it does not.
		 */
		private Object filterBean(final Object bean) {
			if (bean == null) {
				return null;
			}

			List<WComponent> selectedItems;
			Object testBean;
			String itemText;
			String beanText;
			Boolean ok = false;

			/* AND filter: do each one */
			if (firstNameFilterMenu.isVisible()) {
				selectedItems = firstNameFilterMenu.getSelectedItems();
				if (selectedItems != null && !selectedItems.isEmpty()) {
					testBean = getBeanPropertyValue("firstName", bean);
					if (testBean == null) {
						return null;
					}
					ok = false;

					beanText = testBean.toString();
					if (caseInsensitiveMatch) {
						beanText = beanText.toLowerCase();
					}
					for (int i = 0; i < selectedItems.size(); ++i) {
						WMenuItem item = (WMenuItem) selectedItems.get(i);

						itemText = item.getText();
						if (EMPTY.equals(itemText)) {
							itemText = "";
						}

						if (caseInsensitiveMatch) {
							itemText = itemText.toLowerCase();
						}

						if (itemText.equals(beanText)) {
							ok = true;
							break;
						}
					}

					if (!ok) {
						return null;
					}
				}
			}

			if (lastNameFilterMenu.isVisible()) {
				selectedItems = lastNameFilterMenu.getSelectedItems();
				if (selectedItems != null && !selectedItems.isEmpty()) {
					testBean = getBeanPropertyValue("lastName", bean);
					if (testBean == null) {
						return null;
					}
					ok = false;
					beanText = testBean.toString();

					if (caseInsensitiveMatch) {
						beanText = beanText.toLowerCase();
					}
					for (int i = 0; i < selectedItems.size(); ++i) {
						WMenuItem item = (WMenuItem) selectedItems.get(i);
						itemText = item.getText();
						if (EMPTY.equals(itemText)) {
							itemText = "";
						}

						if (caseInsensitiveMatch) {
							itemText = itemText.toLowerCase();
						}
						if (itemText.equals(beanText)) {
							ok = true;
							break;
						}
					}

					if (!ok) {
						return null;
					}
				}
			}

			if (dobFilterMenu.isVisible()) {
				selectedItems = dobFilterMenu.getSelectedItems();
				if (selectedItems != null && !selectedItems.isEmpty()) {
					testBean = getBeanPropertyValue("dateOfBirth", bean);
					if (testBean == null) {
						return null;
					}

					ok = false;
					if (testBean instanceof Date) {
						beanText = new SimpleDateFormat(DATE_FORMAT).format((Date) testBean);
					} else {
						beanText = testBean.toString();
					}

					if (caseInsensitiveMatch) {
						beanText = beanText.toLowerCase();
					}
					for (int i = 0; i < selectedItems.size(); ++i) {
						WMenuItem item = (WMenuItem) selectedItems.get(i);
						itemText = item.getText();
						if (EMPTY.equals(itemText)) {
							itemText = "";
						}

						if (caseInsensitiveMatch) {
							itemText = itemText.toLowerCase();
						}
						if (itemText.equals(beanText)) {
							ok = true;
							break;
						}
					}
					if (!ok) {
						return null;
					}
				}
			}
			return bean;
		}
	}
}
