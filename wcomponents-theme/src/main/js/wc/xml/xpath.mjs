/**
 * Utility for executing XPath queries in JavaScript.
 *
 * Executes an XPath query and returns the result.
 *
 * @function  module:wc/xml/xpath.query
 * @param {string} xpath The xpath query.
 * @param {boolean} singleNode if true will return the first match.
 * @param {Element|Document} element The XML Document or Element to query.
 * @param {boolean} [relative] If true the query is relative to the element, otherwise the whole document will be searched.
 * @returns {Element|Array} The result of the xpath query.
 */
function query(xpath, singleNode, element, relative) {
	let result;
	if (element) {
		let match,
			doc,  // will ALWAYS be of type Document
			context;  // will be either a Document or an Element
		if (element.ownerDocument) {
			doc = element.ownerDocument;
			context = relative ? element : doc;
		} else if (element.nodeType === window.Node.DOCUMENT_NODE) {
			doc = context = element;
		}
		const nsResolver = document.createNSResolver(doc.documentElement);
		if (singleNode) {
			match = doc.evaluate(xpath, context, nsResolver, window.XPathResult.FIRST_ORDERED_NODE_TYPE, null);
			result = match.singleNodeValue;
		} else {
			match = doc.evaluate(xpath, context, nsResolver, window.XPathResult.ANY_TYPE, null);
			const arr = [];
			result = match.iterateNext();
			while (result) {
				arr[arr.length] = result;
				result = match.iterateNext();
			}
			result = arr;
		}
	}
	return result;
}
export default { query };
