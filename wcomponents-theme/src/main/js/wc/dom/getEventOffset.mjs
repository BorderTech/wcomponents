
/**
 * Determine the left and top coordinates of a mouse event (click, mousedown etc.).
 *
 * @function
 * @alias module:wc/dom/getEventOffset
 * @param {MouseEvent} $event The event.
 * @returns {{ X: number, Y: number }} An object encapsulating the offset.
 */
function getOffset($event) {
	const result = {

	};
	result.X = $event.pageX ? $event.pageX : ($event.clientX + document.documentElement.scrollLeft);
	result.Y = $event.pageY ? $event.pageY : ($event.clientY + document.documentElement.scrollTop);
	return result;
}
export default getOffset;
