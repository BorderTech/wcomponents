package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLink;
import com.github.bordertech.wcomponents.WLink.ImagePosition;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * LinkOptionsExample contains a series of controls for displaying and manipulating an example link.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class LinkOptionsExample extends WPanel {

	/**
	 * link container for holding example link.
	 */
	private final WContainer linkContainer = new WContainer();

	/**
	 * text field for the link label.
	 */
	private final WTextField tfLinkLabel = new WTextField();

	/**
	 * text field for the access key.
	 */
	private final WTextField tfAccesskey = new WTextField();

	/**
	 * check box for the disable/enable of the example link.
	 */
	private final WCheckBox cbDisabled = new WCheckBox();

	/**
	 * check box for render as button.
	 */
	private final WCheckBox cbRenderAsButton = new WCheckBox();

	/**
	 * check box to set link as image.
	 */
	private final WCheckBox cbSetImage = new WCheckBox();

	/**
	 * drop down for image position.
	 */
	private final WDropdown ddImagePosition = new WDropdown();

	/**
	 * check box for open in new window.
	 */
	private final WCheckBox cbOpenNew = new WCheckBox();

	/**
	 * initial link label text.
	 */
	private static final String INITIAL_LINK_TEXT = "Link Text";

	/**
	 * link URL.
	 */
	private static final String URL = "http://www.example.com";

	private final WTextField tfUrlField = new WTextField();

	/**
	 * Link example constructor, primarily used for setting up the link config field set.
	 */
	public LinkOptionsExample() {
		tfLinkLabel.setText(INITIAL_LINK_TEXT);
		tfAccesskey.setMaxLength(1);
		tfAccesskey.setColumns(1);

		tfUrlField.setText(URL);
		tfUrlField.setColumns(50);

		List<ImagePosition> positions = new ArrayList<>(Arrays.asList(WLink.ImagePosition.values()));
		positions.add(0, null);
		ddImagePosition.setOptions(positions);

		WValidationErrors errors = new WValidationErrors();
		add(errors);
		WFieldSet fsButtonControls = getButtonControls(errors);
		add(fsButtonControls);
		add(new WHorizontalRule());
		// we use a container here so we can reset it for
		// the render as link option.
		add(linkContainer);
		add(new WHorizontalRule());

	}

	/**
	 * build the button controls field set.
	 *
	 * @param errors the error pane from the page.
	 * @return a field set for the controls.
	 */
	private WFieldSet getButtonControls(final WValidationErrors errors) {
		// Options Layout
		WFieldSet fieldSet = new WFieldSet("Link configuration");
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(30);

		layout.addField("Link text", tfLinkLabel);
		layout.addField("Link address", tfUrlField);
		layout.addField("Link AccessKey", tfAccesskey).getLabel().setHint(
				"A single upper case letter or digit.");
		layout.addField("Render as button", cbRenderAsButton);
		layout.addField("Disabled", cbDisabled);
		layout.addField("Open in a new window", cbOpenNew);
		layout.addField("setImage ('/image/attachment.png')", cbSetImage);
		layout.addField("Image Position", ddImagePosition);
		layout.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 6, 0));

		// Apply Button
		WButton apply = new WButton("Apply");
		apply.setAction(new ValidatingAction(errors, fieldSet) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				applySettings();
			}
		});

		fieldSet.add(layout);
		fieldSet.add(apply);
		return fieldSet;
	}

	/**
	 * @param url the url to validate
	 * @return true if valid
	 */
	private boolean isValidUrl(final String url) {
		UrlValidator validator = new UrlValidator();
		return validator.isValid(url);
	}

	/**
	 * this is were the majority of the work is done for building the link. Note that it is in a container that is
	 * reset, effectively creating a new link. this is only done to enable to dynamically change the link to a button
	 * and back.
	 */
	private void applySettings() {
		linkContainer.reset();
		WLink exampleLink = new WLink();
		exampleLink.setText(tfLinkLabel.getText());
		final String url = tfUrlField.getValue();

		if ("".equals(url) || !isValidUrl(url)) {
			tfUrlField.setText(URL);
			exampleLink.setUrl(URL);
		} else {
			exampleLink.setUrl(url);
		}

		exampleLink.setRenderAsButton(cbRenderAsButton.isSelected());

		exampleLink.setText(tfLinkLabel.getText());

		if (cbSetImage.isSelected()) {
			exampleLink.setImage("/image/attachment.png");
			exampleLink.setImagePosition((ImagePosition) ddImagePosition.getSelected());
		}

		exampleLink.setDisabled(cbDisabled.isSelected());

		if (tfAccesskey.getText() != null && tfAccesskey.getText().length() > 0) {
			exampleLink.setAccessKey(tfAccesskey.getText().toCharArray()[0]);
		}
		if (cbOpenNew.isSelected()) {
			exampleLink.setOpenNewWindow(true);
			exampleLink.setTargetWindowName("_blank");
		} else {
			exampleLink.setOpenNewWindow(false);
		}

		linkContainer.add(exampleLink);
	}

	/**
	 * preparePaintComponents is called to use the applySettings to configure the link.
	 *
	 * @param request the web request.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			applySettings();
			setInitialised(true);
		}
	}
}
