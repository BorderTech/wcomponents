/**
 * Utility for executing XPath queries in JavaScript.
 * @module
 * @requires module:wc/array/toArray
 */
define(["wc/array/toArray"], /** @param toArray wc/array/toArray @ignore */ function(toArray) {
	"use strict";
	var NS_RE = /^xmlns/,
		GETPROPERTY = "getProperty";

	/**
	 * @constructor
	 * @alias module:wc/xml/xpath~Xpath
	 * @private
	 */
	function Xpath() {
		/**
		 * This function allows xpath expressions to contain namespace prefixes in IE.
		 *
		 * IE10 and onwards have implemented a lovely new feature when you fetch XML via AJAX
		 * and use the "responseXML" property you get an XML document that does not support xpath
		 * in either the standards way OR the old MSXML way. You simply can't do it. Yes we could
		 * probably convert it here but then the returned nodes would be part of a different DOM.
		 * It is best for the caller to take care of this. You basically need to either convert it
		 * or set the responseType of your XHR to 'msxml-document' like this:<
		 *<pre><code>
		* var xhr = new XMLHttpRequest();
		* xhr.open(method, url, true);
		* try { xhr.responseType = 'msxml-document'; } catch (e) {}
		* //...
		*    var doc = xhr.responseXML;
		* // 'doc' now contains an MSXML document in IE10's Standards and Quirks document modes
		 </code></pre>
		 * @see {@link http://blogs.msdn.com/b/ie/archive/2012/07/19/xmlhttprequest-responsexml-in-ie10-release-preview.aspx}
		 *
		 * @function
		 * @private
		 * @param {Document} doc The XML document which needs to use XML namespaces in IE.
		 */
		function ieNamespaceResolver(doc) {
			var ns = [], i, next, candidates, IXMLDOMDocument = (GETPROPERTY in doc);
			if (IXMLDOMDocument) {
				if (!doc[GETPROPERTY]("SelectionNamespaces")) {  // assume if it has been set it is set properly
					candidates = doc.documentElement.attributes;
					for (i = 0; i < candidates.length; i++) {
						next = candidates[i];
						if (NS_RE.test(next.name)) {
							ns[ns.length] = next.name + "='" + next.value + "'";
						}
					}
					if (ns.length) {
						ns = ns.join(" ");
						console.log("IE namespace resolver: ", ns);
						doc.setProperty("SelectionNamespaces", ns);
					}
				}
			}
			else {  // exceptions will probably be thrown by the caller because of this
				console.error("Not an MSXML document, can't do xpath");  // should we throw an exception?
			}
		}

		/**
		 * Executes an XPath query and returns the result.
		 *
		 * @function  module:wc/xml/xpath.query
		 * @param {string} xpath The xpath query.
		 * @param {boolean} singleNode if true will return the first match.
		 * @param {Element|Document} element The XML Document or Element to query.
		 * @param {boolean} relative If true the query is relative to the element, otherwise the whole document will be searched.
		 * @returns {Element|Array} The result of the xpath query.
		 */
		this.query = function(xpath, singleNode, element, relative) {
			var result, arr, match, nsResolver,
				doc,  // will ALWAYS be of type Document
				context;  // will be either a Document or an Element
			if (element) {
				if (element.ownerDocument) {
					doc = element.ownerDocument;
					context = relative ? element : doc;
				}
				else if (element.nodeType === window.Node.DOCUMENT_NODE) {
					doc = context = element;
				}
				if (doc.evaluate) {
					nsResolver = document.createNSResolver(doc.documentElement);
					if (singleNode) {
						match = doc.evaluate(xpath, context, nsResolver, window.XPathResult.FIRST_ORDERED_NODE_TYPE, null);
						result = match.singleNodeValue;
					}
					else {
						match = doc.evaluate(xpath, context, nsResolver, window.XPathResult.ANY_TYPE, null);
						arr = [];
						result = match.iterateNext();
						while (result) {
							arr[arr.length] = result;
							result = match.iterateNext();
						}
						result = arr;
					}
				}
				else {  // internet explorer
					try {
						ieNamespaceResolver(doc);
						if (singleNode) {
							result = context.selectSingleNode(xpath);
						}
						else {
							result = context.selectNodes(xpath);
							if (result) {
								result = toArray(result);
							}
						}
					}
					catch (e) {
						console.log(e.message);
						result = null;
					}
				}
			}
			return result;
		};
	}
	return /** @alias module:wc/xml/xpath */ new Xpath();
});
