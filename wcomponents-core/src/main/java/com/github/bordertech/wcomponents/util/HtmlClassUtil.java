package com.github.bordertech.wcomponents.util;

import org.apache.commons.configuration.Configuration;


/**
 * Provides an enum which can be used to add HTML class attribute values to components.
 *
 * <p>
 * Included are a set of helpers to attach icons. By default these will apply icons from the <a href="http://fontawesome.io/icons/">Font Awesome</a>
 * library included with the default theme. Each icon may be individually overridden using a configuration property. Each overrideable icon
 * includes documentation of the configuration parameter used to set the icon's HTML class attribute value. If the default icons are changed your
 * theme must include a way to ensure the new icon is available. This is safest if simply changing to an alternate
 * <a href="http://fontawesome.io/icons/">Font Awesome</a> icon.
 * </p>
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HtmlClassUtil {
	/**
	 * The configuration instance.
	 */
	private static final Configuration CONFIG = Config.getInstance();

	/**
	 * The help icon HTML class. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> question mark icon.
	 */
	private static final String HELP_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.help", "fa-question-circle");

	/**
	 * The info icon HTML class. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon as per the default theme.
	 */
	private static final String INFO_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.info", "fa-info-circle");

	/**
	 * The warning icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon as per the default theme.
	 */
	private static final String WARN_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.warn", "fa-exclamation-triangle");

	/**
	 * The error icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon as per the default theme.
	 */
	private static final String ERROR_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.error", "fa-minus-circle");

	/**
	 * The success icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon as per the default theme.
	 */
	private static final String SUCCESS_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.success", "fa-check-circle");

	// ACTION ICONS

	/**
	 * The save icon HTML class. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon.
	 */
	private static final String SAVE_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.save", "fa-floppy-o");

	/**
	 * The search icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon.
	 */
	private static final String SEARCH_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.search", "fa-search");

	/**
	 * The cancel icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon.
	 */
	private static final String CANCEL_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.cancel", "fa-ban");

	/**
	 * The add icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon. The default icon is the same as is used in
	 * {@link com.github.bordertech.wcomponents.WMultiTextField} and {@link com.github.bordertech.wcomponents.WMultiDropdown}.
	 */
	private static final String ADD_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.add", "fa-plus-square");

	/**
	 * The delete icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon. The default icon is the same as is used in
	 * {@link com.github.bordertech.wcomponents.WMultiTextField} and {@link com.github.bordertech.wcomponents.WMultiDropdown}.
	 */
	private static final String DELETE_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.delete", "fa-minus-square");

	/**
	 * The edit icon. Defaults to a <a href="http://fontawesome.io/icons/">Font Awesome</a> icon.
	 */
	private static final String EDIT_ICON = CONFIG.getString("com.github.bordertech.wcomponents.HtmlClass.icon.edit", "fa-pencil");

	/**
	 * Provides a set of HTML class attribute values.
	 */
	public enum HtmlClassName {

		/**
		 * Center align the content of any component.
		 *
		 * <p>
		 * Should not be used with {@link com.github.bordertech.wcomponents.WColumn}, {@link com.github.bordertech.wcomponents.WList},
		 * {@link com.github.bordertech.wcomponents.layout.ListLayout} or {@link com.github.bordertech.wcomponents.layout.ColumnLayout} as these all
		 * have the ability to set alignment intrinsically to the component/layout.
		 * </p>
		 */
		ALIGN_CENTER ("wc-align-center"),

		/**
		 * Left align the content of any component. This is not terribly useful as this is the default alignment but can be used to re-set alignment
		 * if one has used {@link #ALIGN_CENTER} or {@link #ALIGN_CENTER}.
		 *
		 * <p>
		 * Should not be used with {@link com.github.bordertech.wcomponents.WColumn}, {@link com.github.bordertech.wcomponents.WList},
		 * {@link com.github.bordertech.wcomponents.layout.ListLayout} or {@link com.github.bordertech.wcomponents.layout.ColumnLayout} as these all
		 * have the ability to set alignment intrinsically to the component/layout.
		 * </p>
		 */
		ALIGN_LEFT ("wc-align-left"),

		/**
		 * Right align the content of any component.
		 *
		 * <p>
		 * Should not be used with {@link com.github.bordertech.wcomponents.WColumn}, {@link com.github.bordertech.wcomponents.WList},
		 * {@link com.github.bordertech.wcomponents.layout.ListLayout} or {@link com.github.bordertech.wcomponents.layout.ColumnLayout} as these all
		 * have the ability to set alignment intrinsically to the component/layout.
		 * </p>
		 */
		ALIGN_RIGHT ("wc-align-center"),

		/**
		 * Make a container scroll horizontally if its content does not fit. Probably only really useful for
		 * {@link com.github.bordertech.wcomponents.WTable}. Using this on any component which contains menus or date fields may have unexpected
		 * consequences.
		 */
		HORIZONTAL_SCROLL ("wc-hscroll"),

		/**
		 * Move a component out of the viewport but leave it available for accessibility purposes. This is most useful for
		 * {@link com.github.bordertech.wcomponents.WHeading} to set accessibility markers but can be used for other explanatory components.
		 */
		OFF_SCREEN ("wc-off"),

		/**
		 * Indicates that this component will opt-in to responsive design options. This is usually relevant to layout components such as
		 * {@link com.github.bordertech.wcomponents.WPanel}, {@link com.github.bordertech.wcomponents.WTable} or
		 * {@link com.github.bordertech.wcomponents.WRow}
		 */
		RESPOND ("wc-respond"),

		// ICONS

		/**
		 * Apply an icon to a component. The icon will appear before any other content in the component. If you have other visible content in the
		 * component you may prefer to use {@link #ICON_BEFORE} which will put a small gap between the icon and the following content.
		 *
		 * <p>
		 * Must be used in conjunction with  another value available to your theme. By default these are from
		 * <a href="http://fontawesome.io/icons/">Font Awesome</a>. This is extremely useful for {@link com.github.bordertech.wcomponents.WButton} or
		 * {@link com.github.bordertech.wcomponents.WLink} when the component has no other visible content.
		 * </p>
		 */
		ICON ("wc-icon"),

		/**
		 * Apply an icon to the end of a component. The icon will appear after any other content in the component and there will be a small gap
		 * between the end of the content and the icon.
		 *
		 * <p>
		 * Must be used in conjunction with  another value available to your theme. By default these are from
		 * <a href="http://fontawesome.io/icons/">Font Awesome</a>. This is extremely useful for {@link com.github.bordertech.wcomponents.WButton} or
		 * {@link com.github.bordertech.wcomponents.WLink} when the component has visible text.
		 * </p>
		 */
		ICON_AFTER ("wc-icon-after"),

		/**
		 * Apply an icon to the beginning of a component. The icon will appear before any other content in the component.and there will be a small gap
		 * between the icon and the content.
		 *
		 * <p>
		 * Must be used in conjunction with  another value available to your theme. By default these are from
		 * <a href="http://fontawesome.io/icons/">Font Awesome</a>. This is extremely useful for {@link com.github.bordertech.wcomponents.WButton} or
		 * {@link com.github.bordertech.wcomponents.WLink} when the component has visible text.
		 * </p>
		 */
		ICON_BEFORE ("wc-icon-before"),

		// START OF ICONS: some helpers to attach common icons.

		/**
		 * Apply a "help" icon to a component. Most useful for {@link com.github.bordertech.wcomponents.WButton} or
		 * {@link com.github.bordertech.wcomponents.WLink}
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.help".</p>
		 */
		ICON_HELP ("wc-icon " + HELP_ICON),

		/**
		 * Apply a "help" icon to the end of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.help".</p>
		 */
		ICON_HELP_AFTER ("wc-icon-after " + HELP_ICON),

		/**
		 * Apply a "help" icon to the beginning of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.help".</p>
		 */
		ICON_HELP_BEFORE ("wc-icon-before " + HELP_ICON),

		// STATUS ICONS

		/**
		 * Apply an "information" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.info".</p>
		 */
		ICON_INFO ("wc-icon " + INFO_ICON),

		/**
		 * Apply an "information" icon to the end of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.info".</p>
		 */
		ICON_INFO_AFTER ("wc-icon-after " + INFO_ICON),

		/**
		 * Apply an "information" icon to the beginning of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.info".</p>
		 */
		ICON_INFO_BEFORE ("wc-icon-before " + INFO_ICON),

		/**
		 * Apply a "warning" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.warn".</p>
		 */
		ICON_WARN ("wc-icon " + WARN_ICON),

		/**
		 * Apply a "warning" icon to the end of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.warn".</p>
		 */
		ICON_WARN_AFTER ("wc-icon-after " + WARN_ICON),

		/**
		 * Apply a "warning" icon to the end of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.warn".</p>
		 */
		ICON_WARN_BEFORE ("wc-icon-before " + WARN_ICON),

		/**
		 * Apply an "error" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.error".</p>
		 */
		ICON_ERROR ("wc-icon " + ERROR_ICON),

		/**
		 * Apply an "error" icon to the end of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.error".</p>
		 */
		ICON_ERROR_AFTER ("wc-icon-after " + ERROR_ICON),

		/**
		 * Apply an "error" icon to the beginning of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.error".</p>
		 */
		ICON_ERROR_BEFORE ("wc-icon-before " + ERROR_ICON),

		/**
		 * Apply a "success" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.success".</p>
		 */
		ICON_SUCCESS ("wc-icon " + SUCCESS_ICON),

		/**
		 * Apply a "success" icon to the end of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.success".</p>
		 */
		ICON_SUCCESS_AFTER ("wc-icon-after " + SUCCESS_ICON),

		/**
		 * Apply a "success" icon to the beginning of a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.success".</p>
		 */
		ICON_SUCCESS_BEFORE ("wc-icon-before " + SUCCESS_ICON),

		// ACTION ICONS

		/**
		 * Add an "add" icon. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.add".</p>
		 */
		ICON_ADD ("wc-icon " + ADD_ICON),

		/**
		 * Add an "add" icon to the end of a component. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.add".</p>
		 */
		ICON_ADD_AFTER ("wc-icon-after " + ADD_ICON),

		/**
		 * Add an "add" icon to the beginning of a component. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.add".</p>
		 */
		ICON_ADD_BEFORE ("wc-icon-before " + ADD_ICON),

		/**
		 * Add a "delete" icon. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.delete".</p>
		 */
		ICON_DELETE ("wc-icon " + DELETE_ICON),

		/**
		 * Add a "delete" icon to the end of a component. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.delete".</p>
		 */
		ICON_DELETE_AFTER ("wc-icon-after " + DELETE_ICON),

		/**
		 * Add a "delete" icon to the beginning of a component. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.delete".</p>
		 */
		ICON_DELETE_BEFORE ("wc-icon-before " + DELETE_ICON),

		/**
		 * Add an "edit" icon.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.edit".</p>
		 */
		ICON_EDIT ("wc-icon " + EDIT_ICON),

		/**
		 * Add an "edit" icon to the end of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.edit".</p>
		 */
		ICON_EDIT_AFTER ("wc-icon-after " + EDIT_ICON),

		/**
		 * Add an "edit" icon to the beginning of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.edit".</p>
		 */
		ICON_EDIT_BEFORE ("wc-icon-before " + EDIT_ICON),

		/**
		 * Add a "save" icon.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.save".</p>
		 */
		ICON_SAVE ("wc-icon " + SAVE_ICON),

		/**
		 * Add a "save" icon to the end of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.save".</p>
		 */
		ICON_SAVE_AFTER ("wc-icon-after " + SAVE_ICON),

		/**
		 * Add a "save" icon to the beginning of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.save".</p>
		 */
		ICON_SAVE_BEFORE ("wc-icon-before " + SAVE_ICON),

		/**
		 * Add a "search" icon.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.search".</p>
		 */
		ICON_SEARCH ("wc-icon " + SEARCH_ICON),

		/**
		 * Add a "search" icon to the end of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.search".</p>
		 */
		ICON_SEARCH_AFTER ("wc-icon-after " + SEARCH_ICON),

		/**
		 * Add a "search" icon to the beginning of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.search".</p>
		 */
		ICON_SEARCH_BEFORE ("wc-icon-before " + SEARCH_ICON),

		/**
		 * Add a "cancel" icon.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.cancel".</p>
		 */
		ICON_CANCEL ("wc-icon " + CANCEL_ICON),

		/**
		 * Add a "cancel" icon to the end of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.cancel".</p>
		 */
		ICON_CANCEL_AFTER ("wc-icon-after " + CANCEL_ICON),

		/**
		 * Add a "cancel" icon to the beginning of a component.
		 * <p>To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.cancel".</p>
		 */
		ICON_CANCEL_BEFORE ("wc-icon-before " + CANCEL_ICON);

		// END OF ICONS

		private final String className;

		/**
		 * Instantiate each item in the enum.
		 * @param className the string value of the item
		 */
		HtmlClassName(final String className) {
			this.className = className;
		}

		/**
		 * @return the value to be used in the HTML element's class attribute
		 */
		@Override
		public String toString() {
			return this.className;
		}
	}
}
