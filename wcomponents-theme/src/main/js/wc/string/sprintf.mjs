/**
 * This module exists to abstract away the underlying printf implementation.
 * sprintf attaches itself to "window" if it can find nothing else.
 * It is not a native ES module.
 */
import "sprintf-js/src/sprintf.js";

const { window } = globalThis;

/**
 * @param {any[]} args - ?
 * @returns {string} ?
 */
export default function sprintf (...args) {
	// @ts-ignore
	return window.sprintf(...args);
}
