import getBox from "wc/dom/getBox.mjs";

/**
 * Get the viewport size.
 *
 * @function module:wc/dom/getViewportSize
 * @param {Boolean} [withoutScrollbars] If true then attempt to account for the scroll bar width.
 * @param {Window} [view] Optionally provide the window to use
 * @returns {{ width: number, height: number }} The viewport size, in pixels, encapsulated in an object.
 */
export default function(withoutScrollbars, view=window) {
	const DOCUMENT_ELEMENT = view.document.documentElement,
		SELF = view.self,
		thisViewportView = view.top.visualViewport,
		WIDTH = "width",
		HEIGHT = "height";  // to improve compression
	const result = {
		width: 0,
		height: 0
	};
	if (withoutScrollbars) {
		result[WIDTH] = DOCUMENT_ELEMENT.clientWidth;
		result[HEIGHT] = DOCUMENT_ELEMENT.clientHeight;
	} else if (thisViewportView) {
		result[WIDTH] = thisViewportView.width;
		result[HEIGHT] = thisViewportView.height;
	} else if (typeof SELF.innerWidth !== "undefined") {
		result[WIDTH] = SELF.innerWidth;
		result[HEIGHT] = SELF.innerHeight;
	} else if (DOCUMENT_ELEMENT?.getBoundingClientRect) {
		const box = getBox(DOCUMENT_ELEMENT);
		result[WIDTH] = box[WIDTH];
		result[HEIGHT] = box[HEIGHT];
	} else {
		result[WIDTH] = DOCUMENT_ELEMENT.clientWidth || view.document.body.clientWidth || 0;
		result[HEIGHT] = DOCUMENT_ELEMENT.clientHeight || view.document.body.clientHeight || 0;
	}
	return result;
}
