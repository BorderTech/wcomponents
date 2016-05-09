package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFileWidget;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This example demonstrates the use of WFileWidget to select and upload a file. The contents of the uploaded file in
 * displayed in the "console" component when the "Upload" button is pressed.
 *
 * @author Martin Shevchenko
 */
public class SimpleFileUpload extends WContainer {

	private final WFileWidget fileWidget;
	private final WFileWidget imageWidget;
	private final WTextField console;

	/**
	 * Creates a SimpleFileUpload example.
	 */
	public SimpleFileUpload() {
		fileWidget = new WFileWidget();
		WLabel fileLabel = new WLabel("Select a file to upload", fileWidget);

		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		layout.addField(fileLabel, fileWidget);

		console = new WTextField();
		console.setReadOnly(true);

		WButton uploadBtn = new WButton("Upload");

		uploadBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String fileText;

				if (Util.empty(fileWidget.getFileName())) {
					fileText = "nothing uploaded";
				} else {
					fileText = fileWidget.getFile().getMimeType();
				}

				console.setText(fileText);
			}
		});
		layout.addField(uploadBtn);
		layout.addField("Uploaded mime type", console);

		add(new WHorizontalRule());
		layout = new WFieldLayout();
		layout.setLabelWidth(25);
		add(layout);
		imageWidget = new WFileWidget();
		imageWidget.setFileTypes(new ArrayList<>(Arrays.asList("image/jpeg",
				"image/png",
				"image/gif",
				"image/jpg")));
		layout.addField("Image file upload", imageWidget).getLabel().setHint("png, jpg, gif only.");

		WFileWidget constrainedWidget = new WFileWidget();
		constrainedWidget.setMaxFileSize(2000);
		layout.addField("File up to 2k", constrainedWidget);
		uploadBtn = new WButton("Upload constrained files");
		layout.addField(uploadBtn);
	}
}
