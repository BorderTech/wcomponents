/**
 * This module exists to abstract away the underlying printf implementation.
 * sprintf attaches itself to "window" if it can find nothing else.
 * It is not a native ES module.
 */
import "lib/sprintf.js";

/**
 * @param {any[]} args
 * @returns {string}
 */
export default function(...args) {
	// @ts-ignore
	return window.sprintf(...args);
}
