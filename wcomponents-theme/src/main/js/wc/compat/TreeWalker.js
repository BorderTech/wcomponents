(function(global) {
	/**
	 * @module
	 * @private
	 * @requires module:wc/has
	 */
	define(["wc/has"], /** @param has wc/has @ignore */ function(has) {
		"use strict";
		/* #################################################################################################################
		 * Don't change this code.
		 * The bug is in your code, not here.
		 *
		 * If you DO change this code you MUST prove your change against unit tests that pass in native TreeWalker
		 * implementations as well as this implementation.
		 * ################################################################################################################ */

		if (!has("global-nodefilter")) {
			var nodeFilter = {
				FILTER_ACCEPT: 1,
				FILTER_REJECT: 2,
				FILTER_SKIP: 3,
				SHOW_ALL: -1,
				SHOW_ELEMENT: 1,
				SHOW_ATTRIBUTE: 2,
				SHOW_TEXT: 4,
				SHOW_CDATA_SECTION: 8,
				SHOW_ENTITY_REFERENCE: 16,
				SHOW_ENTITY: 32,
				SHOW_PROCESSING_INSTRUCTION: 64,
				SHOW_COMMENT: 128,
				SHOW_DOCUMENT: 256,
				SHOW_DOCUMENT_TYPE: 512,
				SHOW_DOCUMENT_FRAGMENT: 1024,
				SHOW_NOTATION: 2048
			};
			global["NodeFilter"] = nodeFilter;
		}


		/**
		 * Implementation of the DOM2 TreeWalker object for browsers that don't implement it natively.
		 * @constructor
		 * @private
		 * @alias module:wc/compat/TreeWalker~TreeWalker
		 * @param {Node} argRoot The root of the tree to be walked
		 * @param {number} argWhatToShow Bitmap of nodes to show in the tree walker.
		 * @param {Function} argFilter The filter function which determines the nodes in the tree which are acceptable.
		 *
		 * @throws {TypeError} A type error if argRoot is undefined (or null or JS equivalent) or does not have a nodeType property.
		 */
		function TreeWalker(argRoot, argWhatToShow, argFilter/* , argExpandEntityReferences */) {
			if (!argRoot || !argRoot.nodeType) {  // TODO, check exactly what args are allowed here
				throw new TypeError("Parameter 'argRoot' must be a DOM node.");
			}

			var self = this,
				root = argRoot,
				whatToShow = argWhatToShow,
				filter = argFilter ? (argFilter.acceptNode ? argFilter : {acceptNode: argFilter}) : null,
				nodeTypeMap = [undefined, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048],
				isFirstChild = false,
				isLastChild = false,
				isNextSibling = false,
				isNextNode = false,
				isPreviousNode = false,
				FILTER_ACCEPT = 1,
				FILTER_REJECT = 2,
				FILTER_SKIP = 3;

			/**
			 * The current node of the tree walker as it traverses a tree.
			 * @var
			 * @public
			 * @static
			 */
			this.currentNode = root;

			/**
			 * Get the previous node in the tree.
			 * @function module:wc/compat/TreeWalker~TreeWalker~previous
			 * @private
			 * @param {TreeWalker} self The current treeWalker instance.
			 * @returns {?Node}
			 * @throws {TypeError} A type error if self.currentNode is undefined.
			 */
			function previous(self) {
				var result,
					node = self.currentNode,
					filterResult = null,
					parent,
					child,
					backtrack,
					branch,
					temp,
					lastChild;
				if (!node) {
					throw new TypeError("currentNode has been set to null, check your usage of this property");
				}

				// last child processing resembles previousSibling processing if the lastChild node is filtered
				if (isLastChild) {
					node = node.lastChild;
					if (!node) {
						return null;
					}

					// TODO: need to determine what should happen if a FILTER_REJECT is returned, do we stop there,
					// or do we continue like we are by treating it like a FILTER_SKIP...
					// Also, I think this code is passing some elementes through the filter function twice..
					temp = node;
					while ((temp = temp.lastChild)) {
						// if this nodeType matches whatToShow then we pass it through the filter function
						if (nodeTypeMap[node.nodeType] & whatToShow) {
							if (filter ? filter.acceptNode(node) === FILTER_ACCEPT : true) {
								break;
							}
						}
						node = temp;
					}
					filterResult = 0; // trigger the filter function
				}

				PROCESSING: while (true) {
					// filter
					// first time in while loop don't worry about the filter, were just
					// interested in finding the next logical element in the tree
					if (filterResult === null) {
						filterResult = 0; // case for first time run - filterResult is null
					}
					else if (nodeTypeMap[node.nodeType] & whatToShow) { // if this nodeType matches whatToShow then we pass it through the filter function
						filterResult = filter ? filter.acceptNode(node) : FILTER_ACCEPT;
					}
					else { // 'hop' over/into this node
						filterResult = FILTER_SKIP;
					}
					// TODO typecheck filterResult

					// isLastChild and previousSibling stop traversing when a parentNode is accepted
					if (!isPreviousNode && parent) {
						if (filterResult === FILTER_ACCEPT) {
							result = null;
							break;
						}
						parent = null;
					}

					// if this node is accepted set & return it
					// except if there is a child, as we are just trying to find the
					// last deepest child of the previous branch
					// if a child node is accepted we store it as a backtrack reference
					// so that if a later descendent child NodeFilter.FILTER_REJECT's
					// we dont have to walk back up tree retesting each ancestor against the filter function,
					// we can just jump straight to the backtrack node
					if (filterResult === FILTER_ACCEPT) {
						if (isPreviousNode && child) {
							backtrack = node;
						}
						else {
							result = self.currentNode = node;
							break;
						}
					}

					// find the 'lastChild'
					if (child) {
						if (filterResult !== FILTER_REJECT) {  // check if this branch has been NodeFilter.FILTER_REJECTed
							if ((lastChild = child.lastChild)) {
								node = child = lastChild;
								continue PROCESSING;
							}
							else if (!(nodeTypeMap[node.nodeType] & whatToShow)) {  // is this node a type we can accept?
								if (node.previousSibling) {
									node = (node.previousSibling);
								}
								filterResult = FILTER_REJECT;
								child = null;
								continue PROCESSING;
							}
							else if (backtrack) {
								result = self.currentNode = backtrack;
								break;
							}
							// child is lastChild in the previous skipped branch
							else {
								node = branch;
							}
						}
						else if (backtrack) {
							result = self.currentNode = backtrack;
							break;
						}
						// if the child rejects and we have no successful backtrack node we reset node to
						// the top of this branch and go from there
						else {
							node = branch;
						}
					}

					// IE8 doesn't get this right in unit tests if root/node is a HTML5 element: even if the
					// HTML5 fix is applied but gets it right in practice
					if (node !== root) { // dont go further back then the root node
						/*
						 * Quick kludge for IE
						 * TODO: fix this properly because it hurts everyone else but only IE should be here anyway!!!
						 */
						if (root.nodeType === node.nodeType && root.nodeType === Node.ELEMENT_NODE) {
							if ((node.id || root.id) && node.id === root.id) {  // this is  a cheap test which IE will probably pass
								result = null;
								break;
							}
							else if ((root.outerHTML || node.outerHTML) && root.outerHTML === node.outerHTML) {  // this is a definitive IE test for the same elements if the element has no id
								result = null;
								break;
							}
						}
						// if a successful previousSibling is a branch (it has children) we will need to find it's last child
						// we capture it in case there is no successful lastChild in the branch so that can we return here to continue
						if ((child = branch = node.previousSibling)) {
							node = child;
							continue PROCESSING;
						}
						else if ((parent = node.parentNode)) {
							node = parent;
							continue PROCESSING;
						}
					}
					result = null;
					break;
				}
				return result;
			}

			/**
			 * Get the next node in the tree.
			 * @function module:wc/compat/TreeWalker~TreeWalker~next
			 * @private
			 * @param {TreeWalker} self The current treeWalker instance.
			 * @returns {?Node}
			 * @throws {TypeError} A type error if self.currentNode is undefined.
			 * @throws {Error} Throws if the tree cannot be processed - usually only if the tree is malformed.
			 */
			function next(self) {
				var result,
					queryRoot,
					node,
					filterResult = null,
					parent,
					child,
					sibling,
					trueParent, temp;
				node = queryRoot = self.currentNode;
				if (!node) {
					throw new TypeError("currentNode has been set to null, check your usage of this property");
				}

				if (isNextSibling) {
					trueParent = null;
					temp = node;

					// TODO: this is ineffieience, because it may not be required.
					// move closer to the actual test
					while ((temp = temp === root ? null : temp.parentNode)) {
						if (nodeTypeMap[temp.nodeType] & whatToShow) {
							if (filter ? filter.acceptNode(node) === FILTER_ACCEPT : true) {
								trueParent = temp.parentNode;
								break;
							}
						}
						else {
							break;
						}
					}
					filterResult = null;
				}

				// process each node depth first
				PROCESSING:
				while (true) { // <-- looks like a scary test huh
					// filter
					// first time in while loop don't worry about the filter, were just interested in finding the next logical
					// element in the tree and then filtering it
					if (filterResult === null) {
						filterResult = 0; // case for first time run - filterResult is null
					}
					else {
						// if this nodeType matches whatToShow then we pass it through the filter function
						if (nodeTypeMap[node.nodeType] & whatToShow) {
							filterResult = filter ? filter.acceptNode(node) : FILTER_ACCEPT;
						}
						else { // 'hop' over/into this node
							filterResult = FILTER_SKIP;
						}
						// TODO typecheck filterResult

						// if this node is accepted set & return it
						if (filterResult === FILTER_ACCEPT) {
							result = self.currentNode = node;
							break;
						}
					}

					// isNextSibling
					//   Only move into branches if the node is being skipped (i.e. the tree is being 'flattened').
					// isNextNode
					//   Always move into branches except when we filter reject the node (and hence the branch)
					if ((isNextNode && filterResult !== FILTER_REJECT) || (isNextSibling && filterResult === FILTER_SKIP)) {
						if ((child = node.firstChild)) {
							node = child;
							continue PROCESSING;
						}
					}

					// find a nextSibling, or backtrack to a parent with a nextSibling
					// note that backtracked parentNodes are not filtered, except that in the case of isNextSibling
					// isFirstChild
					//   We don't check siblings unless the are descendents of the queryNode since they cannot be considered a 'child'
					if ((isFirstChild ? node !== queryRoot : true) && (node !== root)) {
						parent = sibling = node;
						do {
							if ((sibling = sibling.nextSibling)) {
								if (node !== sibling) {
									node = sibling;
									continue PROCESSING;
								}
								else {
									throw new Error("Treewalker can not process this tree, appears malformed");
								}
							}
						}
						while ((parent = sibling = parent.parentNode) &&
								(isFirstChild ? parent !== queryRoot : true) &&
								(parent !== root) &&
								(parent !== trueParent));
					}
					// done processing, found nothing
					result = null;
					break;
				}
				return result;
			}

			/**
			 * Get the first node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 */
			this.firstChild = function() {
				isFirstChild = true;
				isNextSibling = true;
				isNextNode = true;
				return next(self);
			};

			/**
			 * Get the last node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 */
			this.lastChild = function() {
				isLastChild = true;
				isPreviousNode = false;
				return previous(self);
			};

			/**
			 * Get the next sibling node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 */
			this.nextSibling = function() {
				isFirstChild = false;
				isNextSibling = true;
				isNextNode = false;
				return next(self);
			};

			/**
			 * Get the next node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 */
			this.nextNode = function() {
				isFirstChild = false;
				isNextSibling = false;
				isNextNode = true;
				return next(self);
			};

			/**
			 * Get the previous sibling node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 */
			this.previousSibling = function() {
				isLastChild = false;
				isPreviousNode = false;
				return previous(self);
			};

			/**
			 * Get the previous node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 */
			this.previousNode = function() {
				isLastChild = false;
				isPreviousNode = true;
				return previous(self);
			};

			/**
			 * Get the parent node in the tree which is acceptable according to the tree walker's filter function.
			 * @function
			 * @public
			 * @returns {?Node}
			 * @throws {TypeError} Thrown if the current treeWalker instance currentNode is unset.
			 */
			this.parentNode = function() {
				var node = this.currentNode,
					filterResult;
				if (!node) {
					throw new TypeError("currentNode has been set to null, check your usage of this property");
				}
				while ((node = node === root ? null : node.parentNode)) {
					if (nodeTypeMap[node.nodeType] & whatToShow) {
						filterResult = filter ? filter.acceptNode(node) : FILTER_ACCEPT;
					}
					else {
						return null;
					}
					if (filterResult === FILTER_ACCEPT) {
						return (this.currentNode = node);
					}
				}
				return null;
			};
		}

		/**
		 * Provides compatability layer for DOM 2 TreeWalker where there is no native implementation.
		 *
		 * @function
		 * @alias module:wc/compat/TreeWalker
		 * @param {Node} root The root node of the tree to be walked.
		 * @param {number} whatToShow Bitmap of nodes to show in the tree walker.
		 * @param {Function} filter The filter function which determines the nodes in the tree which are acceptable.
		 * @param {Boolean} expandEntityReferences Not used: here for complete compatibility with some TreeWalkers.
		 * @returns {module:wc/compat/TreeWalker~TreeWalker}
		 */
		function createTreeWalker(root, whatToShow, filter, expandEntityReferences) {
			return new TreeWalker(root, whatToShow, filter, expandEntityReferences);
		}

		if (!has("dom-createtreewalker")) {
			document["createTreeWalker"] = createTreeWalker;
		}

		return createTreeWalker;
	});
})(this);
