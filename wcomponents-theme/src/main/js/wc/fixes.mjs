/*
 * This module determines which browser specific fixes we need to load.
 * Modules loaded here are different to those loaded in the compat module because they are not needed by the scripting
 * environment per se, but rather they are needed a little later - before the user interacts with the page (hopefully)
 * but after other modules have initialized. The return object of this module is an array of module name strings. This
 * module should not be used except in the environment set-up in XSLT.
 *
 * @module
 */
const global = window,
	result = [],
	n = global.navigator,
	dua = n.userAgent;

const isFirefox = /Gecko\/(\S+)/.test(dua);
const isEdge = /Edg.*\/(\S+)/.test(dua);
const isWebkit = /AppleWebKit\/(\S+)/.test(dua);

if (isWebkit && !isEdge) {
	result.push("wc/fix/focus_webkit");
} else if (isFirefox) {
	result.push("wc/fix/shiftKey_ff");
}
export default result;
