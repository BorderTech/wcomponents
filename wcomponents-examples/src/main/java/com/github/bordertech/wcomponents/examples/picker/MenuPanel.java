package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WebUtilities;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.LogFactory;

/**
 * Displays a menu of examples which the user can choose an example from.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
final class MenuPanel extends WPanel {

	/**
	 * The file to store recently accessed examples in.
	 */
	private static final String RECENT_FILE_NAME = "recent.dat";

	/**
	 * The maximum number of items to keep the recently accessed menu.
	 */
	private static final int MAX_RECENT_ITEMS = 20;

	/**
	 * The list of recently accessed examples, in order of most recently used.
	 */
	private final List<ExampleData> recent = new ArrayList<>();

	/**
	 * A menu of recently accessed examples.
	 */
	private final WMenu menu = new WMenu(WMenu.MenuType.TREE);

	/**
	 * A WTree to select from all available examples.
	 */
	private final ExamplePickerTree tree = new ExamplePickerTree();

	/**
	 * A WHeading for the recent example menu.
	 */
	private final WHeading recentMenuHeading = new WHeading(HeadingLevel.H2, "Recent Examples");

	/**
	 * Creates a MenuPanel.
	 */
	MenuPanel() {
		super(Type.CHROME);
		setTitleText("Menu");
		loadRecentList();
		add(new WHeading(HeadingLevel.H2, "Select an example"));
		tree.setIdName("example_selector_tree");
		add(tree);

		add(recentMenuHeading);
		add(menu);
		menu.setSelectionMode(WMenu.SelectionMode.SINGLE);
		menu.setMargin(new Margin(null, null, Size.LARGE, null));

		tree.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				ExampleData example = tree.getSelectedExampleData();
				if (example != null) {
					TreePicker picker = WebUtilities.getAncestorOfClass(TreePicker.class, tree);
					picker.selectExample(example);
					addToRecent(example);
					if (!menu.isVisible()) {
						menu.setVisible(true);
						recentMenuHeading.setVisible(true);
					}
				}
				menu.clearSelectedMenuItems();
			}
		});
	}

	/**
	 * @return the recently used examples menu
	 */
	public WMenu getMenu() {
		return menu;
	}

	/**
	 * Adds an example to the recent subMenu.
	 *
	 * @param text the text to display.
	 * @param data the example data instance
	 * @param select should the menuItem be selected
	 */
	private void addRecentExample(final String text, final ExampleData data, final boolean select) {
		WMenuItem item = new WMenuItem(text, new SelectExampleAction());
		item.setCancel(true);
		menu.add(item);
		item.setActionObject(data);
		if (select) {
			menu.setSelectedMenuItem(item);
		}
	}

	/**
	 * Retrieves the closest known match to a WComponent (or example) which this MenuPanel knows about. A fully
	 * qualified class name or partial name may be provided. A fully qualified match is returned in preference to a
	 * partial one. Partial name matching is case-insensitive, for example "prog" will match "WProgressBarExample".
	 *
	 * @param className the component class name to search for.
	 * @return the class for the given name, or null if not found.
	 */
	public ExampleData getClosestMatch(final String className) {
		// First try a fully qualified match to an example contained in the menu.
		ExampleData result = getMatch(menu, className, false);

		if (result == null) {
			// Try a fully qualified name that may not be in the list of examples.
			try {
				Class clazz = Class.forName(className);

				if (WComponent.class.isAssignableFrom(clazz)) {
					ExampleData data = new ExampleData(clazz.getSimpleName(), clazz);

					return data;
				}
			} catch (ClassNotFoundException | NoClassDefFoundError ignored) {
				// Just keep searching.
			}
		}

		if (result == null) {
			// Ok, definitely not a fully qualified class name, try a partial match.
			result = getMatch(menu, className, true);
		}

		return result;
	}

	/**
	 * Recursively searches the menu for a match to a WComponent with the given name.
	 *
	 * @param node the current node in the menu being searched.
	 * @param name the component class name to search for.
	 * @param partial if true, perform a case-insensitive partial name search.
	 * @return the class for the given name, or null if not found.
	 */
	private ExampleData getMatch(final WComponent node, final String name, final boolean partial) {
		if (node instanceof WMenuItem) {
			ExampleData data = (ExampleData) ((WMenuItem) node).getActionObject();

			Class<? extends WComponent> clazz = data.getExampleClass();

			if (clazz.getName().equals(name) || data.getExampleName().equals(name)
					|| (partial && clazz.getSimpleName().toLowerCase().contains(name.toLowerCase()))
					|| (partial && data.getExampleName().toLowerCase().contains(name.toLowerCase()))) {
				return data;
			}
		} else if (node instanceof Container) {
			for (int i = 0; i < ((Container) node).getChildCount(); i++) {
				ExampleData result = getMatch(((Container) node).getChildAt(i), name, partial);

				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	/**
	 * Reads the list of recently selected examples from a file on the file system.
	 */
	private void loadRecentList() {
		recent.clear();
		File file = new File(RECENT_FILE_NAME);

		if (file.exists()) {
			try {
				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				XMLDecoder decoder = new XMLDecoder(inputStream);
				List result = (List) decoder.readObject();
				decoder.close();
				for (Object obj : result) {
					if (obj instanceof ExampleData) {
						recent.add((ExampleData) obj);
					}
				}
			} catch (IOException ex) {
				LogFactory.getLog(getClass()).error("Unable to load recent list", ex);
			}
		}
	}

	/**
	 * Writes the list of recent selections to a file on the file system.
	 */
	private void storeRecentList() {
		synchronized (recent) {
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(RECENT_FILE_NAME));
				XMLEncoder encoder = new XMLEncoder(out);
				encoder.writeObject(recent);
				encoder.close();
			} catch (IOException ex) {
				LogFactory.getLog(getClass()).error("Unable to save recent list", ex);
			}
		}
	}

	/**
	 * Adds an example to the list of recently accessed examples. The list of recently examples will be persisted to the
	 * file system.
	 *
	 * @param example the data for the recently accessed example.
	 */
	public void addToRecent(final ExampleData example) {
		synchronized (recent) {
			recent.remove(example); // only add it once
			recent.add(0, example);

			// Only keep the last few entries.
			while (recent.size() > MAX_RECENT_ITEMS) {
				recent.remove(MAX_RECENT_ITEMS);
			}

			storeRecentList();
			setInitialised(false);
		}
	}

	/**
	 * Updates the entries in the "Recent" sub-menu.
	 */
	private void updateRecentMenu() {
		menu.removeAllMenuItems();

		int index = 1;
		boolean first = true;

		for (Iterator<ExampleData> i = recent.iterator(); i.hasNext();) {
			ExampleData data = i.next();

			try {
				StringBuilder builder = new StringBuilder(Integer.toString(index++)).append(". ");

				if (data.getExampleGroupName() != null) {
					builder.append(data.getExampleGroupName()).append(" - ");
				}

				builder.append(data.getExampleName());
				addRecentExample(builder.toString(), data, first);
				first = false;
			} catch (Exception e) {
				i.remove();
				LogFactory.getLog(getClass()).error("Unable to read recent class: " + data.getExampleName());
			}
		}
	}

	/**
	 * Override preparePaintComponent in order to populate the recently accessed menu when a user accesses this panel
	 * for the first time.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			updateRecentMenu();
			setInitialised(true);
		}

		boolean hasRecentMenu = menu.getMenuItems().size() > 0;
		menu.setVisible(hasRecentMenu);
		recentMenuHeading.setVisible(hasRecentMenu);
	}

	/**
	 * @return a read-only copy of the most recently accessed examples.
	 */
	public List<ExampleData> getRecent() {
		return Collections.unmodifiableList(recent);
	}

	/**
	 * @return the ExamplePickerTree used in this panel
	 */
	public ExamplePickerTree getTree() {
		return tree;
	}
}
