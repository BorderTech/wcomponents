package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.examples.AppPreferenceParameterExample;
import com.github.bordertech.wcomponents.examples.AutoReFocusExample;
import com.github.bordertech.wcomponents.examples.AutoReFocusRepeaterExample;
import com.github.bordertech.wcomponents.examples.ButtonOptionsExample;
import com.github.bordertech.wcomponents.examples.EntryFieldExample;
import com.github.bordertech.wcomponents.examples.ErrorGenerator;
import com.github.bordertech.wcomponents.examples.ForwardExample;
import com.github.bordertech.wcomponents.examples.HtmlClassPropertiesExample;
import com.github.bordertech.wcomponents.examples.HtmlInjector;
import com.github.bordertech.wcomponents.examples.I18nExample;
import com.github.bordertech.wcomponents.examples.InfoDump;
import com.github.bordertech.wcomponents.examples.InputBeanBindingExample;
import com.github.bordertech.wcomponents.examples.KitchenSink;
import com.github.bordertech.wcomponents.examples.LinkOptionsExample;
import com.github.bordertech.wcomponents.examples.LoadAjaxControlsExample;
import com.github.bordertech.wcomponents.examples.MarginExample;
import com.github.bordertech.wcomponents.examples.MultiPollingExample;
import com.github.bordertech.wcomponents.examples.PatternValidationExample;
import com.github.bordertech.wcomponents.examples.SimpleCancelButtonExample;
import com.github.bordertech.wcomponents.examples.SimpleFileUpload;
import com.github.bordertech.wcomponents.examples.TextAreaExample;
import com.github.bordertech.wcomponents.examples.TextDuplicator;
import com.github.bordertech.wcomponents.examples.TextDuplicatorHandleRequestImpl;
import com.github.bordertech.wcomponents.examples.TextDuplicatorVelocity2;
import com.github.bordertech.wcomponents.examples.TextDuplicatorVelocityImpl;
import com.github.bordertech.wcomponents.examples.TextFieldExample;
import com.github.bordertech.wcomponents.examples.WAbbrTextExample;
import com.github.bordertech.wcomponents.examples.WApplicationExample;
import com.github.bordertech.wcomponents.examples.WAudioExample;
import com.github.bordertech.wcomponents.examples.WButtonActionExample;
import com.github.bordertech.wcomponents.examples.WButtonExample;
import com.github.bordertech.wcomponents.examples.WCheckBoxTriggerActionExample;
import com.github.bordertech.wcomponents.examples.WContentExample;
import com.github.bordertech.wcomponents.examples.WDataListServletExample;
import com.github.bordertech.wcomponents.examples.WDefinitionListExample;
import com.github.bordertech.wcomponents.examples.WDialogExample;
import com.github.bordertech.wcomponents.examples.WDropdownExample;
import com.github.bordertech.wcomponents.examples.WDropdownOptionsExample;
import com.github.bordertech.wcomponents.examples.WDropdownSpaceHandlingExample;
import com.github.bordertech.wcomponents.examples.WDropdownSpecialCharHandlingExample;
import com.github.bordertech.wcomponents.examples.WDropdownSubmitOnChangeExample;
import com.github.bordertech.wcomponents.examples.WDropdownTriggerActionExample;
import com.github.bordertech.wcomponents.examples.WFigureExample;
import com.github.bordertech.wcomponents.examples.WImageCachedExample;
import com.github.bordertech.wcomponents.examples.WImageExample;
import com.github.bordertech.wcomponents.examples.WLinkActionExample;
import com.github.bordertech.wcomponents.examples.WMultiFileWidgetAjaxExample;
import com.github.bordertech.wcomponents.examples.WMultiSelectExample;
import com.github.bordertech.wcomponents.examples.WNumberFieldExample;
import com.github.bordertech.wcomponents.examples.WPanelExample;
import com.github.bordertech.wcomponents.examples.WPasswordFieldExample;
import com.github.bordertech.wcomponents.examples.WPopupExample;
import com.github.bordertech.wcomponents.examples.WRadioButtonInRepeater;
import com.github.bordertech.wcomponents.examples.WRadioButtonTriggerActionExample;
import com.github.bordertech.wcomponents.examples.WSectionExample;
import com.github.bordertech.wcomponents.examples.WShufflerExample;
import com.github.bordertech.wcomponents.examples.WSingleSelectExample;
import com.github.bordertech.wcomponents.examples.WSkipLinksExample;
import com.github.bordertech.wcomponents.examples.WStyledTextFontAwesomeExample;
import com.github.bordertech.wcomponents.examples.WStyledTextOptionsExample;
import com.github.bordertech.wcomponents.examples.WSuggestionsExample;
import com.github.bordertech.wcomponents.examples.WTemplateExample;
import com.github.bordertech.wcomponents.examples.WTextExample;
import com.github.bordertech.wcomponents.examples.WTextFieldExample;
import com.github.bordertech.wcomponents.examples.WTimeoutWarningDefaultExample;
import com.github.bordertech.wcomponents.examples.WTimeoutWarningExample;
import com.github.bordertech.wcomponents.examples.WTimeoutWarningOptionsExample;
import com.github.bordertech.wcomponents.examples.WTreeExample;
import com.github.bordertech.wcomponents.examples.WVideoExample;
import com.github.bordertech.wcomponents.examples.WWindowExample;
import com.github.bordertech.wcomponents.examples.WhiteSpaceExample;
import com.github.bordertech.wcomponents.examples.datatable.DataTableBeanExample;
import com.github.bordertech.wcomponents.examples.datatable.DataTableOptionsExample;
import com.github.bordertech.wcomponents.examples.datatable.SelectableDataTableExample;
import com.github.bordertech.wcomponents.examples.datatable.SimpleEditableDataTableExample;
import com.github.bordertech.wcomponents.examples.datatable.SimpleRowEditingTableExample;
import com.github.bordertech.wcomponents.examples.datatable.TableCellWithActionExample;
import com.github.bordertech.wcomponents.examples.datatable.TreeTableExample;
import com.github.bordertech.wcomponents.examples.datatable.TreeTableHierarchyExample;
import com.github.bordertech.wcomponents.examples.datatable.WDataTableContentExample;
import com.github.bordertech.wcomponents.examples.datatable.WDataTableExample;
import com.github.bordertech.wcomponents.examples.layout.BorderLayoutExample;
import com.github.bordertech.wcomponents.examples.layout.ColumnLayoutExample;
import com.github.bordertech.wcomponents.examples.layout.FlowLayoutExample;
import com.github.bordertech.wcomponents.examples.layout.GridLayoutExample;
import com.github.bordertech.wcomponents.examples.layout.GridLayoutOptionsExample;
import com.github.bordertech.wcomponents.examples.layout.ListLayoutExample;
import com.github.bordertech.wcomponents.examples.layout.ListLayoutOptionExample;
import com.github.bordertech.wcomponents.examples.menu.ColumnMenuExample;
import com.github.bordertech.wcomponents.examples.menu.MenuBarExample;
import com.github.bordertech.wcomponents.examples.menu.MenuFlyoutExample;
import com.github.bordertech.wcomponents.examples.menu.MenuItemActionMessagesExample;
import com.github.bordertech.wcomponents.examples.menu.TreeMenuExample;
import com.github.bordertech.wcomponents.examples.othersys.LinkExamples;
import com.github.bordertech.wcomponents.examples.repeater.RepeaterExample;
import com.github.bordertech.wcomponents.examples.repeater.RepeaterExampleWithEditableRows;
import com.github.bordertech.wcomponents.examples.repeater.link.RepeaterLinkExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlAllExamples;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlCrtWDropdownExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlGroupExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlMandatoryExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlOptionsExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleCheckBoxSelectExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleDisableExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleWDropdownExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleWFieldExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleWMultiSelectExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSuite;
import com.github.bordertech.wcomponents.examples.table.FilterableTableExample;
import com.github.bordertech.wcomponents.examples.table.SimpleEditableTableExample;
import com.github.bordertech.wcomponents.examples.table.SimpleExpandableContentTableExample;
import com.github.bordertech.wcomponents.examples.table.SimpleExpandableTableExample;
import com.github.bordertech.wcomponents.examples.table.SimplePaginationTableExample;
import com.github.bordertech.wcomponents.examples.table.SimplePaginationWithRowOptionsTableExample;
import com.github.bordertech.wcomponents.examples.table.SimpleSelectableTableExample;
import com.github.bordertech.wcomponents.examples.table.SimpleSortingTableExample;
import com.github.bordertech.wcomponents.examples.table.SimpleTableExample;
import com.github.bordertech.wcomponents.examples.table.TableBeanProviderExample;
import com.github.bordertech.wcomponents.examples.table.TableCellActionExample;
import com.github.bordertech.wcomponents.examples.table.TableContentExample;
import com.github.bordertech.wcomponents.examples.table.TableExpandableContentModelExample;
import com.github.bordertech.wcomponents.examples.table.TableRowEditingAjaxExample;
import com.github.bordertech.wcomponents.examples.table.TableScrollableModelExample;
import com.github.bordertech.wcomponents.examples.table.WTableColumnAlignmentExample;
import com.github.bordertech.wcomponents.examples.table.WTableColumnWidthExample;
import com.github.bordertech.wcomponents.examples.table.WTableExample;
import com.github.bordertech.wcomponents.examples.table.WTableOptionsExample;
import com.github.bordertech.wcomponents.examples.theme.AccessKeyExample;
import com.github.bordertech.wcomponents.examples.theme.NestedTabSetExample;
import com.github.bordertech.wcomponents.examples.theme.WButtonDefaultSubmitExample;
import com.github.bordertech.wcomponents.examples.theme.WCancelButtonExample;
import com.github.bordertech.wcomponents.examples.theme.WCheckBoxExample;
import com.github.bordertech.wcomponents.examples.theme.WCheckBoxSelectExample;
import com.github.bordertech.wcomponents.examples.theme.WCollapsibleExample;
import com.github.bordertech.wcomponents.examples.theme.WCollapsibleGroupExample;
import com.github.bordertech.wcomponents.examples.theme.WConfirmationButtonExample;
import com.github.bordertech.wcomponents.examples.theme.WDateFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WEmailFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WFieldLayoutExample;
import com.github.bordertech.wcomponents.examples.theme.WFieldNestedExample;
import com.github.bordertech.wcomponents.examples.theme.WFieldSetExample;
import com.github.bordertech.wcomponents.examples.theme.WHeadingExample;
import com.github.bordertech.wcomponents.examples.theme.WHiddenCommentExample;
import com.github.bordertech.wcomponents.examples.theme.WLabelExample;
import com.github.bordertech.wcomponents.examples.theme.WListExample;
import com.github.bordertech.wcomponents.examples.theme.WListOptionsExample;
import com.github.bordertech.wcomponents.examples.theme.WMenuSelectModeExample;
import com.github.bordertech.wcomponents.examples.theme.WMessageBoxExample;
import com.github.bordertech.wcomponents.examples.theme.WMessagesExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiDropdownExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiFileWidgetExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiSelectPairExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiTextFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WPanelMarginExample;
import com.github.bordertech.wcomponents.examples.theme.WPanelTypeExample;
import com.github.bordertech.wcomponents.examples.theme.WPartialDateFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WPhoneNumberFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WProgressBarExample;
import com.github.bordertech.wcomponents.examples.theme.WRadioButtonExample;
import com.github.bordertech.wcomponents.examples.theme.WRadioButtonSelectExample;
import com.github.bordertech.wcomponents.examples.theme.WRowExample;
import com.github.bordertech.wcomponents.examples.theme.WSelectToggleExample;
import com.github.bordertech.wcomponents.examples.theme.WTabAndCollapsibleExample;
import com.github.bordertech.wcomponents.examples.theme.WTabExample;
import com.github.bordertech.wcomponents.examples.theme.WTabSetExample;
import com.github.bordertech.wcomponents.examples.theme.WTabSetTriggerActionExample;
import com.github.bordertech.wcomponents.examples.theme.WToggleButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxPollingWButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxReplaceControllerExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWCheckboxExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWCollapsibleExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWDropdownExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWPaginationExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWPanelExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWRadioButtonSelectExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWRepeaterExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.WCollapsibleOptionsExample;
import com.github.bordertech.wcomponents.examples.validation.ValidationExamples;
import com.github.bordertech.wcomponents.examples.validation.basic.BasicDiagnosticComponentExample;
import com.github.bordertech.wcomponents.examples.validation.basic.BasicFieldsValidationExample2;
import com.github.bordertech.wcomponents.examples.validation.fields.FieldValidation;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;

/**
 * ExampleData contains the information necessary to describe an example. It also provides some groupings for the
 * standard WComponent examples.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public final class ExampleData implements Serializable {

	/**
	 * AJAX examples.
	 */
	public static final ExampleData[] AJAX_EXAMPLES = new ExampleData[]{
		new ExampleData("Button", AjaxWButtonExample.class),
		new ExampleData("Checkbox", AjaxWCheckboxExample.class),
		new ExampleData("Collapsible", AjaxWCollapsibleExample.class),
		new ExampleData("Collapsible Options Example", WCollapsibleOptionsExample.class),
		new ExampleData("Dropdown", AjaxWDropdownExample.class),
		new ExampleData("Pagination", AjaxWPaginationExample.class),
		new ExampleData("Polling button", AjaxPollingWButtonExample.class),
		new ExampleData("Lazy Panel", AjaxWPanelExample.class),
		new ExampleData("RadioButtonSelect", AjaxWRadioButtonSelectExample.class),
		new ExampleData("Repeater", AjaxWRepeaterExample.class),
		new ExampleData("Suggestions", WSuggestionsExample.class),
		new ExampleData("WDataListServer", WDataListServletExample.class),
		new ExampleData("Multi pollers", MultiPollingExample.class),
		new ExampleData("Load AJAX controls via AJAX", LoadAjaxControlsExample.class),
		new ExampleData("Ajax controllers replaced via ajax", AjaxReplaceControllerExample.class)
	};

	/**
	 * Examples showing (interactive) form controls.
	 */
	public static final ExampleData[] FORM_CONTROLS = new ExampleData[]{
		new ExampleData("Overview", EntryFieldExample.class),
		new ExampleData("Button", WButtonExample.class),
		new ExampleData("Button action example", WButtonActionExample.class),
		new ExampleData("Button configuration options", ButtonOptionsExample.class),
		new ExampleData("Button default submit", WButtonDefaultSubmitExample.class),
		new ExampleData("Cancel button", WCancelButtonExample.class),
		new ExampleData("CheckBox", WCheckBoxExample.class),
		new ExampleData("Checkbox (action on change)", WCheckBoxTriggerActionExample.class),
		new ExampleData("CheckBoxSelect", WCheckBoxSelectExample.class),
		new ExampleData("Confirmation button", WConfirmationButtonExample.class),
		new ExampleData("Date field", WDateFieldExample.class),
		new ExampleData("Dropdown", WDropdownExample.class),
		new ExampleData("Dropdown (action on change)", WDropdownTriggerActionExample.class),
		new ExampleData("Dropdown (submit on change)", WDropdownSubmitOnChangeExample.class),
		new ExampleData("Dropdown (space handling)", WDropdownSpaceHandlingExample.class),
		new ExampleData("Dropdown (special char handling)", WDropdownSpecialCharHandlingExample.class),
		new ExampleData("Dropdown configuration options", WDropdownOptionsExample.class),
		new ExampleData("Email field", WEmailFieldExample.class),
		new ExampleData("File upload (simple)", SimpleFileUpload.class),
		new ExampleData("Labels", WLabelExample.class),
		new ExampleData("Multi-dropdown", WMultiDropdownExample.class),
		new ExampleData("Multi-file upload", WMultiFileWidgetExample.class),
		new ExampleData("Multi-file ajax", WMultiFileWidgetAjaxExample.class),
		new ExampleData("Multi Select", WMultiSelectExample.class),
		new ExampleData("Multi-select pair", WMultiSelectPairExample.class),
		new ExampleData("Multi-text field", WMultiTextFieldExample.class),
		new ExampleData("Number Field", WNumberFieldExample.class),
		new ExampleData("Partial date field", WPartialDateFieldExample.class),
		new ExampleData("WPasswordField", WPasswordFieldExample.class),
		new ExampleData("Phone Number Field", WPhoneNumberFieldExample.class),
		new ExampleData("RadioButton", WRadioButtonExample.class),
		new ExampleData("RadioButton (action on change)", WRadioButtonTriggerActionExample.class),
		new ExampleData("RadioButton in a repeater", WRadioButtonInRepeater.class),
		new ExampleData("RadioButtonSelect", WRadioButtonSelectExample.class),
		new ExampleData("Select toggle", WSelectToggleExample.class),
		new ExampleData("Shuffler", WShufflerExample.class),
		new ExampleData("Simple Cancel Button", SimpleCancelButtonExample.class),
		new ExampleData("Single Select", WSingleSelectExample.class),
		new ExampleData("Text Area", TextAreaExample.class),
		new ExampleData("Text Field", TextFieldExample.class),
		new ExampleData("WTextField", WTextFieldExample.class),
		new ExampleData("Toggle Button", WToggleButtonExample.class),
		new ExampleData("Tree", WTreeExample.class)
	};

	/**
	 * Examples showing (non-interactive) feedback and indicator controls.
	 */
	public static final ExampleData[] FEEDBACK_AND_INDICATORS = new ExampleData[]{
		new ExampleData("Abbreviations", WAbbrTextExample.class),
		new ExampleData("Access keys", AccessKeyExample.class),
		new ExampleData("Audio", WAudioExample.class),
		new ExampleData("Dynamic images", WImageCachedExample.class),
		new ExampleData("Figure", WFigureExample.class),
		new ExampleData("Headings", WHeadingExample.class),
		new ExampleData("Images", WImageExample.class),
		new ExampleData("Message Box", WMessageBoxExample.class),
		new ExampleData("Messages", WMessagesExample.class),
		new ExampleData("Progress bar", WProgressBarExample.class),
		new ExampleData("Styled Text options", WStyledTextOptionsExample.class),
		new ExampleData("Icons with WStyledText", WStyledTextFontAwesomeExample.class),
		new ExampleData("HTML class properties", HtmlClassPropertiesExample.class),
		new ExampleData("Text", WTextExample.class),
		new ExampleData("Timeout Warning", WTimeoutWarningExample.class),
		new ExampleData("Timeout Warning default options", WTimeoutWarningDefaultExample.class),
		new ExampleData("TimeoutWarning options", WTimeoutWarningOptionsExample.class),
		new ExampleData("Video", WVideoExample.class)
	};

	/**
	 * WDataTable examples.
	 */
	public static final ExampleData[] WDATATABLE_EXAMPLES = new ExampleData[]{
		new ExampleData("Basic WDataTable", WDataTableExample.class),
		new ExampleData("Table with row selection", SelectableDataTableExample.class),
		new ExampleData("Editable table", SimpleEditableDataTableExample.class),
		new ExampleData("Editable table with per-cell editability", SimpleRowEditingTableExample.class),
		new ExampleData("Table cell action", TableCellWithActionExample.class),
		new ExampleData("Data Table (bean)", DataTableBeanExample.class),
		new ExampleData("Tree table", TreeTableExample.class),
		new ExampleData("Tree table (hierarchy)", TreeTableHierarchyExample.class),
		new ExampleData("Table configuration options", DataTableOptionsExample.class),
		new ExampleData("Table with dynamic images", WDataTableContentExample.class)
	};

	/**
	 * WTable examples.
	 */
	public static final ExampleData[] WTABLE_EXAMPLES = new ExampleData[]{
		new ExampleData("Basic example", WTableExample.class),
		new ExampleData("Simple table (bean bound)", SimpleTableExample.class),
		new ExampleData("Simple editable", SimpleEditableTableExample.class),
		new ExampleData("Simple expandable", SimpleExpandableTableExample.class),
		new ExampleData("Simple expandable content", SimpleExpandableContentTableExample.class),
		new ExampleData("Simple pagination", SimplePaginationTableExample.class),
		new ExampleData("Simple pagination with row options", SimplePaginationWithRowOptionsTableExample.class),
		new ExampleData("Simple selectable", SimpleSelectableTableExample.class),
		new ExampleData("Simple sorting", SimpleSortingTableExample.class),
		new ExampleData("Table using a bean provider", TableBeanProviderExample.class),
		new ExampleData("Cell action", TableCellActionExample.class),
		new ExampleData("Expandable model", TableExpandableContentModelExample.class),
		new ExampleData("Scrollable model", TableScrollableModelExample.class),
		new ExampleData("Advanced cell editting with AJAX", TableRowEditingAjaxExample.class),
		new ExampleData("Options example", WTableOptionsExample.class),
		new ExampleData("Filtering example", FilterableTableExample.class),
		new ExampleData("WTable Cell with image content ", TableContentExample.class),
		new ExampleData("Simple Column Width", WTableColumnWidthExample.class),
		new ExampleData("Simple Column Alignment", WTableColumnAlignmentExample.class)
	};

	/**
	 * Other miscellaneous examples.
	 */
	public static final ExampleData[] MISC_EXAMPLES = new ExampleData[]{
		new ExampleData("Repeater", RepeaterExample.class),
		new ExampleData("Repeater (editable)", RepeaterExampleWithEditableRows.class),
		new ExampleData("Repeater (link)", RepeaterLinkExample.class),
		new ExampleData("App preference parameter example", AppPreferenceParameterExample.class),
		new ExampleData("Auto re-focus", AutoReFocusExample.class),
		new ExampleData("Auto re-focus (repeater)", AutoReFocusRepeaterExample.class),
		new ExampleData("ErrorGenerator", ErrorGenerator.class),
		new ExampleData("Forward example", ForwardExample.class),
		new ExampleData("Html injector", HtmlInjector.class),
		new ExampleData("InfoDump", InfoDump.class),
		new ExampleData("Internationalisation", I18nExample.class),
		new ExampleData("Kitchen sink", KitchenSink.class),
		new ExampleData("Pattern validation", PatternValidationExample.class),
		new ExampleData("Text duplicator", TextDuplicator.class),
		new ExampleData("Text duplicator (handleRequest)", TextDuplicatorHandleRequestImpl.class),
		new ExampleData("Text duplicator (Velocity)", TextDuplicatorVelocityImpl.class),
		new ExampleData("Text duplicator (Velocity2)", TextDuplicatorVelocity2.class),
		new ExampleData("WApplication", WApplicationExample.class),
		new ExampleData("WContent", WContentExample.class),
		new ExampleData("WHiddenComment", WHiddenCommentExample.class),
		new ExampleData("Input bean binding", InputBeanBindingExample.class),
		new ExampleData("White space example", WhiteSpaceExample.class)
	};

	/**
	 * Subordinate control examples.
	 */
	public static final ExampleData[] SUBORDINATE_EXAMPLES = new ExampleData[]{
		new ExampleData("Overview", SubordinateControlSuite.class),
		new ExampleData("All", SubordinateControlAllExamples.class),
		new ExampleData("Checkbox group", SubordinateControlSimpleCheckBoxSelectExample.class),
		new ExampleData("Crt dropdown", SubordinateControlCrtWDropdownExample.class),
		new ExampleData("Disable", SubordinateControlSimpleDisableExample.class),
		new ExampleData("Group", SubordinateControlGroupExample.class),
		new ExampleData("Mandatory", SubordinateControlMandatoryExample.class),
		new ExampleData("Multi-select", SubordinateControlSimpleWMultiSelectExample.class),
		new ExampleData("Simple", SubordinateControlExample.class),
		new ExampleData("Simple (2)", SubordinateControlSimpleExample.class),
		new ExampleData("Simple dropdown", SubordinateControlSimpleWDropdownExample.class),
		new ExampleData("Simple field", SubordinateControlSimpleWFieldExample.class),
		new ExampleData("Configuration options", SubordinateControlOptionsExample.class)
	};

	/**
	 * Layout examples.
	 */
	public static final ExampleData[] LAYOUT_EXAMPLES = new ExampleData[]{
		new ExampleData("Border layout", BorderLayoutExample.class),
		new ExampleData("Column layout", ColumnLayoutExample.class),
		new ExampleData("Columns with WRow / WCol", WRowExample.class),
		new ExampleData("Flow layout", FlowLayoutExample.class),
		new ExampleData("Grid layout", GridLayoutExample.class),
		new ExampleData("Grid layout configuration options", GridLayoutOptionsExample.class),
		new ExampleData("List layout", ListLayoutExample.class),
		new ExampleData("List layout options", ListLayoutOptionExample.class),
		new ExampleData("Margins", MarginExample.class),
		new ExampleData("Collapsible", WCollapsibleExample.class),
		new ExampleData("Collapsible group", WCollapsibleGroupExample.class),
		new ExampleData("Definition List", WDefinitionListExample.class),
		new ExampleData("Field Layout", WFieldLayoutExample.class),
		new ExampleData("Field nested", WFieldNestedExample.class),
		new ExampleData("FieldSet", WFieldSetExample.class),
		new ExampleData("List", WListExample.class),
		new ExampleData("List configuration options", WListOptionsExample.class),
		new ExampleData("Panel margins", WPanelMarginExample.class),
		new ExampleData("Panel types", WPanelExample.class),
		new ExampleData("Panel dynamic type", WPanelTypeExample.class),
		new ExampleData("Section", WSectionExample.class),
		new ExampleData("Template", WTemplateExample.class)
	};

	/**
	 * Examples of pop-up windows, dialogs, etc.
	 */
	public static final ExampleData[] POPUP_EXAMPLES = new ExampleData[]{
		new ExampleData("Dialog", WDialogExample.class),
		new ExampleData("Popup", WPopupExample.class),
		new ExampleData("Window", WWindowExample.class)
	};

	/**
	 * Menu examples.
	 */
	public static final ExampleData[] MENU_EXAMPLES = new ExampleData[]{
		new ExampleData("Bar Menu", MenuBarExample.class),
		new ExampleData("Column menu", ColumnMenuExample.class),
		new ExampleData("Flyout menu", MenuFlyoutExample.class),
		new ExampleData("Tree menu", TreeMenuExample.class),
		new ExampleData("Menu action messages", MenuItemActionMessagesExample.class),
		new ExampleData("Menu Select Mode", WMenuSelectModeExample.class)
	};

	/**
	 * Tabset examples.
	 */
	public static final ExampleData[] TABSET_EXAMPLES = new ExampleData[]{
		new ExampleData("Tabset", WTabSetExample.class),
		new ExampleData("Tabset and collapsibles", WTabAndCollapsibleExample.class),
		new ExampleData("Tabset action on change", WTabSetTriggerActionExample.class),
		new ExampleData("Nested tabsets", NestedTabSetExample.class),
		new ExampleData("Tabs", WTabExample.class)
	};

	/**
	 * Validation examples.
	 */
	public static final ExampleData[] VALIDATION_EXAMPLES = new ExampleData[]{
		new ExampleData("Validation examples", ValidationExamples.class),
		new ExampleData("Basic diagnostic", BasicDiagnosticComponentExample.class),
		new ExampleData("Basic fields", BasicFieldsValidationExample2.class),
		new ExampleData("Field validation", FieldValidation.class)
	};

	/**
	 * WLink and similar linky examples.
	 */
	public static final ExampleData[] LINK_EXAMPLES = new ExampleData[]{
		new ExampleData("External link examples", LinkExamples.class),
		new ExampleData("Link configuration options", LinkOptionsExample.class),
		new ExampleData("Link action", WLinkActionExample.class),
		new ExampleData("Skip links", WSkipLinksExample.class)
	};

	/**
	 * A short name / description of the example.
	 */
	private String exampleName;

	/**
	 * The example class.
	 */
	private Class<? extends WComponent> exampleClass;

	/**
	 * the name of the group for this example.
	 */
	private String exampleGroupName;

	/**
	 * Creates an ExampleData.
	 *
	 * @param exampleName the short name / description of the example
	 * @param exampleClass the example class
	 */
	public ExampleData(final String exampleName,
			final Class<? extends WComponent> exampleClass) {
		this.exampleName = exampleName;
		this.exampleClass = exampleClass;
	}

	/**
	 * Creates an ExampleData.
	 */
	public ExampleData() {
	}

	/**
	 * returns the group name of the example.
	 *
	 * @return the group name.
	 */
	public String getExampleGroupName() {
		return exampleGroupName;
	}

	/**
	 * setter for the group name of the example.
	 *
	 * @param exampleGroupName the group name.
	 */
	public void setExampleGroupName(final String exampleGroupName) {
		this.exampleGroupName = exampleGroupName;
	}

	/**
	 * @return Returns the short name / description of the example.
	 */
	public String getExampleName() {
		return exampleName;
	}

	/**
	 * setter for example name.
	 *
	 * @param exampleName the example name.
	 *
	 */
	public void setExampleName(final String exampleName) {
		this.exampleName = exampleName;
	}

	/**
	 * setter for example class.
	 *
	 * @param exampleClass the example class
	 */
	public void setExampleClass(final Class<? extends WComponent> exampleClass) {
		this.exampleClass = exampleClass;
	}

	/**
	 * @return Returns the example class.
	 */
	public Class<? extends WComponent> getExampleClass() {
		return exampleClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ExampleData)) {
			return false;
		}

		ExampleData that = (ExampleData) obj;

		return Util.equals(exampleName, that.exampleName)
				&& Util.equals(exampleClass, that.exampleClass)
				&& Util.equals(exampleGroupName, that.exampleGroupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (exampleName == null ? 0 : exampleName.hashCode())
				+ exampleClass.hashCode()
				+ (exampleGroupName == null ? 0 : exampleGroupName.hashCode());
	}
}
