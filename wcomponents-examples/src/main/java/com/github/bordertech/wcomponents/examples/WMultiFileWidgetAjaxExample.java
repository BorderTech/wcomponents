package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WEditableImage;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFigure;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WImageEditor;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.awt.Dimension;

/**
 * Multifile upload with AJAX trigger.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiFileWidgetAjaxExample extends WContainer {

	private final WNumberField cols = new WNumberField();
	private final WNumberField size = new WNumberField();
	private final WNumberField maxfiles = new WNumberField();
	private final WNumberField previewHeight = new WNumberField();
	private final WCheckBox showThumnails = new WCheckBox();
	private final WCheckBox mandatory = new WCheckBox();
	private final WCheckBox readonly = new WCheckBox();
	private final WFigure imageHolder;

	/**
	 * Construct.
	 */
	public WMultiFileWidgetAjaxExample() {
		WValidationErrors errors = new WValidationErrors();
		add(errors);

		WFieldLayout paramsLayout = new WFieldLayout();
		paramsLayout.setLabelWidth(25);
		paramsLayout.setMargin(new Margin(0, 0, 12, 0));
		add(paramsLayout);
		cols.setMinValue(0);
		cols.setMaxValue(8);
		paramsLayout.addField("How many columns of files?", cols);

		previewHeight.setMinValue(1);
		previewHeight.setMaxValue(150);
		paramsLayout.addField("Max height of preview images", previewHeight);

		size.setMinValue(1);
		paramsLayout.addField("Max height of viewed image", size);

		maxfiles.setMinValue(0);
		paramsLayout.addField("Max number of files to allow", maxfiles);

		showThumnails.setSelected(true);
		paramsLayout.addField("show thumbnails", showThumnails);

		paramsLayout.addField("Mandatory", mandatory);

		paramsLayout.addField("Readonly", readonly);

		WPanel split = new WPanel();
		split.setLayout(new ColumnLayout(new int[]{50, 50}, 12, 0));
		add(split);

		// Left
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		split.add(layout);

		final WMultiFileWidget widget = new WMultiFileWidget() {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				if (cols.getValue() != null) {
					setColumns(cols.getValue().intValue());
				} else {
					setColumns(1);
				}
				if (maxfiles.getValue() != null) {
					setMaxFiles(maxfiles.getValue().intValue());
				}
				setMandatory(mandatory.isSelected(), "This field is required");
				setReadOnly(readonly.isSelected());
				if (previewHeight.getValue() != null) {
					if (previewHeight.getValue().intValue() == -1) {
						setThumbnailSize(null);
					} else {
						setThumbnailSize(new Dimension(-1, previewHeight.getValue().intValue()));
					}
				} else {
					setThumbnailSize(null);
				}
			}
		};
		// widget.setColumns(2);
		widget.setUseThumbnails(true);
		widget.setDropzone(split);
		widget.setFileTypes(new String[]{"image/*"});

		layout.addField("Upload", widget);
		WImageEditor editor = new WImageEditor();
		editor.setSize(new Dimension(300, 400));
		editor.setUseCamera(true);
		InternalResource overlay = new ImageResource("/image/overlay.png", "Overlay image shows visible guidlines");
		editor.setOverlayUrl(overlay.getTargetUrl());
		widget.setEditor(editor);
		add(editor);

		previewHeight.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// TODO Auto-generated method stub
				widget.clearThumbnails();
			}
		});

		showThumnails.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// TODO Auto-generated method stub
				widget.setUseThumbnails(showThumnails.isSelected());
			}
		});

		mandatory.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				widget.setMandatory(mandatory.isSelected(), "This field is required");
			}
		});

		readonly.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				widget.setReadOnly(readonly.isSelected());
			}
		});

		maxfiles.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				if (maxfiles.getValue() != null) {
					widget.setMaxFiles(maxfiles.getValue().intValue());
				} else {
					widget.setMaxFiles(0);
				}
			}
		});

		WButton apply = new WButton("Apply");
		apply.setAction(new ValidatingAction(errors, widget) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				System.out.print(cols);
			}
		});

		add(apply);

		// Right
		final WPanel contentPanel = new WPanel();
		split.add(contentPanel);

		contentPanel.add(new WHeading(HeadingLevel.H2, "File View"));

		final WImage image = new WEditableImage(widget) {
			@Override
			public String getImageUrl() {
				String fileId = (String) getAttribute("image-fileid");
				if (fileId != null) {
					// Get the url each time to allow for step count in the URL
					return widget.getFileUrl(fileId);
				}
				return null;
			}

			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);

				if (size.getValue() != null) {
					setSize(new Dimension(-1, size.getValue().intValue()));
				}
			}
		};

		imageHolder = new WFigure(image, "") {
			@Override
			public boolean isHidden() {
				return image.getImageUrl() == null;
			}
		};
		contentPanel.add(imageHolder);

		// File AJAX action (ie selected)
		widget.setFileAjaxAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String fileId = (String) event.getActionObject();
				FileWidgetUpload file = widget.getFile(fileId);
				String url = widget.getFileUrl(fileId);
				image.reset();
				image.setAlternativeText(file.getFile().getDescription());
				image.setImageUrl(url);
				image.setAttribute("image-fileid", fileId);

				if (imageHolder.getDecoratedLabel() != null) {
					if (!"".equals(image.getAlternativeText())) {
						imageHolder.getDecoratedLabel().setBody(
								new WText(image.getAlternativeText()));
					} else {
						imageHolder.getDecoratedLabel().setBody(new WText("Unnamed Image."));
					}
				}
			}
		});

		// File changed action (removed from list)
		widget.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String fileId = (String) image.getAttribute("image-fileid");
				if (fileId != null) {
					// Check file id still in list
					FileWidgetUpload file = widget.getFile(fileId);
					if (file == null) {
						contentPanel.reset();
					}
				}
			}
		});

		add(new WAjaxControl(cols, layout));
		add(new WAjaxControl(previewHeight, widget));
		add(new WAjaxControl(showThumnails, widget));
		add(new WAjaxControl(mandatory, layout));
		add(new WAjaxControl(readonly, layout));
		add(new WAjaxControl(maxfiles, layout));
		add(new WAjaxControl(size, contentPanel));
		add(new WAjaxControl(maxfiles, layout));
		add(new WAjaxControl(widget, imageHolder));

	}
}
