package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WLink.ImagePosition;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * Utility methods for rendering file element.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class FileWidgetRendererUtil {

	/**
	 * Prevent instantiation of utility class.
	 */
	private FileWidgetRendererUtil() {
		// Do nothing
	}

	/**
	 * @param widget the multi file widget
	 * @param xml the xml string builder
	 * @param file the file to render
	 * @param idx the index of the file
	 */
	public static void renderFileElement(final WMultiFileWidget widget, final XmlStringBuilder xml,
			final FileWidgetUpload file, final int idx) {
		xml.appendTagOpen("ui:file");
		xml.appendAttribute("id", file.getFileId());
		xml.appendAttribute("name", file.getFile().getName());
		xml.appendAttribute("type", file.getFile().getMimeType());
		xml.appendAttribute("size", String.valueOf(file.getFile().getSize()));
		xml.appendClose();

		// Link to file
		xml.appendTagOpen("ui:link");
		xml.appendAttribute("id", widget.getId() + "-" + idx);
		xml.appendAttribute("url", widget.getFileUrl(file.getFileId()));

		// Thumb nail (if used)
		if (widget.isUseThumbnails()) {
			xml.appendAttribute("imageUrl", widget.getFileThumbnailUrl(file.getFileId()));
			// Position (if provided)
			ImagePosition thumbnailPosition = widget.getThumbnailPosition();
			if (thumbnailPosition != null) {
				switch (thumbnailPosition) {
					case NORTH:
						xml.appendAttribute("imagePosition", "n");
						break;
					case EAST:
						xml.appendAttribute("imagePosition", "e");
						break;
					case SOUTH:
						xml.appendAttribute("imagePosition", "s");
						break;
					case WEST:
						xml.appendAttribute("imagePosition", "w");
						break;
					default:
						throw new SystemException("Unknown image position: " + thumbnailPosition);
				}
			}
		}
		xml.appendClose();

		if (widget.getFileAjaxAction() == null) {
			xml.appendTagOpen("ui:windowAttributes");
			xml.appendAttribute("name", "uploadfile");
			xml.appendEnd();
		}

		xml.appendEscaped(file.getFile().getName());

		xml.appendEndTag("ui:link");
		xml.appendEndTag("ui:file");
	}

}
