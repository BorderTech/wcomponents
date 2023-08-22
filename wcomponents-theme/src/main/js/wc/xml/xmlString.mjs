/**
 * Utility for serializing and deserializing XML.
 * @private
 */

let serializer;

/**
 * Serialize an XML document.
 *
 * @function module:wc/xml/xmlString.to
 * @param {XMLDocument} node An XML DOM object
 * @returns {String} The serialized XML.
 */
function to(node) {
	let result;
	if (node) {
		serializer = serializer || new globalThis.XMLSerializer();
		result = serializer.serializeToString(node);
	}
	return result || null;
}

/**
 * Deserialize an XML string.
 *
 * @function module:wc/xml/xmlString.from
 * @param {String} xmlstring An XML string.
 * @returns {Document} An XML document.
 */
function from(xmlstring) {
	const domParser = new globalThis.DOMParser();
	return domParser.parseFromString(xmlstring, "text/xml");
}

export default { from, to };
