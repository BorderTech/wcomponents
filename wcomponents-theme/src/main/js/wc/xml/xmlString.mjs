/**
 * Utility for serializing and deserializing XML.
 * @private
 */

let serializer;

/**
 * Serialize an XML document.
 *
 * @function module:wc/xml/xmlString.to
 * @param {Document} node An XML DOM object
 * @returns {String} The serialized XML.
 */
function to(node) {
	if (node) {
		serializer = serializer || new XMLSerializer();
		return serializer.serializeToString(node);
	}
	return null;
}

/**
 * Deserialize an XML string.
 *
 * @function module:wc/xml/xmlString.from
 * @param {String} xmlstring An XML string.
 * @returns {Document} An XML document.
 */
function from(xmlstring) {
	const domParser = new DOMParser();
	return domParser.parseFromString(xmlstring?.trim(), "text/xml");
}

export default { from, to };
