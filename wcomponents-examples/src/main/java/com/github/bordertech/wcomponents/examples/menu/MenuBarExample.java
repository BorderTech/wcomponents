package com.github.bordertech.wcomponents.examples.menu;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Disableable;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WDialog;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WMenuItemGroup;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import java.util.Date;

/**
 * This component demonstrates the usage of a {@link WMenu.MenuType#BAR Tree} {@link WMenu}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MenuBarExample extends WContainer {

	/**
	 * A menu to be used by other examples.
	 */
	private WMenu barMenu;

	/**
	 * Creates a MenuBarExample.
	 */
	public MenuBarExample() {
		WPanel content = new WPanel(WPanel.Type.BLOCK);
		WText selectedMenuText = new WText();
		content.add(new WStyledText("Selected item: ", WStyledText.Type.EMPHASISED));
		content.add(selectedMenuText);
		add(content);
		WMenu bar = buildMenuBar(selectedMenuText);

		add(bar);
		WDialog dialog = new WDialog(new WText("hello! Generated at: ".concat(new Date().toString())));
		add(dialog);
		WMenuItem launchDialogItem = new WMenuItem("Launch Dialog");
		bar.addMenuItem(launchDialogItem);
		dialog.setTrigger(launchDialogItem);
	}

	/**
	 * Create a MenuBaExample with an existing selectedItem reporter.
	 * @param selectedMenuText The container to report the selected item.
	 */
	public MenuBarExample(final WText selectedMenuText) {
		barMenu = buildMenuBar(selectedMenuText);
	}

	/**
	 * Allow an external example to get a menu bar from this example.
	 * @return the WMenu.
	 */
	public WMenu getMenu() {
		return barMenu;
	}

	/**
	 * Builds up a menu bar for inclusion in the example.
	 *
	 * @param selectedMenuText the WText to display the selected menu item.
	 * @return a menu bar for the example.
	 */
	private WMenu buildMenuBar(final WText selectedMenuText) {
		WMenu menu = new WMenu();

		// The Colours menu just shows simple text
		WSubMenu colourMenu = new WSubMenu("Colours");
		colourMenu.setMode(WSubMenu.MenuMode.LAZY);
		colourMenu.setAccessKey('C');
		addMenuItem(colourMenu, "Red", selectedMenuText);
		addMenuItem(colourMenu, "Green", selectedMenuText);
		addMenuItem(colourMenu, "Blue", selectedMenuText);
		colourMenu.addSeparator();
		colourMenu.add(new WMenuItem("Disable colour menu", new ToggleDisabledAction(colourMenu)));
		menu.add(colourMenu);

		// The Shapes menu shows grouping of items
		WSubMenu shapeMenu = new WSubMenu("Shapes");
		shapeMenu.setAccessKey('S');
		addMenuItem(shapeMenu, "Circle", selectedMenuText);

		WMenuItemGroup triangleGroup = new WMenuItemGroup("Triangles");
		shapeMenu.add(triangleGroup);
		shapeMenu.setMode(WSubMenu.MenuMode.DYNAMIC);
		addMenuItem(triangleGroup, "Equilateral", selectedMenuText);
		addMenuItem(triangleGroup, "Isosceles", selectedMenuText);
		addMenuItem(triangleGroup, "Scalene", selectedMenuText);
		addMenuItem(triangleGroup, "Right-angled", selectedMenuText);
		addMenuItem(triangleGroup, "Obtuse", selectedMenuText);

		WMenuItemGroup quadGroup = new WMenuItemGroup("Quadrilaterals");
		shapeMenu.add(quadGroup);
		addMenuItem(quadGroup, "Square", selectedMenuText);
		addMenuItem(quadGroup, "Rectangle", selectedMenuText);
		addMenuItem(quadGroup, "Rhombus", selectedMenuText);
		addMenuItem(quadGroup, "Trapezoid", selectedMenuText);
		addMenuItem(quadGroup, "Parallelogram", selectedMenuText);

		shapeMenu.addSeparator();
		shapeMenu.add(new WMenuItem("Disable shape menu", new ToggleDisabledAction(shapeMenu)));
		menu.add(shapeMenu);

		// The Image menu shows use of decorated labels and images
		WDecoratedLabel imageLabel = new WDecoratedLabel(new WImage("/image/wrench.png", "spanner"), new WText("Images"), null);
		WSubMenu imageMenu = new WSubMenu(imageLabel);
		imageMenu.add(createImageMenuItem("/image/flag.png", "Flag", "eg-menu-image-1",
				selectedMenuText));
		imageMenu.add(createImageMenuItem("/image/attachment.png", "Attachment", "eg-menu-image-2",
				selectedMenuText));
		imageMenu.add(createImageMenuItem("/image/settings.png", "Settings", "eg-menu-image-3",
				selectedMenuText));

		imageMenu.addSeparator();
		imageMenu.add(new WMenuItem("Disable image menu", new ToggleDisabledAction(imageMenu)));
		menu.add(imageMenu);

		WSubMenu sitesMenu = new WSubMenu("External apps");
		sitesMenu.add(new WMenuItem("External website", "http://www.example.com/"));
		WMenuItem google = new WMenuItem("Example (new window)", "http://www.example.com/");
		google.setTargetWindow("exampleWindow");
		sitesMenu.add(google);
		menu.add(sitesMenu);

		// Add an item to toggle the states of all the menus
		menu.add(new WMenuItem("Toggle top-level menus", new ToggleDisabledAction(colourMenu,
				shapeMenu, imageMenu,
				sitesMenu)));

		menu.add(new WMenuItem("Link", "http://www.example.com"));
		menu.add(new WMenuItem("No Action"));

		WMenuItem itemWithIcon = new WMenuItem("Help");
		itemWithIcon.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// do something
			}
		});
		itemWithIcon.setHtmlClass(HtmlClassProperties.ICON_HELP_BEFORE);
		menu.add(itemWithIcon);

		WMenuItem itemWithImage = new WMenuItem(new WDecoratedLabel(new WImage("/image/home.png", "home"), new WText("Home"), null));
		menu.add(itemWithImage);
		menu.add(new WMenuItem(new WDecoratedLabel(new WImage("/image/settings.png", "settings"), new WText("Settings Menu"), null)));
		itemWithImage = new WMenuItem(new WDecoratedLabel(new WImage("/image/flag.png", "flag")));
		itemWithImage.setAccessibleText("flag this view");
		menu.add(itemWithImage);
		return menu;
	}

	/**
	 * Adds an example menu item with the given text and an example action to the a parent component.
	 *
	 * @param parent the component to add the menu item to.
	 * @param text the text to display on the menu item.
	 * @param selectedMenuText the WText to display the selected menu item.
	 */
	private void addMenuItem(final WComponent parent, final String text,
			final WText selectedMenuText) {
		WMenuItem menuItem = new WMenuItem(text, new ExampleMenuAction(selectedMenuText));
		menuItem.setActionObject(text);
		if (parent instanceof WSubMenu) {
			((WSubMenu) parent).add(menuItem);
		} else {
			((WMenuItemGroup) parent).add(menuItem);
		}
	}

	/**
	 * Creates an example menu item using an image.
	 *
	 * @param resource the name of the image resource
	 * @param desc the description for the image
	 * @param cacheKey the cache key for this image
	 * @param selectedMenuText the WText to display the selected menu item.
	 * @return a menu item using an image
	 */
	private WMenuItem createImageMenuItem(final String resource, final String desc,
			final String cacheKey,
			final WText selectedMenuText) {
		WImage image = new WImage(resource, desc);
		image.setCacheKey(cacheKey);
		WDecoratedLabel label = new WDecoratedLabel(image, new WText(desc), null);
		WMenuItem menuItem = new WMenuItem(label, new ExampleMenuAction(selectedMenuText));
		menuItem.setActionObject(desc);
		return menuItem;
	}

	/**
	 * An action which toggles the disabled status of a component.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class ToggleDisabledAction implements Action {

		/**
		 * The disableable to toggle the disabled status of.
		 */
		private final Disableable[] disableables;

		/**
		 * Creates a ToggleDisabledMenuAction which toggles the disabled status of the given Disableable when the action
		 * executes.
		 *
		 * @param disableables the disableable to toggle the disabled status of.
		 */
		private ToggleDisabledAction(final Disableable... disableables) {
			this.disableables = disableables;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute(final ActionEvent event) {
			for (Disableable disableable : disableables) {
				disableable.setDisabled(!disableable.isDisabled());
			}
		}
	}
}
