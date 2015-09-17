package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.examples.WCheckBoxTriggerActionExample;

/**
 * In this example the {@link WCheckBoxTriggerActionExample} is made AJAX aware. The checkboxes are made ajax triggers,
 * the target of each trigger is the information text box. When any of the checkboxes are ticked/unticked an AJAX
 * request is triggered and the response will re-paint the infomation box. No longer is the whole page refreshed.
 *
 * @author Christina Harris
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0 This is now simply a wrapper as {@link WCheckBoxTriggerActionExample} is a better ajax example.
 */
public class AjaxWCheckboxExample extends WContainer {

	/**
	 * Creates a AjaxWCheckboxExample.
	 */
	public AjaxWCheckboxExample() {
		add(new WCheckBoxTriggerActionExample());
	}

}
