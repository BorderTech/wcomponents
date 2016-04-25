package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;

/**
 * <p>
 * This component displays the java source code for the WComponent examples.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 */
public class SourcePanel extends WPanel {

	/**
	 * The source code.
	 */
	private final WText source = new WText();

	/**
	 * Creates a SourcePanel.
	 */
	public SourcePanel() {
		source.setEncodeText(false);
		WTemplate template = new WTemplate("/com/github/bordertech/wcomponents/examples/picker/sourceView.vm", TemplateRendererFactory.TemplateEngine.VELOCITY);
		add(template);
		template.addTaggedComponent("src", source);
	}

	/**
	 * Sets the source code to be displayed in the panel.
	 *
	 * @param sourceText the source code to display.
	 */
	public void setSource(final String sourceText) {
		String formattedSource;

		if (sourceText == null) {
			formattedSource = "";
		} else {
			formattedSource = WebUtilities.encode(sourceText); // XML escape content
		}

		source.setText(formattedSource);
	}
}
