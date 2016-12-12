package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * Example use of the {@link WMultiFileWidget} component.
 * </p>
 * <p>
 * WMultiFileWidget allows user to upload multiple files. File information is rendered for each upload. The file link
 * pops up a window to show the uploaded content.
 * </p>
 * <ul>
 * <li>Use <code>getSelectedFiles()</code> to get the checked file uploads only.</li>
 * <li>Use <code>getFiles()</code> to get all file uploads.</li>
 * <li>Use <code>setMaxFileSize(long)</code> to set the maximum size of files that can be uploaded.</li>
 * <li>Use <code>setContentType(String[])</code> to set the file types that will be accepted (setting no types implies
 * that all types are accepted).</li>
 * </ul>
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class WMultiFileWidgetExample extends WContainer {

	private final WMultiFileWidget allFiles = new WMultiFileWidget();
	private final WMultiFileWidget imageFiles = new WMultiFileWidget();
	private final WMultiFileWidget textFiles = new WMultiFileWidget();
	private final WMultiFileWidget pdfFiles = new WMultiFileWidget();

	/**
	 * The text area to output messages from the actions.
	 */
	private final WTextArea console = new WTextArea();

	/**
	 * Creates a WMultiFileWidgetExample.
	 */
	public WMultiFileWidgetExample() {
		add(new WHeading(HeadingLevel.H2, "A WMultiFileWidget that accepts all file types and has a dropzone."));

		// Multi file upload widgets
		// smallFiles.setMaxFileSize(1024);
		allFiles.setAccessibleText("File selector for all files");
		WPanel dropzone = new WPanel(WPanel.Type.ACTION);
		dropzone.setTitleText("File Upload");
		WLabel labelAll = new WLabel("Drop files in here to upload or use selector below", allFiles);
		dropzone.add(labelAll);
		dropzone.add(allFiles);
		add(dropzone);
		allFiles.setDropzone(dropzone);

		add(new WHeading(HeadingLevel.H2, "A WMultiFileWidget that accepts images of any type and displays thumbnails in two columns"));

		imageFiles.setFileTypes(new String[]{"image/*"});
		imageFiles.setAccessibleText("File selector for image files");
		imageFiles.setUseThumbnails(true);
		imageFiles.setColumns(2);
		add(imageFiles);

		add(new WHeading(HeadingLevel.H2, "A WMultiFileWidget that accepts one text file displays thumbnails."));

		textFiles.setFileTypes(new String[]{".txt"});  // N.B. this one uses an extension instead of a mime type "text/plain"
		// textFiles.setMaxFileSize(8 * 1024);
		textFiles.setAccessibleText("File selector for text files");
		textFiles.setUseThumbnails(true);
		textFiles.setMaxFiles(1);
		add(textFiles);

		add(new WHeading(HeadingLevel.H2, "A WMultiFileWidget that accepts pdfs and html files up to a max size of 5 MB."));

		pdfFiles.setFileTypes(new String[]{"application/pdf", "text/html"});
		pdfFiles.setMaxFileSize(5 * 1024 * 1024);
		pdfFiles.setAccessibleText("File selector for PDF or HTML files");
		pdfFiles.setUseThumbnails(true);
		add(pdfFiles);

		add(new WHorizontalRule());

		WPanel panel = new WPanel();
		panel.setLayout(new FlowLayout(Alignment.VERTICAL, 0, 6));
		add(panel);

		// Actions
		// Process Files
		WContainer container = new WContainer();
		panel.add(container);
		WButton processFiles = new WButton("Process Files");
		container.add(processFiles);
		container.add(new WText(" - Performs server side examination of the selected files."));

		// Round trip
		container = new WContainer();
		panel.add(container);
		WButton roundtrip = new WButton("Roundtrip");
		container.add(roundtrip);
		container.add(new WText(" - Performs a round trip to the server, which proves we don't lose access to the files already selected."));

		// Toggle readonly
		container = new WContainer();
		panel.add(container);
		WButton readonly = new WButton("Toggle Readonly");
		container.add(readonly);
		container.add(new WText(" - Toggle the readonly state of all the multiFileWidgets in the example."));

		// Reset
		container = new WContainer();
		panel.add(container);
		WButton reset = new WButton("Reset");
		container.add(reset);
		container.add(new WText(" - Clear all the file selections and the console."));

		add(new WHorizontalRule());

		// Console to output messages from the actions.
		add(new WHeading(HeadingLevel.H2, "Console"));

		console.setRows(12);
		console.setColumns(100);
		WLabel label = new WLabel("Console", console);
		label.setHidden(true);
		add(label);
		add(console);

		// Action logic
		processFiles.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				processFiles();
			}
		});

		roundtrip.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				console.setText("Roundtrip performed");
			}
		});

		readonly.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				toggleReadOnly();
			}
		});

		reset.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				WMultiFileWidgetExample.this.reset();
			}
		});
	}

	/**
	 * Toggles the readonly state off all file widgets in the example.
	 */
	private void toggleReadOnly() {
		allFiles.setReadOnly(!allFiles.isReadOnly());
		imageFiles.setReadOnly(!imageFiles.isReadOnly());
		textFiles.setReadOnly(!textFiles.isReadOnly());
		pdfFiles.setReadOnly(!pdfFiles.isReadOnly());
	}

	/**
	 * Outputs information on the uploaded files to the text area.
	 */
	private void processFiles() {
		StringBuffer buf = new StringBuffer();
		appendFileDetails(buf, allFiles);
		appendFileDetails(buf, textFiles);
		appendFileDetails(buf, pdfFiles);
		console.setText(buf.toString());
	}

	/**
	 * Appends details of the uploaded files to the given string buffer.
	 *
	 * @param buf the buffer to append file details to.
	 * @param fileWidget the WFieldWidget to obtain the files from.
	 */
	private void appendFileDetails(final StringBuffer buf, final WMultiFileWidget fileWidget) {
		List<FileWidgetUpload> files = fileWidget.getFiles();

		if (files != null) {
			for (FileWidgetUpload file : files) {
				String streamedSize;

				try {
					InputStream in = file.getFile().getInputStream();

					int size = 0;
					while (in.read() >= 0) {
						size++;
					}

					streamedSize = String.valueOf(size);
				} catch (IOException e) {
					streamedSize = e.getMessage();
				}

				buf.append("Name: ").append(file.getFile().getName());
				buf.append("\nSize: ").append(streamedSize).append(" bytes\n\n");
			}
		}
	}
}
