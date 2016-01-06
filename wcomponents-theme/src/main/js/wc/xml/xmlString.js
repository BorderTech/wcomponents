/**
 * Utility for serializing and deserializing XML.
 * @module
 * @requires module:wc/has
 */
define(["wc/has"], /** @param has wc/has @ignore */ function(has) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/xml/xmlString~XmlString
	 * @private
	 */
	function XmlString() {
		var serializer;

		/**
		 * Serialize an XML document.
		 *
		 * @function module:wc/xml/xmlString.to
		 * @param {Element} node An XML DOM object
		 * @returns {String} The serialized XML.
		 */
		this.to = function(node) {
			var result;
			if (node) {
				/*
				 * The reason we test for the non-standard "xml" property first is because of Internet Explorer.
				 * In IE9 XMLSerializer is implemented but does not serialize XML.
				 * In IE10 XMLSerializer works but not on all XML Dom objects - it depends how they were created.
				 * It simplifies the code to simply test for the .xml property first.
				 */
				if (typeof node.xml === "string") {
					result = node.xml;
				}
				else if (window.XMLSerializer) {
					serializer = serializer || new window.XMLSerializer();
					result = serializer.serializeToString(node);
				}
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
			var domParser;

			function useDomParser(xmlstring) {
				return domParser.parseFromString(xmlstring, "text/xml");
			}

			function useActiveX(xmlstring) {
				var xmlObj = new window.ActiveXObject("Microsoft.XMLDOM");
				xmlObj.async = false;
				if (!(xmlObj.loadXML(xmlstring))) {
					throw new Error("Unable to parse XML string\n" + xmlstring);
				}
				return xmlObj;
			}

			try {
				if (has("activex")) {
					this.from = useActiveX;
					return useActiveX(xmlstring);
				}
				else if (window.DOMParser) {
					domParser = new window.DOMParser();
					this.from = useDomParser;
					return useDomParser(xmlstring);
				}
				return null;
			}
			finally {
				xmlstring = null;
			}
		};
	}
	return /** @alias module:wc/xml/xmlString */ new XmlString();
});
