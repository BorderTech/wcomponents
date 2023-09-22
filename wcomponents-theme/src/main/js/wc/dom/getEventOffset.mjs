
/**
 * Determine the left and top coordinates of a mouse event (click, mousedown etc.).
 *
 * @function
 * @alias module:wc/dom/getEventOffset
 * @param {MouseEvent} $event The event.
 * @returns {{ X: number, Y: number }} An object encapsulating the offset.
 */
function getOffset({ pageX, pageY, view, clientX, clientY }) {
	return {
		X: pageX || (clientX + view.document.documentElement.scrollLeft),
		Y: pageY || (clientY + view.document.documentElement.scrollTop)
	};
}
export default getOffset;
