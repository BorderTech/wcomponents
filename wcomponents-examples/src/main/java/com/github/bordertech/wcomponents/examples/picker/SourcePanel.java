package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.Request;
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
	 * URL to the syntax highlighter core.
	 */
	private final WText script1Url = new WText();
	/**
	 * URL to the syntax highlighter language pack.
	 */
	private final WText script2Url = new WText();
	/**
	 * URL to the syntax highlighter CSS.
	 */
	private final WText cssUrl = new WText();

	/**
	 * Creates a SourcePanel.
	 */
	public SourcePanel() {
		source.setEncodeText(false);
		WTemplate template = new WTemplate("/com/github/bordertech/wcomponents/examples/picker/sourceView.vm", TemplateRendererFactory.TemplateEngine.VELOCITY);
		add(template);
		template.addTaggedComponent("src", source);
		template.addTaggedComponent("script1", script1Url);
		template.addTaggedComponent("script2", script2Url);
		template.addTaggedComponent("css", cssUrl);

	}

	@Override
	protected void preparePaintComponent(Request request) {
		if (!isInitialised()) {
			String fileName = "/com/github/bordertech/wcomponents/examples/sunlight.dark.css";
			InternalResource resource = new InternalResource(fileName, fileName);
			cssUrl.setText(resource.getTargetUrl());
			fileName = "/com/github/bordertech/wcomponents/examples/sunlight-min.js";
			resource = new InternalResource(fileName, fileName);
			script1Url.setText(resource.getTargetUrl());
			fileName = "/com/github/bordertech/wcomponents/examples/sunlight.java-min.js";
			resource = new InternalResource(fileName, fileName);
			script2Url.setText(resource.getTargetUrl());
			setInitialised(true);
		}
		super.preparePaintComponent(request);
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
