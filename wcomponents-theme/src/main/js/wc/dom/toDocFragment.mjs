/**
 * "Safe" conversion of HTML to DocumentFragment.
 * @param {String} html the HTML to convert to a document fragment
 * TODO doubt we need this any more, deleted half of it already (also could use HTML templates)
 */
export default function toDocFragment(html) {
	const result = document.createDocumentFragment();
	const tmpDF = document.createDocumentFragment();
	const tmpElement = tmpDF.ownerDocument.createElement("div");
	const tmpContainer = tmpDF.appendChild(tmpElement);
	tmpContainer.innerHTML = html;
	let next;
	while ((next = tmpContainer.firstChild)) {
		result.appendChild(next);
	}
	return result;
}
