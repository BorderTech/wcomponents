package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WImageEditor;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.Iterator;
import java.util.List;

/**
 * The Renderer for the {@link WMultiFileWidget} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WMultiFileWidgetRenderer extends AbstractWebXmlRenderer {

	/**
	 * XML element name.
	 */
	private static final String TAG_NAME = "ui:multifileupload";
	/**
	 * Paints the given WMultiFileWidget.
	 *
	 * @param component the WMultiFileWidget to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMultiFileWidget widget = (WMultiFileWidget) component;
		XmlStringBuilder xml = renderContext.getWriter();

		// Check if rendering a file upload response
		String uploadId = widget.getFileUploadRequestId();
		if (uploadId != null) {
			handleFileUploadRequest(widget, xml, uploadId);
			return;
		}
		boolean readOnly = widget.isReadOnly();


		xml.appendTagOpen(TAG_NAME);
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");
		xml.appendOptionalAttribute("hidden", widget.isHidden(), "true");
		if (readOnly) {
			xml.appendAttribute("readOnly", "true");
		} else {
			long maxFileSize = widget.getMaxFileSize();
			int maxFiles = widget.getMaxFiles();
			WComponent dropzone = widget.getDropzone();
			WImageEditor editor = widget.getEditor();

			xml.appendOptionalAttribute("disabled", widget.isDisabled(), "true");
			xml.appendOptionalAttribute("required", widget.isMandatory(), "true");
			xml.appendOptionalAttribute("toolTip", widget.getToolTip());
			xml.appendOptionalAttribute("accessibleText", widget.getAccessibleText());
			xml.appendOptionalAttribute("acceptedMimeTypes", typesToString(widget.getFileTypes()));
			xml.appendOptionalAttribute("maxFileSize", maxFileSize > 0, maxFileSize);
			xml.appendOptionalAttribute("maxFiles", maxFiles > 0, maxFiles);
			if (dropzone != null) {
				xml.appendAttribute("dropzone", dropzone.getId());
			}
			if (editor != null) {
				xml.appendAttribute("editor", editor.getId());
				if (editor.getUseCamera()) {
					xml.appendAttribute("camera", true);
				}
			}
		}
		if (widget.getColumns() != null) {
			xml.appendAttribute("cols", widget.getColumns());
		}
		if (widget.getFileAjaxAction() != null) {
			xml.appendAttribute("ajax", "true");
		}

		List<Diagnostic> diags = readOnly ? null : widget.getDiagnostics(Diagnostic.ERROR);

		if (widget.getFiles().isEmpty()) {
			if (readOnly || diags == null || diags.isEmpty()) {
				xml.appendEnd();
				return;
			}
			xml.appendClose();
		} else {
			xml.appendClose();
			// Render files
			int i = 0;
			for (FileWidgetUpload file : widget.getFiles()) {
				FileWidgetRendererUtil.renderFileElement(widget, xml, file, i++);
			}
		}
		if (!readOnly && diags != null && !diags.isEmpty()) {
			DiagnosticRenderUtil.renderDiagnostics(widget, renderContext);
		}
		xml.appendEndTag(TAG_NAME);
	}

	/**
	 * Paint the response for the file uploaded in this request.
	 *
	 * @param widget the file widget to render
	 * @param xml the XML string builder
	 * @param uploadId the file id uploaded in this request
	 */
	protected void handleFileUploadRequest(final WMultiFileWidget widget, final XmlStringBuilder xml, final String uploadId) {

		FileWidgetUpload file = widget.getFile(uploadId);
		if (file == null) {
			throw new SystemException("Invalid file id [" + uploadId + "] to render uploaded response.");
		}
		int idx = widget.getFiles().indexOf(file);
		FileWidgetRendererUtil.renderFileElement(widget, xml, file, idx);
	}

	/**
	 * Flattens the list of accepted mime types into a format suitable for rendering.
	 *
	 * @param types the list of accepted mime types
	 * @return the list of accepted mime types in a format suitable for rendering
	 */
	private String typesToString(final List<String> types) {
		if (types == null || types.isEmpty()) {
			return null;
		}

		StringBuffer typesString = new StringBuffer();

		for (Iterator<String> iter = types.iterator(); iter.hasNext();) {
			typesString.append(iter.next());

			if (iter.hasNext()) {
				typesString.append(',');
			}
		}

		return typesString.toString();
	}

}
