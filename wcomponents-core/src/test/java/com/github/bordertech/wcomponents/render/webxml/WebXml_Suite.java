package com.github.bordertech.wcomponents.render.webxml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech} package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Layout tests
	BorderLayoutRenderer_Test.class,
	ColumnLayoutRenderer_Test.class,
	FlowLayoutRenderer_Test.class,
	GridLayoutRenderer_Test.class,
	ListLayoutRenderer_Test.class,
	// Component layout tests
	WAjaxControlRenderer_Test.class,
	WAbbrTextRenderer_Test.class,
	WApplicationRenderer_Test.class,
	WAudioRenderer_Test.class,
	WButtonRenderer_Test.class,
	WCancelButtonRenderer_Test.class,
	WCheckBoxRenderer_Test.class,
	WCheckBoxSelectRenderer_Test.class,
	WCollapsibleRenderer_Test.class,
	WCollapsibleToggleRenderer_Test.class,
	WColumnRenderer_Test.class,
	WConfirmationButtonRenderer_Test.class,
	WContentRenderer_Test.class,
	WComponentGroupRenderer_Test.class,
	WDataTableRenderer_Test.class,
	WDateFieldRenderer_Test.class,
	WDecoratedLabelRenderer_Test.class,
	WDefinitionListRenderer_Test.class,
	WDialogRenderer_Test.class,
	WDropdownRenderer_Test.class,
	WEmailFieldRenderer_Test.class,
	WFieldErrorIndicatorRenderer_Test.class,
	WFieldRenderer_Test.class,
	WFieldLayoutRenderer_Test.class,
	WFieldSetRenderer_Test.class,
	WFieldWarningIndicatorRenderer_Test.class,
	WFigureRenderer_Test.class,
	WFileWidgetRenderer_Test.class,
	WHiddenCommentRenderer_Test.class,
	WHorizontalRuleRenderer_Test.class,
	WHeadingRenderer_Test.class,
	WImageRenderer_Test.class,
	WInternalLinkRenderer_Test.class,
	WLabelRenderer_Test.class,
	WListRenderer_Test.class,
	WLinkRenderer_Test.class,
	WMenuItemGroupRenderer_Test.class,
	WMenuItemRenderer_Test.class,
	WMenuRenderer_Test.class,
	WMessageBoxRenderer_Test.class,
	WMultiDropdownRenderer_Test.class,
	WMultiFileWidgetRenderer_Test.class,
	WMultiSelectRenderer_Test.class,
	WMultiSelectPairRenderer_Test.class,
	WMultiTextFieldRenderer_Test.class,
	WNumberFieldRenderer_Test.class,
	WPanelRenderer_Test.class,
	WPartialDateFieldRenderer_Test.class,
	WPasswordFieldRenderer_Test.class,
	WPhoneNumberFieldRenderer_Test.class,
	WPopupRenderer_Test.class,
	WPrintButtonRenderer_Test.class,
	WProgressBarRenderer_Test.class,
	WRadioButtonRenderer_Test.class,
	WRadioButtonSelectRenderer_Test.class,
	WRepeaterRenderer_Test.class,
	WRowRenderer_Test.class,
	WSectionRenderer_Test.class,
	WSelectToggleRenderer_Test.class,
	WShufflerRenderer_Test.class,
	WSingleSelectRenderer_Test.class,
	WSkipLinksRenderer_Test.class,
	WStyledTextRenderer_Test.class,
	WSubMenuRenderer_Test.class,
	WSubordinateControlRenderer_Test.class,
	WSuggestionsRenderer_Test.class,
	WTabGroupRenderer_Test.class,
	WTableRenderer_Test.class,
	WTabRenderer_Test.class,
	WTabSetRenderer_Test.class,
	WTemplateRenderer_Test.class,
	WTextAreaRenderer_Test.class,
	WTextFieldRenderer_Test.class,
	WTextRenderer_Test.class,
	WValidationErrorsRenderer_Test.class,
	WVideoRenderer_Test.class,
	// Performance tests
	WebXmlRenderingPerformance_Test.class
})
public class WebXml_Suite {
}
