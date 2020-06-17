package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Image;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WEditableImage;
import com.github.bordertech.wcomponents.WFigure;
import com.github.bordertech.wcomponents.WImageEditor;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.file.File;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import java.awt.Dimension;

/**
 * Example of using {@link WImageEditor} with a static Image.
 * <p>
 * To use the image editor with a static image you need to use a combination of
 * {@link WEditableImage}, {@link WImageEditor} and {@link WMultiFileWidget}.
 * </p>
 * <p>
 * {@link WEditableImage} provides the static image.
 * </p>
 * <p>
 * {@link WImageEditor} provides the image editor widget.
 * </p>
 * <p>
 * {@link WMultiFileWidget} is used by the image editor to upload the edited image.
 * </p>
 * <p>
 * Some custom javascript is also used to link the image to the uploader and also demonstrate how the editor can be
 * configured.
 * </p>
 */
public class WImageEditorForImageExample extends WPanel {

	private static final Dimension REDACT_DIMENION = new Dimension(180, 240);

	private final WTemplate script = new WTemplate("com/github/bordertech/wcomponents/examples/initImageEditExample.txt", TemplateRendererFactory.TemplateEngine.PLAINTEXT);

	private final WMultiFileWidget fileUploadWidget = new WMultiFileWidget() {
		@Override
		public boolean isHidden() {
			return true;
		}
	};

	private final WEditableImage editImage = new WEditableImage(fileUploadWidget);

	/**
	 * Create example.
	 */
	public WImageEditorForImageExample() {

		// Add the script to Config the Editor
		add(script);

		// Panel that can be used via AJAX to refresh image
		final WPanel detail = new WPanel();
		add(detail);

		// Add the image editor widget
		final WImageEditor editor = new WImageEditor();
		editor.setSize(REDACT_DIMENION);
		detail.add(editor);

		// Bind the editor to the file upload
		fileUploadWidget.setEditor(editor);

		// Setup the file upload to handle editted file
		fileUploadWidget.setFileTypes(new String[]{"image/*"});
		fileUploadWidget.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				handleFileEditted();
			}
		});
		detail.add(fileUploadWidget);

		// Setup the image content
		ImageResource content = new ImageResource("/com/github/bordertech/wcomponents/examples/portlet-portrait.jpg", "portrait", REDACT_DIMENION);
		editImage.setImage(new MyContent(content));

		// Put the image in a Figure
		final WFigure imageHolder = new WFigure(editImage, content.getDescription());
		detail.add(imageHolder);

		// Create the AJAX trigger to refresh the Image
		WAjaxControl ajax = new WAjaxControl(fileUploadWidget, detail);
		detail.add(ajax);
	}

	private void handleFileEditted() {
		if (!fileUploadWidget.getFiles().isEmpty()) {
			File editted = fileUploadWidget.getFiles().get(0).getFile();
			getImageContent().setUpdatedBytes(editted.getBytes());
		}
		fileUploadWidget.reset();
	}

	private MyContent getImageContent() {
		return (MyContent) editImage.getImage();
	}

	/**
	 * Hold the image content and allow it to be updated.
	 */
	private static class MyContent implements Image {

		private final Image backing;

		private byte[] updatedBytes = null;

		public MyContent(final Image backing) {
			this.backing = backing;
		}

		@Override
		public Dimension getSize() {
			return backing.getSize();
		}

		@Override
		public byte[] getBytes() {
			if (updatedBytes == null) {
				return backing.getBytes();
			}
			return updatedBytes;
		}

		@Override
		public String getDescription() {
			return backing.getDescription();
		}

		@Override
		public String getMimeType() {
			return backing.getMimeType();
		}

		public void setUpdatedBytes(final byte[] updatedBytes) {
			this.updatedBytes = updatedBytes;
		}

	}

}
