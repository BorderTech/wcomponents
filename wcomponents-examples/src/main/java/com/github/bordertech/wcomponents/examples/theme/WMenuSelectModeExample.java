package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenu.MenuType;
import com.github.bordertech.wcomponents.WMenu.SelectMode;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WMenuItemGroup;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSubMenu;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;
import java.util.List;

/**
 * Example showing how SelectMode can be used on {@link WMenu}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMenuSelectModeExample extends WContainer {

	/**
	 * Constructs the example.
	 */
	public WMenuSelectModeExample() {
		WTabSet tabs = new WTabSet();
		tabs.addTab(new MenuDetails(MenuType.BAR), "Bar", TabMode.LAZY);
		tabs.addTab(new MenuDetails(MenuType.COLUMN), "Column", TabMode.LAZY);
		tabs.addTab(new MenuDetails(MenuType.FLYOUT), "Flyout", TabMode.LAZY);
		tabs.addTab(new MenuDetails(MenuType.TREE), "Tree", TabMode.LAZY);
		add(tabs);
	}

	/**
	 * Example built for a particular Menu Type.
	 */
	public static class MenuDetails extends WPanel {

		/**
		 * Type of menu.
		 */
		private final WText typeText = new WText();
		/**
		 * Selected items.
		 */
		private final WText selectedText = new WText();

		/**
		 * Menu to be used.
		 */
		private final WMenu menu;
		/**
		 * SubMenu 1.
		 */
		private final WSubMenu sub1 = new WSubMenu("S1 - sub1");
		/**
		 * SubMenu 2 with group and nested subMenu.
		 */
		private final WSubMenu sub2 = new WSubMenu("S2sub2");
		/**
		 * SubMenu 3.
		 */
		private final WSubMenu sub3 = new WSubMenu("S3 - sub3");

		/**
		 * MenuItemGroup 1.
		 */
		private final WMenuItemGroup grp1 = new WMenuItemGroup("G1 - Group1");
		/**
		 * MenuItemGroup 2 - with subMenu.
		 */
		private final WMenuItemGroup grp2 = new WMenuItemGroup("G2 - Group2 (with submenu)");
		/**
		 * MenuItemGroup 3.
		 */
		private final WMenuItemGroup grp3 = new WMenuItemGroup("G3 - Group3");

		/**
		 * Drop1 - Select Mode.
		 */
		private final WDropdown drop1 = new WDropdown(WMenu.SelectMode.values());
		/**
		 * Drop2 - Select Mode.
		 */
		private final WDropdown drop2 = new WDropdown(WMenu.SelectMode.values());
		/**
		 * Drop3 - Select Mode.
		 */
		private final WDropdown drop3 = new WDropdown(WMenu.SelectMode.values());

		/**
		 * Group 1 - Item 1.
		 */
		private final WMenuItem g1Item1 = new WMenuItem("G1 - item1");
		/**
		 * Group 1 - Item 2.
		 */
		private final WMenuItem g1Item2 = new WMenuItem("G1 - item2");

		/**
		 * Group 2 - Item 1.
		 */
		private final WMenuItem g2Item1 = new WMenuItem("G2 - item1");
		/**
		 * Group 2 - Item 2.
		 */
		private final WMenuItem g2Item2 = new WMenuItem("G2 - item2");

		/**
		 * Group 3 - Item 1.
		 */
		private final WMenuItem g3Item1 = new WMenuItem("G3 - item1");
		/**
		 * Group 3 - Item 2.
		 */
		private final WMenuItem g3Item2 = new WMenuItem("G3 - item2");

		/**
		 * SubMenu 1 - Item 1.
		 */
		private final WMenuItem s1Item1 = new WMenuItem("S1 - item1");
		/**
		 * SubMenu 1 - Item 2.
		 */
		private final WMenuItem s1Item2 = new WMenuItem("S1 - item2");

		/**
		 * SubMenu 2 - Item 1.
		 */
		private final WMenuItem s2Item1 = new WMenuItem("S2 - item1");
		/**
		 * SubMenu 2 - Item 2.
		 */
		private final WMenuItem s2Item2 = new WMenuItem("S2 - item2");

		/**
		 * SubMenu 3 - Item 1.
		 */
		private final WMenuItem s3Item1 = new WMenuItem("S3 - item one");
		/**
		 * SubMenu 3 - Item 2.
		 */
		private final WMenuItem s3Item2 = new WMenuItem("S3 - item two which is much longer than its ancestors");

		/**
		 * Menu - Item 1.
		 */
		private final WMenuItem item1 = new WMenuItem("M - item1");
		/**
		 * Menu - Item 2.
		 */
		private final WMenuItem item2 = new WMenuItem("M - item2");
		/**
		 * Menu - Item 3.
		 */
		private final WMenuItem item3 = new WMenuItem("M - item3");
		/**
		 * Menu - Item 4.
		 */
		private final WMenuItem item4 = new WMenuItem("M - item4");

		/**
		 * The menu option in the configuration drop downs .
		 */
		public static final String MENU_OPTION = "Menu";

		/**
		 * Disable CheckBoxSelect.
		 */
		private final WCheckBoxSelect chkDisable = new WCheckBoxSelect(
				new String[]{MENU_OPTION, grp1.getHeadingText(),
					grp2.getHeadingText(),
					grp3.getHeadingText(),
					sub1.getText(), sub2.getText(),
					item1.getText(), item2.getText(),
					g1Item1.getText(), s1Item1.getText()});
		/**
		 * Selectable CheckBoxSelect.
		 */
		private final WCheckBoxSelect chkSelectable = new WCheckBoxSelect(new String[]{sub1.
			getText(), sub2.getText(),
			item1.getText(), item2.getText(),
			g1Item1.getText(),
			s1Item1.getText()});

		/**
		 * Disable CheckBox - used to show disable options.
		 */
		private final WCheckBox boxDisable = new WCheckBox();
		/**
		 * Selectable CheckBox - used to show selectable options..
		 */
		private final WCheckBox boxSelectable = new WCheckBox();

		/**
		 * Construct menu example.
		 *
		 * @param menuType the menu type to use.
		 */
		public MenuDetails(final MenuType menuType) {
			menu = new WMenu(menuType);
			grp1.add(g1Item1);
			grp1.add(g1Item2);

			grp2.add(g2Item1);
			sub1.add(s1Item1);
			sub1.add(s1Item2);
			grp2.add(sub1);
			grp2.add(g2Item2);

			sub2.add(s2Item1);
			grp3.add(g3Item1);
			grp3.add(g3Item2);
			sub2.add(grp3);
			sub2.add(s2Item2);

			sub3.add(s3Item1);
			sub3.add(s3Item2);
			sub2.add(sub3);

			menu.add(item1);
			menu.add(grp1);
			menu.add(item2);
			menu.add(grp2);
			menu.add(item3);
			menu.add(sub2);
			menu.add(item4);

			drop1.setActionOnChange(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					menu.setSelectMode((SelectMode) drop1.getSelected());
				}
			});
			drop2.setActionOnChange(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					sub1.setSelectMode((SelectMode) drop2.getSelected());
				}
			});
			drop3.setActionOnChange(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					sub2.setSelectMode((SelectMode) drop3.getSelected());
				}
			});

			WAjaxControl control1 = new WAjaxControl(drop1, this);
			WAjaxControl control2 = new WAjaxControl(drop2, this);
			WAjaxControl control3 = new WAjaxControl(drop3, this);
			add(control1);
			add(control2);
			add(control3);

			WFieldLayout layout1 = new WFieldLayout();
			layout1.addField("Type", typeText);
			layout1.addField("Selected", selectedText);

			WFieldLayout layout2 = new WFieldLayout();
			layout2.addField("Menu mode", drop1);
			layout2.addField("Sub1 mode", drop2);
			layout2.addField("Sub2 mode", drop3);

			createHideShowControl(layout2, boxDisable, chkDisable, "Disable/Enable", "Disable");
			createHideShowControl(layout2, boxSelectable, chkSelectable, "Override Selectable",
					"Selectable");

			add(menu);
			add(new WHeading(WHeading.SECTION, "Details"));
			add(layout1);
			add(new WHeading(WHeading.SECTION, "Configuration"));
			add(layout2);
			add(new WButton("Submit"));

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			if (!isInitialised()) {
				typeText.setText(menu.getType().toString());
				menu.setSelectMode((SelectMode) drop1.getSelected());
				sub1.setSelectMode((SelectMode) drop2.getSelected());
				sub2.setSelectMode((SelectMode) drop3.getSelected());
				setInitialised(true);
			}

			handleSelectedText();
			handleSelectable();
			handleDisable();
		}

		/**
		 * Setup the field and subordinate control for the configuration options.
		 *
		 * @param layout the field layout
		 * @param box the checkBox used as the trigger
		 * @param chk the CheckBoxSelect to add into the field
		 * @param boxLabel the checkBox label
		 * @param chkLabel the CheckBoxSelect label
		 */
		private void createHideShowControl(final WFieldLayout layout, final WCheckBox box,
				final WCheckBoxSelect chk,
				final String boxLabel, final String chkLabel) {
			layout.addField(boxLabel, box);
			WField field2 = layout.addField(chkLabel, chk);
			SubordinateBuilder builder = new SubordinateBuilder();
			builder.condition().equals(box, "true");
			builder.whenTrue().show(field2);
			builder.whenFalse().hide(field2);
			add(builder.build());
		}

		/**
		 * Handle selected text.
		 */
		private void handleSelectedText() {
			List<WComponent> list = menu.getSelectedItems();
			StringBuffer selected = new StringBuffer();
			for (WComponent comp : list) {
				if (comp instanceof WMenuItem) {
					WMenuItem item = (WMenuItem) comp;
					selected.append(item.getText()).append(", ");
				} else if (comp instanceof WSubMenu) {
					WSubMenu item = (WSubMenu) comp;
					selected.append(item.getText()).append(", ");
				}
			}
			selectedText.setText(selected.toString());
		}

		/**
		 * Handle selectable.
		 */
		private void handleSelectable() {
			if (boxSelectable.isSelected()) {
				List<?> selected = chkSelectable.getSelected();
				sub1.setSelectable(selected.contains(sub1.getText()));
				sub2.setSelectable(selected.contains(sub2.getText()));
				item1.setSelectable(selected.contains(item1.getText()));
				item2.setSelectable(selected.contains(item2.getText()));
				g1Item1.setSelectable(selected.contains(g1Item1.getText()));
				s1Item1.setSelectable(selected.contains(s1Item1.getText()));
			} else {
				sub1.setSelectable(false);
				sub2.setSelectable(false);
				item1.setSelectable(null);
				item2.setSelectable(null);
				g1Item1.setSelectable(null);
				s1Item1.setSelectable(null);
			}
		}

		/**
		 * Handle disabled.
		 */
		private void handleDisable() {
			List<?> selected = chkDisable.getSelected();
			menu.setDisabled((selected.contains(MENU_OPTION) && boxDisable.isSelected()));
			grp1.setDisabled((selected.contains(grp1.getHeadingText()) && boxDisable.isSelected()));
			grp2.setDisabled((selected.contains(grp2.getHeadingText()) && boxDisable.isSelected()));
			grp3.setDisabled((selected.contains(grp3.getHeadingText()) && boxDisable.isSelected()));
			sub1.setDisabled((selected.contains(sub1.getText()) && boxDisable.isSelected()));
			sub2.setDisabled((selected.contains(sub2.getText()) && boxDisable.isSelected()));
			item1.setDisabled((selected.contains(item1.getText()) && boxDisable.isSelected()));
			item2.setDisabled((selected.contains(item2.getText()) && boxDisable.isSelected()));
			g1Item1.setDisabled((selected.contains(g1Item1.getText()) && boxDisable.isSelected()));
			s1Item1.setDisabled((selected.contains(s1Item1.getText()) && boxDisable.isSelected()));
		}
	}
}
