package com.github.bordertech.wcomponents.util;

/**
 * Provides an enum which can be used to add HTML class attribute values to components.
 *
 * <p>
 * Included are values representing common icons. By default these will apply icons from the
 * <a href="http://fontawesome.io/icons/">Font Awesome</a>
 * library included with the default theme. Each icon may be individually overridden using a configuration property.
 * Each overrideable icon includes documentation of the configuration parameter used to set the icon's HTML class
 * attribute value. If the default icons are changed your theme must include a way to ensure the new icon is available.
 * This is safest if simply changing to an alternate
 * <a href="http://fontawesome.io/icons/">Font Awesome</a> icon.
 * </p>
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public enum HtmlClassProperties {

	/**
	 * Center align the content of any component.
	 *
	 * <p>
	 * Should not be used with {@link com.github.bordertech.wcomponents.WColumn}, {@link com.github.bordertech.wcomponents.WList},
	 * {@link com.github.bordertech.wcomponents.layout.ListLayout} or
	 * {@link com.github.bordertech.wcomponents.layout.ColumnLayout} as these all have the ability to set alignment
	 * intrinsically to the component/layout.
	 * </p>
	 */
	ALIGN_CENTER("wc-align-center"),
	/**
	 * Left align the content of any component. This is not terribly useful as this is the default alignment but can be
	 * used to re-set alignment if one has used {@link #ALIGN_CENTER} or {@link #ALIGN_CENTER}.
	 *
	 * <p>
	 * Should not be used with {@link com.github.bordertech.wcomponents.WColumn}, {@link com.github.bordertech.wcomponents.WList},
	 * {@link com.github.bordertech.wcomponents.layout.ListLayout} or
	 * {@link com.github.bordertech.wcomponents.layout.ColumnLayout} as these all have the ability to set alignment
	 * intrinsically to the component/layout.
	 * </p>
	 */
	ALIGN_LEFT("wc-align-left"),
	/**
	 * Right align the content of any component.
	 *
	 * <p>
	 * Should not be used with {@link com.github.bordertech.wcomponents.WColumn}, {@link com.github.bordertech.wcomponents.WList},
	 * {@link com.github.bordertech.wcomponents.layout.ListLayout} or
	 * {@link com.github.bordertech.wcomponents.layout.ColumnLayout} as these all have the ability to set alignment
	 * intrinsically to the component/layout.
	 * </p>
	 */
	ALIGN_RIGHT("wc-align-right"),
	/**
	 * Apply the theme default border to any component. This will only work if the border is not set by a more specific selector and
	 * will not apply padding so the border will sit directly against the content unless the component has default padding.
	 */
	BORDER("wc-border"),
	/**
	 * Allow the text content of this component to break at non-word boundaries. This should be used with extreme caution. If you apply
	 * it to a component in a horizontal FlowLayout you could end up with a long column of single letters.
	 * Best applied to WStyledText.
	 */
	BREAK_WORDS("wc-brkall"),
	/**
	 * Make a container scroll horizontally if its content does not fit. Probably only really useful for
	 * {@link com.github.bordertech.wcomponents.WTable}. Using this on any component which contains menus or date fields
	 * may have unexpected consequences.
	 */
	HORIZONTAL_SCROLL("wc-hscroll"),
	/**
	 * Move a component out of the viewport but leave it available for accessibility purposes. This is most useful for
	 * {@link com.github.bordertech.wcomponents.WHeading} to set accessibility markers but can be used for other
	 * explanatory components. Move a component out of the viewport but leave it available for accessibility purposes.
	 * This is most useful for {@link com.github.bordertech.wcomponents.WHeading} to set accessibility markers but can
	 * be used for other explanatory components.
	 *
	 * <p>
	 * For example one could use this with {@link com.github.bordertech.wcomponents.WStyledText} with whitespace
	 * PARAGRAPHS to add some explanatory information for visually impaired users if one's UI design is dependent on
	 * visual context.</p>
	 */
	OFF_SCREEN("wc-off"),
	/**
	 * Indicates that this component will opt-in to responsive design options. This is usually relevant to layout
	 * components such as
	 * {@link com.github.bordertech.wcomponents.WPanel}, {@link com.github.bordertech.wcomponents.WTable} or
	 * {@link com.github.bordertech.wcomponents.WRow}
	 */
	/**
	 * Indicates that this component will opt-in to responsive design options. This is usually relevant to layout
	 * components such as {@link com.github.bordertech.wcomponents.WPanel} or
	 * {@link com.github.bordertech.wcomponents.WRow}
	 */
	RESPOND("wc-respond"),
	// ICONS

	/**
	 * Apply an icon to a component. The icon will appear before any other content in the component. If you have other
	 * visible content in the component you may prefer to use {@link #ICON_BEFORE} which will put a small gap between
	 * the icon and the following content.
	 *
	 * <p>
	 * Must be used in conjunction with another value available to your theme. By default these are from
	 * <a href="http://fontawesome.io/icons/">Font Awesome</a>. This is extremely useful for
	 * {@link com.github.bordertech.wcomponents.WButton} or {@link com.github.bordertech.wcomponents.WLink} when the
	 * component has no other visible content.
	 * </p>
	 */
	ICON("wc-icon"),
	/**
	 * Apply an icon to the end of a component. The icon will appear after any other content in the component and there
	 * will be a small gap between the end of the content and the icon.
	 *
	 * <p>
	 * Must be used in conjunction with another value available to your theme. By default these are from
	 * <a href="http://fontawesome.io/icons/">Font Awesome</a>. This is extremely useful for
	 * {@link com.github.bordertech.wcomponents.WButton} or {@link com.github.bordertech.wcomponents.WLink} when the
	 * component has visible text.
	 * </p>
	 */
	ICON_AFTER("wc-icon wc-icon-after"),
	/**
	 * Apply an icon to the beginning of a component. The icon will appear before any other content in the component.and
	 * there will be a small gap between the icon and the content.
	 *
	 * <p>
	 * Must be used in conjunction with another value available to your theme. By default these are from
	 * <a href="http://fontawesome.io/icons/">Font Awesome</a>. This is extremely useful for
	 * {@link com.github.bordertech.wcomponents.WButton} or {@link com.github.bordertech.wcomponents.WLink} when the
	 * component has visible text.
	 * </p>
	 */
	ICON_BEFORE("wc-icon wc-icon-before"),
	// START OF ICONS: some helpers to attach common icons.

	/**
	 * Apply a "help" icon to a component. Most useful for {@link com.github.bordertech.wcomponents.WButton} or
	 * {@link com.github.bordertech.wcomponents.WLink}
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.help".</p>
	 */
	ICON_HELP("wc-icon ", ConfigurationProperties.getHtmlIconClassHelp()),
	/**
	 * Apply a "help" icon to the end of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.help".</p>
	 */
	ICON_HELP_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassHelp()),
	/**
	 * Apply a "help" icon to the beginning of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.help".</p>
	 */
	ICON_HELP_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassHelp()),
	// STATUS ICONS

	/**
	 * Apply an "information" icon to a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.info".</p>
	 */
	ICON_INFO("wc-icon ", ConfigurationProperties.getHtmlIconClassInfo()),
	/**
	 * Apply an "information" icon to the end of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.info".</p>
	 */
	ICON_INFO_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassInfo()),
	/**
	 * Apply an "information" icon to the beginning of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.info".</p>
	 */
	ICON_INFO_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassInfo()),
	/**
	 * Apply a "warning" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.warn".</p>
	 */
	ICON_WARN("wc-icon ", ConfigurationProperties.getHtmlIconClassWarn()),
	/**
	 * Apply a "warning" icon to the end of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.warn".</p>
	 */
	ICON_WARN_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassWarn()),
	/**
	 * Apply a "warning" icon to the end of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.warn".</p>
	 */
	ICON_WARN_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassWarn()),
	/**
	 * Apply an "error" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.error".</p>
	 */
	ICON_ERROR("wc-icon ", ConfigurationProperties.getHtmlIconClassError()),
	/**
	 * Apply an "error" icon to the end of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.error".</p>
	 */
	ICON_ERROR_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassError()),
	/**
	 * Apply an "error" icon to the beginning of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.error".</p>
	 */
	ICON_ERROR_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassError()),
	/**
	 * Apply a "success" icon to a component. Do not use with {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.success".</p>
	 */
	ICON_SUCCESS("wc-icon ", ConfigurationProperties.getHtmlIconClassSuccess()),
	/**
	 * Apply a "success" icon to the end of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.success".</p>
	 */
	ICON_SUCCESS_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassSuccess()),
	/**
	 * Apply a "success" icon to the beginning of a component. Do not use with
	 * {@link com.github.bordertech.wcomponents.WMessageBox}.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.success".</p>
	 */
	ICON_SUCCESS_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassSuccess()),
	// ACTION ICONS

	/**
	 * Add an "add" icon. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.add".</p>
	 */
	ICON_ADD("wc-icon ", ConfigurationProperties.getHtmlIconClassAdd()),
	/**
	 * Add an "add" icon to the end of a component. The default icon is the same as is used in WMultiTextField and
	 * WMultiDropdown.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.add".</p>
	 */
	ICON_ADD_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassAdd()),
	/**
	 * Add an "add" icon to the beginning of a component. The default icon is the same as is used in WMultiTextField and
	 * WMultiDropdown.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.add".</p>
	 */
	ICON_ADD_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassAdd()),
	/**
	 * Add a "delete" icon. The default icon is the same as is used in WMultiTextField and WMultiDropdown.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.delete".</p>
	 */
	ICON_DELETE("wc-icon ", ConfigurationProperties.getHtmlIconClassDelete()),
	/**
	 * Add a "delete" icon to the end of a component. The default icon is the same as is used in WMultiTextField and
	 * WMultiDropdown.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.delete".</p>
	 */
	ICON_DELETE_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassDelete()),
	/**
	 * Add a "delete" icon to the beginning of a component. The default icon is the same as is used in WMultiTextField
	 * and WMultiDropdown.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.delete".</p>
	 */
	ICON_DELETE_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassDelete()),
	/**
	 * Add an "edit" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.edit".</p>
	 */
	ICON_EDIT("wc-icon ", ConfigurationProperties.getHtmlIconClassEdit()),
	/**
	 * Add an "edit" icon to the end of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.edit".</p>
	 */
	ICON_EDIT_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassEdit()),
	/**
	 * Add an "edit" icon to the beginning of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.edit".</p>
	 */
	ICON_EDIT_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassEdit()),
	/**
	 * Add a "save" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.save".</p>
	 */
	ICON_SAVE("wc-icon ", ConfigurationProperties.getHtmlIconClassSave()),
	/**
	 * Add a "save" icon to the end of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.save".</p>
	 */
	ICON_SAVE_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassSave()),
	/**
	 * Add a "save" icon to the beginning of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.save".</p>
	 */
	ICON_SAVE_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassSave()),
	/**
	 * Add a "search" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.search".</p>
	 */
	ICON_SEARCH("wc-icon ", ConfigurationProperties.getHtmlIconClassSearch()),
	/**
	 * Add a "search" icon to the end of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.search".</p>
	 */
	ICON_SEARCH_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassSearch()),
	/**
	 * Add a "search" icon to the beginning of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.search".</p>
	 */
	ICON_SEARCH_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassSearch()),
	/**
	 * Add a "cancel" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.cancel".</p>
	 */
	ICON_CANCEL("wc-icon ", ConfigurationProperties.getHtmlIconClassCancel()),
	/**
	 * Add a "cancel" icon to the end of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.cancel".</p>
	 */
	ICON_CANCEL_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassCancel()),
	/**
	 * Add a "cancel" icon to the beginning of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.cancel".</p>
	 */
	ICON_CANCEL_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassCancel()),
	/**
	 * Add a "menu" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.menu".</p>
	 */
	ICON_MENU("wc-icon ", ConfigurationProperties.getHtmlIconClassMenu()),
	/**
	 * Add a "menu" icon to the end of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.menu".</p>
	 */
	ICON_MENU_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassMenu()),
	/**
	 * Add a "menu" icon to the beginning of a component.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.menu".</p>
	 */
	ICON_MENU_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassMenu()),
	/**
	 * Add a "print" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.print".</p>
	 */
	ICON_PRINT("wc-icon ", ConfigurationProperties.getHtmlIconClassPrint()),
	/**
	 * Add a "print" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.print".</p>
	 */
	ICON_PRINT_AFTER("wc-icon wc-icon-after ", ConfigurationProperties.getHtmlIconClassPrint()),
	/**
	 * Add a "print" icon.
	 * <p>
	 * To change the icon use configuration param "com.github.bordertech.wcomponents.HtmlClass.icon.print".</p>
	 */
	ICON_PRINT_BEFORE("wc-icon wc-icon-before ", ConfigurationProperties.getHtmlIconClassPrint());
	// END OF ICONS
	private final String className;

	/**
	 * Instantiate an item in the enum without config options.
	 *
	 * @param className the string value of the item
	 */
	HtmlClassProperties(final String className) {
		this.className = className;
	}

	/**
	 * Instantiate an item in the enum with config options.
	 *
	 * @param className the string value of the item
	 * @param iconClassName the classname for the icon
	 */
	HtmlClassProperties(final String className, final String iconClassName) {
		if (null == iconClassName) {
			this.className = className;
		} else {
			this.className = className.concat(iconClassName);
		}
	}

	/**
	 * @return the value to be used in the HTML element's class attribute
	 */
	@Override
	public String toString() {
		return this.className;
	}
}
