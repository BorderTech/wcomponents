package com.github.dibp.wcomponents.examples.picker;

import java.io.Serializable;

import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.util.Util;

import com.github.dibp.wcomponents.examples.AppPreferenceParameterExample;
import com.github.dibp.wcomponents.examples.AutoReFocusExample;
import com.github.dibp.wcomponents.examples.AutoReFocusRepeaterExample;
import com.github.dibp.wcomponents.examples.ButtonOptionsExample;
import com.github.dibp.wcomponents.examples.EntryFieldExample;
import com.github.dibp.wcomponents.examples.ErrorGenerator;
import com.github.dibp.wcomponents.examples.ForwardExample;
import com.github.dibp.wcomponents.examples.HtmlInjector;
import com.github.dibp.wcomponents.examples.I18nExample;
import com.github.dibp.wcomponents.examples.InfoDump;
import com.github.dibp.wcomponents.examples.InputBeanBindingExample;
import com.github.dibp.wcomponents.examples.KitchenSink;
import com.github.dibp.wcomponents.examples.LinkOptionsExample;
import com.github.dibp.wcomponents.examples.LoadAjaxControlsExample;
import com.github.dibp.wcomponents.examples.MarginExample;
import com.github.dibp.wcomponents.examples.MultiPollingExample;
import com.github.dibp.wcomponents.examples.PatternValidationExample;
import com.github.dibp.wcomponents.examples.SimpleCancelButtonExample;
import com.github.dibp.wcomponents.examples.SimpleFileUpload;
import com.github.dibp.wcomponents.examples.TextAreaExample;
import com.github.dibp.wcomponents.examples.TextDuplicator;
import com.github.dibp.wcomponents.examples.TextDuplicator_HandleRequestImpl;
import com.github.dibp.wcomponents.examples.TextDuplicator_Velocity2;
import com.github.dibp.wcomponents.examples.TextDuplicator_VelocityImpl;
import com.github.dibp.wcomponents.examples.TextFieldExample;
import com.github.dibp.wcomponents.examples.WAbbrTextExample;
import com.github.dibp.wcomponents.examples.WApplicationExample;
import com.github.dibp.wcomponents.examples.WAudioExample;
import com.github.dibp.wcomponents.examples.WButtonActionExample;
import com.github.dibp.wcomponents.examples.WButtonExample;
import com.github.dibp.wcomponents.examples.WCheckBoxTriggerActionExample;
import com.github.dibp.wcomponents.examples.WContentExample;
import com.github.dibp.wcomponents.examples.WDataListServletExample;
import com.github.dibp.wcomponents.examples.WDefinitionListExample;
import com.github.dibp.wcomponents.examples.WDialogExample;
import com.github.dibp.wcomponents.examples.WDropdownExample;
import com.github.dibp.wcomponents.examples.WDropdownOptionsExample;
import com.github.dibp.wcomponents.examples.WDropdownSpaceHandlingExample;
import com.github.dibp.wcomponents.examples.WDropdownSpecialCharHandlingExample;
import com.github.dibp.wcomponents.examples.WDropdownSubmitOnChangeExample;
import com.github.dibp.wcomponents.examples.WDropdownTriggerActionExample;
import com.github.dibp.wcomponents.examples.WFigureExample;
import com.github.dibp.wcomponents.examples.WImageCachedExample;
import com.github.dibp.wcomponents.examples.WImageExample;
import com.github.dibp.wcomponents.examples.WLinkActionExample;
import com.github.dibp.wcomponents.examples.WMultiFileWidgetAjaxExample;
import com.github.dibp.wcomponents.examples.WMultiSelectExample;
import com.github.dibp.wcomponents.examples.WPanelExample;
import com.github.dibp.wcomponents.examples.WPopupExample;
import com.github.dibp.wcomponents.examples.WRadioButtonInRepeater;
import com.github.dibp.wcomponents.examples.WRadioButtonTriggerActionExample;
import com.github.dibp.wcomponents.examples.WSectionExample;
import com.github.dibp.wcomponents.examples.WSessionExample;
import com.github.dibp.wcomponents.examples.WShufflerExample;
import com.github.dibp.wcomponents.examples.WSingleSelectExample;
import com.github.dibp.wcomponents.examples.WSkipLinksExample;
import com.github.dibp.wcomponents.examples.WStyledTextOptionsExample;
import com.github.dibp.wcomponents.examples.WSuggestionsExample;
import com.github.dibp.wcomponents.examples.WTextExample;
import com.github.dibp.wcomponents.examples.WTimeoutWarningDefaultExample;
import com.github.dibp.wcomponents.examples.WTimeoutWarningExample;
import com.github.dibp.wcomponents.examples.WTimeoutWarningOptionsExample;
import com.github.dibp.wcomponents.examples.WVideoExample;
import com.github.dibp.wcomponents.examples.WWindowExample;
import com.github.dibp.wcomponents.examples.WhiteSpaceExample;
import com.github.dibp.wcomponents.examples.datatable.DataTableBeanExample;
import com.github.dibp.wcomponents.examples.datatable.DataTableOptionsExample;
import com.github.dibp.wcomponents.examples.datatable.SelectableDataTableExample;
import com.github.dibp.wcomponents.examples.datatable.SimpleEditableDataTableExample;
import com.github.dibp.wcomponents.examples.datatable.SimpleRowEditingTableExample;
import com.github.dibp.wcomponents.examples.datatable.TableCellWithActionExample;
import com.github.dibp.wcomponents.examples.datatable.TreeTableExample;
import com.github.dibp.wcomponents.examples.datatable.TreeTableHierarchyExample;
import com.github.dibp.wcomponents.examples.datatable.WDataTableContentExample;
import com.github.dibp.wcomponents.examples.datatable.WDataTableExample;
import com.github.dibp.wcomponents.examples.layout.BorderLayoutExample;
import com.github.dibp.wcomponents.examples.layout.ColumnLayoutExample;
import com.github.dibp.wcomponents.examples.layout.FlowLayoutExample;
import com.github.dibp.wcomponents.examples.layout.GridLayoutExample;
import com.github.dibp.wcomponents.examples.layout.GridLayoutOptionsExample;
import com.github.dibp.wcomponents.examples.layout.ListLayoutExample;
import com.github.dibp.wcomponents.examples.menu.ColumnMenuExample;
import com.github.dibp.wcomponents.examples.menu.MenuBarExample;
import com.github.dibp.wcomponents.examples.menu.MenuFlyoutExample;
import com.github.dibp.wcomponents.examples.menu.MenuItemActionMessagesExample;
import com.github.dibp.wcomponents.examples.menu.TreeMenuExample;
import com.github.dibp.wcomponents.examples.repeater.RepeaterExample;
import com.github.dibp.wcomponents.examples.repeater.RepeaterExampleWithEditableRows;
import com.github.dibp.wcomponents.examples.repeater.link.RepeaterLinkExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlAllExamples;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlCrtWDropdownExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlGroupExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlMandatoryExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlOptionsExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSimpleCheckBoxSelectExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSimpleDisableExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSimpleExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSimpleWDropdownExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSimpleWFieldExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSimpleWMultiSelectExample;
import com.github.dibp.wcomponents.examples.subordinate.SubordinateControlSuite;
import com.github.dibp.wcomponents.examples.table.FilterableTableExample;
import com.github.dibp.wcomponents.examples.table.SimpleEditableTableExample;
import com.github.dibp.wcomponents.examples.table.SimpleExpandableContentTableExample;
import com.github.dibp.wcomponents.examples.table.SimpleExpandableTableExample;
import com.github.dibp.wcomponents.examples.table.SimplePaginationTableExample;
import com.github.dibp.wcomponents.examples.table.SimplePaginationWithRowOptionsTableExample;
import com.github.dibp.wcomponents.examples.table.SimpleSelectableTableExample;
import com.github.dibp.wcomponents.examples.table.SimpleSortingTableExample;
import com.github.dibp.wcomponents.examples.table.SimpleTableExample;
import com.github.dibp.wcomponents.examples.table.TableBeanProviderExample;
import com.github.dibp.wcomponents.examples.table.TableCellActionExample;
import com.github.dibp.wcomponents.examples.table.TableContentExample;
import com.github.dibp.wcomponents.examples.table.TableExpandableContentModelExample;
import com.github.dibp.wcomponents.examples.table.TableRowEditingAjaxExample;
import com.github.dibp.wcomponents.examples.table.TableScrollableModelExample;
import com.github.dibp.wcomponents.examples.table.WTableExample;
import com.github.dibp.wcomponents.examples.table.WTableOptionsExample;
import com.github.dibp.wcomponents.examples.theme.AccessKeyExample;
import com.github.dibp.wcomponents.examples.theme.NestedTabSetExample;
import com.github.dibp.wcomponents.examples.theme.WButtonDefaultSubmitExample;
import com.github.dibp.wcomponents.examples.theme.WCancelButtonExample;
import com.github.dibp.wcomponents.examples.theme.WCheckBoxExample;
import com.github.dibp.wcomponents.examples.theme.WCheckBoxSelectExample;
import com.github.dibp.wcomponents.examples.theme.WCollapsibleExample;
import com.github.dibp.wcomponents.examples.theme.WCollapsibleGroupExample;
import com.github.dibp.wcomponents.examples.theme.WColumnLayoutExample;
import com.github.dibp.wcomponents.examples.theme.WConfirmationButtonExample;
import com.github.dibp.wcomponents.examples.theme.WDateFieldExample;
import com.github.dibp.wcomponents.examples.theme.WEmailFieldExample;
import com.github.dibp.wcomponents.examples.theme.WFieldLayoutExample;
import com.github.dibp.wcomponents.examples.theme.WFieldNestedExample;
import com.github.dibp.wcomponents.examples.theme.WFieldSetExample;
import com.github.dibp.wcomponents.examples.theme.WHeadingExample;
import com.github.dibp.wcomponents.examples.theme.WHiddenCommentExample;
import com.github.dibp.wcomponents.examples.theme.WLabelExample;
import com.github.dibp.wcomponents.examples.theme.WListExample;
import com.github.dibp.wcomponents.examples.theme.WListOptionsExample;
import com.github.dibp.wcomponents.examples.theme.WMenuSelectModeExample;
import com.github.dibp.wcomponents.examples.theme.WMessageBoxExample;
import com.github.dibp.wcomponents.examples.theme.WMessagesExample;
import com.github.dibp.wcomponents.examples.theme.WMultiDropdownExample;
import com.github.dibp.wcomponents.examples.theme.WMultiFileWidgetExample;
import com.github.dibp.wcomponents.examples.theme.WMultiSelectPairExample;
import com.github.dibp.wcomponents.examples.theme.WMultiTextFieldExample;
import com.github.dibp.wcomponents.examples.theme.WPanelMarginExample;
import com.github.dibp.wcomponents.examples.theme.WPanelTypeExample;
import com.github.dibp.wcomponents.examples.theme.WPartialDateFieldExample;
import com.github.dibp.wcomponents.examples.theme.WPhoneNumberFieldExample;
import com.github.dibp.wcomponents.examples.theme.WProgressBarExample;
import com.github.dibp.wcomponents.examples.theme.WRadioButtonExample;
import com.github.dibp.wcomponents.examples.theme.WRadioButtonSelectExample;
import com.github.dibp.wcomponents.examples.theme.WRowExample;
import com.github.dibp.wcomponents.examples.theme.WSelectToggleExample;
import com.github.dibp.wcomponents.examples.theme.WTabAndCollapsibleExample;
import com.github.dibp.wcomponents.examples.theme.WTabExample;
import com.github.dibp.wcomponents.examples.theme.WTabSetExample;
import com.github.dibp.wcomponents.examples.theme.WTabSetTriggerActionExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxPollingWButtonExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxReplaceControllerExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWButtonExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWCheckboxExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWCollapsibleExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWDropdownExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWPaginationExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWPanelExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWRadioButtonSelectExample;
import com.github.dibp.wcomponents.examples.theme.ajax.AjaxWRepeaterExample;
import com.github.dibp.wcomponents.examples.theme.ajax.WCollapsibleOptionsExample;
import com.github.dibp.wcomponents.examples.validation.ValidationExamples;
import com.github.dibp.wcomponents.examples.validation.basic.BasicDiagnosticComponentExample;
import com.github.dibp.wcomponents.examples.validation.basic.BasicFieldsValidationExample2;
import com.github.dibp.wcomponents.examples.validation.fields.FieldValidation;
import com.github.dibp.wcomponents.othersys.examples.LinkExamples;

/**
 * ExampleData contains the information necessary to describe an example.
 * It also provides some groupings for the standard WComponent examples.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public final class ExampleData implements Serializable
{
    /**
     * AJAX examples.
     */
    public static final ExampleData[] AJAX_EXAMPLES = new ExampleData[]
    {
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
        new ExampleData("Multi pollers", MultiPollingExample.class),
        new ExampleData("Suggestions", WSuggestionsExample.class)
    };

    /**
     * Examples showing (interactive) form controls.
     */
    public static final ExampleData[] FORM_CONTROLS = new ExampleData[]
    {
        new ExampleData("Overview", EntryFieldExample.class),
        new ExampleData("Button", WButtonExample.class),
        new ExampleData("Button action example", WButtonActionExample.class),
        new ExampleData("Button configuration options", ButtonOptionsExample.class),
        new ExampleData("WButton default submit", WButtonDefaultSubmitExample.class),
        new ExampleData("Cancel button", WCancelButtonExample.class),
        new ExampleData("WCheckBox", WCheckBoxExample.class),
        new ExampleData("Checkbox (action on change)", WCheckBoxTriggerActionExample.class),
        new ExampleData("WCheckBoxSelect", WCheckBoxSelectExample.class),
        new ExampleData("Confirmation button", WConfirmationButtonExample.class),
        new ExampleData("Date field", WDateFieldExample.class),
        new ExampleData("Dropdown", WDropdownExample.class),
        new ExampleData("Dropdown (action on change)", WDropdownTriggerActionExample.class),
        new ExampleData("Dropdown (submit on change)", WDropdownSubmitOnChangeExample.class),
        new ExampleData("Dropdown (space handling)", WDropdownSpaceHandlingExample.class),
        new ExampleData("Dropdown (special char handling)", WDropdownSpecialCharHandlingExample.class),
        new ExampleData("Dropdown configuration options", WDropdownOptionsExample.class),
        new ExampleData("Email field", WEmailFieldExample.class),
        new ExampleData("Simple File upload", SimpleFileUpload.class),
        new ExampleData("Labels", WLabelExample.class),
        new ExampleData("Multi-dropdown", WMultiDropdownExample.class),
        new ExampleData("Multi Select", WMultiSelectExample.class),
        new ExampleData("Multi-file upload", WMultiFileWidgetExample.class),
        new ExampleData("Multi-file ajax", WMultiFileWidgetAjaxExample.class),
        new ExampleData("Multi-select pair", WMultiSelectPairExample.class),
        new ExampleData("Multi-text field", WMultiTextFieldExample.class),
        new ExampleData("Partial date field", WPartialDateFieldExample.class),
        new ExampleData("Phone Number Field", WPhoneNumberFieldExample.class),
        new ExampleData("WRadioButton", WRadioButtonExample.class),
        new ExampleData("Radiobutton (action on change)", WRadioButtonTriggerActionExample.class),
        new ExampleData("Radiobutton in a repeater", WRadioButtonInRepeater.class),
        new ExampleData("Radiobutton select", WRadioButtonSelectExample.class),
        new ExampleData("Single Select", WSingleSelectExample.class),
        new ExampleData("Text Area", TextAreaExample.class),
        new ExampleData("Text Field", TextFieldExample.class),
        new ExampleData("Select toggle", WSelectToggleExample.class),
        new ExampleData("Shuffler", WShufflerExample.class),
        new ExampleData("Simple Cancel Button", SimpleCancelButtonExample.class)
    };

    /**
     * Examples showing (non-interactive) feedback and indicator controls.
     */
    public static final ExampleData[] FEEDBACK_AND_INDICATORS = new ExampleData[]
    {
        new ExampleData("Abbreviations", WAbbrTextExample.class),
        new ExampleData("Access keys", AccessKeyExample.class),
        new ExampleData("Audio", WAudioExample.class),
        new ExampleData("Headings", WHeadingExample.class),
        new ExampleData("Images", WImageExample.class),
        new ExampleData("Message Box", WMessageBoxExample.class),
        new ExampleData("Messages", WMessagesExample.class),
        new ExampleData("Progress bar", WProgressBarExample.class),
        new ExampleData("Text", WTextExample.class),
        new ExampleData("Video", WVideoExample.class),
        
        new ExampleData("Session timeout", WSessionExample.class),
        new ExampleData("WTimeoutWarning", WTimeoutWarningExample.class),
        new ExampleData("WTimeoutWarning default options", WTimeoutWarningDefaultExample.class),
        new ExampleData("WTimeoutWarning options", WTimeoutWarningOptionsExample.class)
    };

    /**
     * WDataTable examples.
     */
    public static final ExampleData[] WDATATABLE_EXAMPLES = new ExampleData[]
    {
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
    public static final ExampleData[] WTABLE_EXAMPLES = new ExampleData[]
    {
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
        new ExampleData("WTable Cell with image content ", TableContentExample.class)
    };

    /**
     * Other miscellaneous examples.
     */
    public static final ExampleData[] MISC_EXAMPLES = new ExampleData[]
    {
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
        new ExampleData("Load AJAX controls via AJAX", LoadAjaxControlsExample.class),
        new ExampleData("Ajax controllers replaced via ajax", AjaxReplaceControllerExample.class),
        new ExampleData("Internationalisation", I18nExample.class),
        new ExampleData("Kitchen sink", KitchenSink.class),
        new ExampleData("Pattern validation", PatternValidationExample.class),
        new ExampleData("Text duplicator", TextDuplicator.class),
        new ExampleData("Text duplicator (handleRequest)", TextDuplicator_HandleRequestImpl.class),
        new ExampleData("Text duplicator (Velocity)", TextDuplicator_VelocityImpl.class),
        new ExampleData("Text duplicator (Velocity2)", TextDuplicator_Velocity2.class),
        new ExampleData("WApplication", WApplicationExample.class),
        new ExampleData("WContent", WContentExample.class),
        new ExampleData("WDataListServer", WDataListServletExample.class),

        new ExampleData("WHiddenComment", WHiddenCommentExample.class),
        new ExampleData("Input bean binding", InputBeanBindingExample.class),
        new ExampleData("White space example", WhiteSpaceExample.class),
        new ExampleData("WStyledText options", WStyledTextOptionsExample.class),
        new ExampleData("Dynamic images", WImageCachedExample.class)
    };

    /**
     * Subordinate control examples.
     */
    public static final ExampleData[] SUBORDINATE_EXAMPLES = new ExampleData[]
    {
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
    public static final ExampleData[] LAYOUT_EXAMPLES = new ExampleData[]
    {
        new ExampleData("Border layout", BorderLayoutExample.class),
        new ExampleData("Column layout", ColumnLayoutExample.class),
        new ExampleData("Columns with WRow / WCol", WRowExample.class),
        new ExampleData("Flow layout", FlowLayoutExample.class),
        new ExampleData("Grid layout", GridLayoutExample.class),
        new ExampleData("Grid layout configuration options", GridLayoutOptionsExample.class),
        new ExampleData("List layout", ListLayoutExample.class),
        new ExampleData("Margins", MarginExample.class),
        new ExampleData("WCollapsible", WCollapsibleExample.class),
        new ExampleData("WCollapsible group", WCollapsibleGroupExample.class),
        new ExampleData("WColumnLayout", WColumnLayoutExample.class),
        new ExampleData("WDefinitionList", WDefinitionListExample.class),
        new ExampleData("WFieldLayout", WFieldLayoutExample.class),
        new ExampleData("WField nested", WFieldNestedExample.class),
        new ExampleData("WFieldSet", WFieldSetExample.class),
        new ExampleData("WList", WListExample.class),
        new ExampleData("WList configuration options", WListOptionsExample.class),
        new ExampleData("WPanel margins", WPanelMarginExample.class),
        new ExampleData("WPanel types", WPanelExample.class),
        new ExampleData("Panel dynamic type", WPanelTypeExample.class),
        new ExampleData("WSection", WSectionExample.class),
        new ExampleData("WFigure", WFigureExample.class)
    };

    /**
     * Examples of pop-up windows, dialogs, etc.
     */
    public static final ExampleData[] POPUP_EXAMPLES = new ExampleData[]
    {
        new ExampleData("Dialog", WDialogExample.class),
        new ExampleData("Popup", WPopupExample.class),
        new ExampleData("Window", WWindowExample.class)
    };

    /**
     * Menu examples.
     */
    public static final ExampleData[] MENU_EXAMPLES = new ExampleData[]
    {
        new ExampleData("Menu bar", MenuBarExample.class),
        new ExampleData("Tree menu", TreeMenuExample.class),
        new ExampleData("Column menu", ColumnMenuExample.class),
        new ExampleData("Flyout menu", MenuFlyoutExample.class),
        new ExampleData("Menu action messages", MenuItemActionMessagesExample.class),
        new ExampleData("Menu Select Mode", WMenuSelectModeExample.class)
    };

    /**
     * Tabset examples.
     */
    public static final ExampleData[] TABSET_EXAMPLES = new ExampleData[]
    {
        new ExampleData("Tabset", WTabSetExample.class),
        new ExampleData("Tabset and collapsibles", WTabAndCollapsibleExample.class),
        new ExampleData("Tabset action on change", WTabSetTriggerActionExample.class),
        new ExampleData("Nested tabsets", NestedTabSetExample.class),
        new ExampleData("Tabs", WTabExample.class)
    };

    /**
     * Validation examples.
     */
    public static final ExampleData[] VALIDATION_EXAMPLES = new ExampleData[]
    {
        new ExampleData("Validation examples", ValidationExamples.class),
        new ExampleData("Basic diagnostic", BasicDiagnosticComponentExample.class),
        new ExampleData("Basic fields", BasicFieldsValidationExample2.class),
        new ExampleData("Field validation", FieldValidation.class)
    };

    /**
     * WLink and similar linky examples
     */
    public static final ExampleData[] LINK_EXAMPLES = new ExampleData[]
    {
         new ExampleData("External link examples", LinkExamples.class),
         new ExampleData("Link configuration options", LinkOptionsExample.class),
         new ExampleData("Link action", WLinkActionExample.class),
         new ExampleData("Skip links", WSkipLinksExample.class)
    };

    /** A short name / description of the example. */
    private String exampleName;

    /** The example class. */
    private Class<? extends WComponent> exampleClass;

    /** the name of the group for this example. */
    private String exampleGroupName;


    /**
     * Creates an ExampleData.
     *
     * @param exampleName the short name / description of the example
     * @param exampleClass the example class
     */
    public ExampleData(final String exampleName,
                       final Class<? extends WComponent> exampleClass)
    {
        this.exampleName = exampleName;
        this.exampleClass = exampleClass;
    }


    /**
     * Creates an ExampleData.
     */
    public ExampleData()
    {
    }


    /**
     * returns the group name of the example.
     * @return the group name.
     */
    public String getExampleGroupName()
    {
        return exampleGroupName;
    }

    /**
     * setter for the group name of the example.
     * @param exampleGroupName the group name.
     */
    public void setExampleGroupName(final String exampleGroupName)
    {
        this.exampleGroupName = exampleGroupName;
    }


    /**
     * @return Returns the short name / description of the example.
     */
    public String getExampleName()
    {
        return exampleName;
    }

    /**
     * setter for example name.
     * @param exampleName the example name.
     * */
    public void setExampleName(final String exampleName)
    {
        this.exampleName = exampleName;
    }

    /**
     * setter for example class.
     * @param exampleClass the example class
     */
    public void setExampleClass(final Class<? extends WComponent> exampleClass)
    {
        this.exampleClass = exampleClass;
    }

    /**
     * @return Returns the example class.
     */
    public Class<? extends WComponent> getExampleClass()
    {
        return exampleClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof ExampleData))
        {
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
    public int hashCode()
    {
        return (exampleName == null ? 0 : exampleName.hashCode())
                + exampleClass.hashCode()
                + (exampleGroupName == null ? 0 : exampleGroupName.hashCode());
    }
}
