package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WImageEditor;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * Example of using {@link WImageEditor} with a file upload.
 * <p>
 * To use the image editor with a file upload you need to use {@link WImageEditor} and {@link WMultiFileWidget}.
 * </p>
 * <p>
 * {@link WMultiFileWidget} is used to select the file and allow it be edited before being uploading.
 * </p>
 * <p>
 * {@link WImageEditor} provides the image editor widget.
 * </p>
 */
public class WImageEditorForMultiFileWidgetExample extends WPanel {

	/**
	 * Creates the example.
	 */
	public WImageEditorForMultiFileWidgetExample() {

		// Messages for uploaded files
		final WMessages messages = new WMessages();
		add(messages);

		// Add the image editor widget
		final WImageEditor editor = new WImageEditor();
		editor.setUseCamera(true);
		add(editor);

		final WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);

		// Setup the file upload to handle editted file
		final WMultiFileWidget fileUploadWidget = new WMultiFileWidget();
		fileUploadWidget.setEditor(editor);
		fileUploadWidget.setUseThumbnails(true);
		fileUploadWidget.setFileTypes(new String[]{"image/*"});
		fileUploadWidget.setMandatory(true);
		layout.addField("Upload a file", fileUploadWidget);

		// Upload button
		WButton submit = new WButton("Submit");
		submit.setAction(new ValidatingAction(messages.getValidationErrors(), layout) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// Create a message for each file uploaded
				for (WMultiFileWidget.FileWidgetUpload uploaded : fileUploadWidget.getFiles()) {
					messages.success("Uploaded file [" + uploaded.getFile().getFileName() + "].");
				}
			}
		});
		layout.addField(submit);

	}

}
