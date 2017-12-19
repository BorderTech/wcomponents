package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WEditableImage;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
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
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
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

	private static final int DEFAULT_IMAGE_WIDTH = 300;
	private static final int DEFAULT_IMAGE_HEIGHT = 400;
	private static final String OVERLAY_URL = "/image/overlay.png";
	private final WNumberField cols = new WNumberField();
	private final WNumberField size = new WNumberField();
	private final WNumberField maxfiles = new WNumberField();
	private final WNumberField previewHeight = new WNumberField();
	private final WCheckBox showThumnails = new WCheckBox();
	private final WCheckBox renderInline = new WCheckBox();
	private final WCheckBox mandatory = new WCheckBox();
	private final WCheckBox readonly = new WCheckBox();
	private final WCheckBox imageEditorShowOverlay = new WCheckBox();
	private final WCheckBox imageEditorIsFace = new WCheckBox();
	private final WNumberField editorWidth = new WNumberField();
	private final WNumberField editorHeight = new WNumberField();
	private final WFigure imageHolder;

	/**
	 * Construct.
	 */
	public WMultiFileWidgetAjaxExample() {
		WValidationErrors errors = new WValidationErrors();
		add(errors);

		WFieldLayout paramsLayout = new WFieldLayout();
		paramsLayout.setLabelWidth(25);
		paramsLayout.setMargin(new Margin(null, null, Size.LARGE, null));
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

		WFieldSet imageEditorFieldSet = new WFieldSet("Image Editor Controls");
		imageEditorFieldSet.setIdName("image_edit_parameters");
		WFieldLayout imageEditorParmsLayout = new WFieldLayout();
		imageEditorParmsLayout.setLabelWidth(25);
		imageEditorParmsLayout.addField("Show overlay", imageEditorShowOverlay);
		imageEditorParmsLayout.addField("Facial Image", imageEditorIsFace);
		imageEditorParmsLayout.addField("Width", editorWidth);
		imageEditorParmsLayout.addField("Height", editorHeight);
		imageEditorFieldSet.add(imageEditorParmsLayout);
		add(imageEditorFieldSet);
		imageEditorFieldSet.setMargin(new Margin(null, null, Size.XL, null));

		showThumnails.setSelected(true);
		paramsLayout.addField("Show thumbnails", showThumnails);

		paramsLayout.addField("Render inline", renderInline);

		paramsLayout.addField("Mandatory", mandatory);

		paramsLayout.addField("Readonly", readonly);

		WPanel split = new WPanel();
		split.setLayout(new ColumnLayout(new int[]{50, 50}, 12, 0));
		split.setHtmlClass(HtmlClassProperties.RESPOND);
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

				// setMaxFileSize(1000000);
			}
		};
		// widget.setColumns(2);
		widget.setUseThumbnails(true);
		widget.setDropzone(split);
		widget.setFileTypes(new String[]{"image/*"});

		layout.addField("Upload", widget);

		editorWidth.setMinValue(1);
		editorHeight.setMinValue(1);

		final WImageEditor editor = new WImageEditor() {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				int width = DEFAULT_IMAGE_WIDTH;
				int height = DEFAULT_IMAGE_HEIGHT;

				if (editorWidth.getValue() != null) {
					width = editorWidth.getValue().intValue();
				}
				if (editorHeight.getValue() != null) {
					height = editorHeight.getValue().intValue();
				}
				Dimension dimension = new Dimension(width, height);
				setSize(dimension);

				if (imageEditorShowOverlay.isSelected()) {
					InternalResource overlay = new ImageResource(OVERLAY_URL, "Overlay image shows visible guidlines");
					setOverlayUrl(overlay.getTargetUrl());
				} else {
					setOverlayUrl("");
				}

				// setIsFace(imageEditorIsFace.isSelected());
			}
		};
		editor.setUseCamera(true);

		widget.setEditor(editor);
		add(editor);

		previewHeight.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				widget.clearThumbnails();
			}
		});

		showThumnails.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				widget.setUseThumbnails(showThumnails.isSelected());
			}
		});

		renderInline.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				editor.setRenderInline(renderInline.isSelected());
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

		final WText hackFaceTracker = new WText();
		hackFaceTracker.setEncodeText(false);
		hackFaceTracker.setText("<script defer=\"defer\">(function() {\n"
				+ "		require([\"wc/ui/facetracking\"], function(facetracking) {\n"
				+ "			window.setTimeout(function() {			\n"
				+ "			var container = document.getElementById(\"image_edit_parameters\"),\n"
				+ "				interval = createRange(\"_interval\", \"Interval\"),\n"
				+ "				minNeighbors = createRange(\"_minNeighbours\", \"Min Neighbours\"),\n"
				+ "				confidence = createRange(\"_confidenceThreshold\", \"Confidence Threshold\", -10, 10, 0.1);\n"
				+ "			if (container) {\n"
				+ "				container.appendChild(interval);\n"
				+ "				container.appendChild(minNeighbors);\n"
				+ "				container.appendChild(confidence);\n"
				+ "			}}, 1000);\n"
				+ "			function createRange(prop, lbl, min, max, step) {\n"
				+ "				var onChange = function(element) {\n"
				+ "						if (element) {\n"
				+ "							var val = element.value;\n"
				+ "							facetracking[prop] = (val * 1);\n"
				+ "							element.title = val;\n"
				+ "						}\n"
				+ "						label.textContent = lbl + \" (\" + facetracking[prop] + \")\";\n"
				+ "					},\n"
				+ "					result = document.createElement(\"div\"),\n"
				+ "					label = result.appendChild(document.createElement(\"label\")),\n"
				+ "					range = result.appendChild(document.createElement(\"input\"));\n"
				+ "				range.setAttribute(\"type\", \"range\");\n"
				+ "				range.setAttribute(\"min\", min || \"0\");\n"
				+ "				range.setAttribute(\"max\", max || \"10\");\n"
				+ "				range.setAttribute(\"step\", step || \"1\");\n"
				+ "				range.setAttribute(\"value\", facetracking[prop]);\n"
				+ "				range.addEventListener(\"change\", function($event) {\n"
				+ "					onChange($event.target);\n"
				+ "				}, false);\n"
				+ "				onChange();\n"
				+ "				return result;\n"
				+ "			}\n"
				+ "		});\n"
				+ "	})();</script>");

		imageEditorIsFace.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				editor.setIsFace(imageEditorIsFace.isSelected());
				if (hackFaceTracker.getParent() == null && imageEditorIsFace.isSelected()) {
					add(hackFaceTracker);
				}
			}
		});

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
		add(new WAjaxControl(renderInline, widget));
		add(new WAjaxControl(mandatory, layout));
		add(new WAjaxControl(readonly, layout));
		add(new WAjaxControl(maxfiles, layout));
		add(new WAjaxControl(size, contentPanel));
		add(new WAjaxControl(maxfiles, layout));
		add(new WAjaxControl(widget, imageHolder));
	}
}
