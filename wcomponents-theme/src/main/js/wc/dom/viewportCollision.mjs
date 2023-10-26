import getBox from "wc/dom/getBox.mjs";
import getViewportSize from "wc/dom/getViewportSize.mjs";

/**
 * Tests if an element is partially out of viewport.
 *
 * @function
 * @alias module:wc/dom/viewportCollision
 *
 * @param {Element} element The element to test for viewport collision.
 * @returns {{ n: number, s: number, e: number, w: number}} with amount of collision (if any) where n & w < 0 when colliding and
 *    s & e > 0 when colliding
 */
function viewportCollision(element) {
	const result = {n: 0, e: 0, s: 0, w: 0},
		box = getBox(element),
		viewportSize = getViewportSize(true);  // get the size minus scroll-bar size

	if (box.left < 0) {
		result.w = box.left;
	}

	if (box.right > viewportSize.width) {
		result.e = (box.right - viewportSize.width);
	}

	if (box.top < 0) {
		result.n = box.top;
	}

	if (box.bottom > viewportSize.height) {
		result.s = (box.bottom - viewportSize.height);
	}
	return result;
}
export default viewportCollision;
