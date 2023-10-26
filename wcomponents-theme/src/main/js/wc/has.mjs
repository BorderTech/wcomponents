/*
 * This module is a remnant of a more fully featured has implementation.
 * See: https://dojotoolkit.org/reference-guide/1.10/dojo/sniff.html
 */
const n = globalThis.navigator,
	dua = n.userAgent;

const features = {
	ff: /Gecko\/(\S+)/.test(dua),
	edge: /Edg.*\/(\S+)/.test(dua),
	webkit: /AppleWebKit\/(\S+)/.test(dua)
};
export default feature => features[feature];
