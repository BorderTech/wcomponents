(function(global) {
	/**
	 * Provides a compatibility layer for Node.
	 * Compatibility with DOM level 1:
	 * {@link http://www.w3.org/TR/1998/REC-DOM-Level-1-19981001/level-one-core.html#ID-1950641247}
	 * and DOM level 3: {@link http://www.w3.org/TR/DOM-Level-3-Core/core.html#Node-DOCUMENT_POSITION_DISCONNECTED}.
	*
	 * @module
	 * @private
	 *
	 * @example typeof Node != 'undefined';//can always be relied on
	 * Node.ELEMENT_NODE //can be relied on to be 1
	 *
	 * @example var el = document.createElement('div');
	 * console.log(el.nodeType === Node.ELEMENT_NODE?'always true':'never false');
	 */
	define(["wc/has"],
	/** @param has wc/has @ignore */
	function(has) {
		"use strict";
		var nodeList = {
			ELEMENT_NODE: 1,
			ATTRIBUTE_NODE: 2,
			TEXT_NODE: 3,
			CDATA_SECTION_NODE: 4,
			ENTITY_REFERENCE_NODE: 5,
			ENTITY_NODE: 6,
			PROCESSING_INSTRUCTION_NODE: 7,
			COMMENT_NODE: 8,
			DOCUMENT_NODE: 9,
			DOCUMENT_TYPE_NODE: 10,
			DOCUMENT_FRAGMENT_NODE: 11,
			NOTATION_NODE: 12,
			// The following few are introduced in DOM level 3
			DOCUMENT_POSITION_DISCONNECTED: 1,
			DOCUMENT_POSITION_PRECEDING: 2,
			DOCUMENT_POSITION_FOLLOWING: 4,
			DOCUMENT_POSITION_CONTAINS: 8,
			DOCUMENT_POSITION_CONTAINED_BY: 16
		};
		/**
		 */
		if (!has("global-node")) {
			global.Node = nodeList;
		}
		return nodeList;
	});
})(this);
