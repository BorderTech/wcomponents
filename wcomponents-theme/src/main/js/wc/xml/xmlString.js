/**
 * Utility for serializing and deserializing XML.
 * @module
 */
define([], function() {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/xml/xmlString~XmlString
	 * @private
	 */
	function XmlString() {
		let serializer;

		/**
		 * Serialize an XML document.
		 *
		 * @function module:wc/xml/xmlString.to
		 * @param {Element} node An XML DOM object
		 * @returns {String} The serialized XML.
		 */
		this.to = function(node) {
			let result;
			if (node) {
				serializer = serializer || new window.XMLSerializer();
				result = serializer.serializeToString(node);
			}
			return result || null;
		};

		/**
		 * Deserialize an XML string.
		 *
		 * @function module:wc/xml/xmlString.from
		 * @param {String} xmlstring An XML string.
		 * @returns {Document} An XML document.
		 */
		this.from = function(xmlstring) {
			const domParser = new window.DOMParser();
			return domParser.parseFromString(xmlstring, "text/xml");
		};
	}
	return /** @alias module:wc/xml/xmlString */ new XmlString();
});
