package com.github.bordertech.wcomponents.examples.common;

import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;

/**
 * Creates a simple template to include some JavaScript to enable client side validation.
 *
 * @author exbtma
 */
public class ClientValidationTemplate extends WTemplate {

	/**
	 * Create a Client ValidationTemplate.
	 */
	public ClientValidationTemplate() {
		super("com/github/bordertech/wcomponents/examples/clientValidation.txt", TemplateRendererFactory.TemplateEngine.PLAINTEXT);
		setVisible(false);
	}
}
