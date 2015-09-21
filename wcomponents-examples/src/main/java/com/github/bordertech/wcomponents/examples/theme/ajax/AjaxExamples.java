package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTabSet;

/**
 * An example of an {@link WTabSet} using AJAX. Each tab is AJAX enabled and the tab content is loaded in the AJAX
 * response.
 *
 * @author Christina Harris
 * @since 18/06/2009
 */
public class AjaxExamples extends WContainer {

	/**
	 * Creates the AjaxExamples example.
	 */
	public AjaxExamples() {
		AjaxWDropdownExample dropdown = new AjaxWDropdownExample();
		AjaxWCheckboxExample checkbox = new AjaxWCheckboxExample();
		AjaxWCollapsibleExample collapsible = new AjaxWCollapsibleExample();
		AjaxWButtonExample button = new AjaxWButtonExample();
		AjaxWPaginationExample paging = new AjaxWPaginationExample();
		AjaxWRadioButtonSelectExample group = new AjaxWRadioButtonSelectExample();
		AjaxWPanelExample panel = new AjaxWPanelExample();

		WTabSet examples = new WTabSet();
		add(examples);

		examples.addTab(dropdown, "Ajax WDropdown Example", WTabSet.TabMode.LAZY);
		examples.addTab(checkbox, "Ajax WCheckbox Example", WTabSet.TabMode.LAZY);
		examples.addTab(collapsible, "Ajax WCollapsible Example", WTabSet.TabMode.LAZY);
		examples.addTab(button, "Ajax WButton Example", WTabSet.TabMode.LAZY);
		examples.addTab(paging, "Ajax WTable Pagination Example", WTabSet.TabMode.LAZY);
		examples.addTab(group, "Ajax WRadioButtonSelect Example", WTabSet.TabMode.LAZY);
		examples.addTab(panel, "Ajax WPanel Example", WTabSet.TabMode.LAZY);
	}
}
