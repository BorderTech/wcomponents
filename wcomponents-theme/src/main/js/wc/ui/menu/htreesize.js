define(["wc/dom/event",
	"wc/dom/Widget",
	"wc/dom/initialise",
	"wc/ui/menu/tree",
	"wc/ui/resizeable",
	"wc/dom/storage",
	"wc/ui/ajax/processResponse",
	"wc/dom/attribute"],
	function(event, Widget, initialise, tree, resizeable, storage, processResponse, attribute) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/menu/htreesize~TreeSizer
		 * @private
		 */
		function TreeSizer() {
			var HANDLE = new Widget("", "wc_branch_resize_handle"),
				resized = {},
				STORE_KEY = "wc-treesize";

			/**
			 * When a tree component is resized store the preferred size for later use.  A subscriber for
			 * {@link module:wc/ui/resizeable}.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element which was resized.
			 */
			function resizeSubscriber(element) {
				var obj, id, store;
				if (element) {
					id = element.id;

					if (element.style.width || element.style.height) {
						obj = { width: element.style.width, height: element.style.height} ;
						resized[id] = obj;
						store = true;
					}
					else if (resized[id]) {
						delete resized[id];
						store = true;
					}

					if (store) {
						storage.put(STORE_KEY, JSON.stringify(resized));
					}
				}
			}

			/**
			 * Set height and width on segments of a htree if these were stored previously.
			 *
			 * @function
			 * @private
			 * @param {String} id The id of the component to be sized.
			 * @param {module:wc/ui/menu/htreesize~dto} obj A dto containing height and/or width.
			 */
			function doReapplySize(id, obj) {
				var element = document.getElementById(id),
					width, height;
				if (element && tree.isHTree(tree.getRoot(element))) {

					width = obj.width;
					height = obj.height;

					if (width) {
						element.style.width = width;
					}
					if (height) {
						element.style.height = height;
					}

					if (width || height) {
						resized[id] = { width: width, height: height };
					}
				}
			}

			/**
			 * An iterator function to set sizes on groups in a htree.
			 * @function
			 * @private
			 * @param {Element} element The tree or group being adjusted.
			 * @param {Object} stored The object version of the stored size info.
			 */
			function reapplySizeOnTreeGroup(element, stored) {
				var id = element.id, obj;

				if (stored && (obj = stored[id])) {
					doReapplySize(id, obj);
				}

			}

			/**
			 * Apply user's previously stored sizes if htree or group is inserted via ajax.
			 * @function
			 * @private
			 * @param {Element} element The ajax target.
			 */
			function ajaxSubscriber(element) {
				var stored = storage.get(STORE_KEY);
				if (stored) {
					stored = JSON.parse(stored);

					if ((tree.isSubMenu(element) && tree.isHTree(tree.getRoot(element)))) {
						reapplySizeOnTreeGroup(element, stored);
					}

					Array.prototype.forEach.call(tree._wd.submenu.findDescendants(element), function(next) {
						if (tree.isHTree(tree.getRoot(next))) {
							reapplySizeOnTreeGroup(next, stored);
						}
					});
				}
			}

			/**
			 * Reset size on doubleclick on the handle.
			 * @function
			 * @private
			 * @param {Event} $event A wrapped dblclick event.
			 */
			function dblClickEvent($event) {
				var target = $event.target, element;
				if (!$event.defaultPrevented && (element = HANDLE.findAncestor(target))) {
					if (resizeable.clearSize(element)) {
						$event.preventDefault();
					}
				}
			}

			function keydownEvent($event) {
				var target = $event.target;

				if (!$event.defaultPrevented && $event.keyCode === KeyEvent.DOM_VK_RETURN) {
					if (resizeable.clearSize(target)) {
						$event.preventDefault();
					}
				}
			}

			function focusEvent($event) {
				var target = $event.target, BS = "htreesize.inited";
				if (HANDLE.isOneOfMe(target) && !attribute.get(target, BS)) {
					event.add(target, event.TYPE.keydown, keydownEvent);
					attribute.set(target, BS, true);
				}
			}

			/**
			 * initialise htrees by setting any stored sizes and registering subscribers.
			 *
			 * @function module:wc/ui/menu/htreesize.initialise
			 * @public
			 * @param {Element} element The element being initialised.
			 */
			this.initialise = function(element) {
				var stored, o;

				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}

				event.add(element, event.TYPE.dblclick, dblClickEvent);
				processResponse.subscribe(ajaxSubscriber, true);
				resizeable.subscribe(resizeSubscriber);

				if ((stored = storage.get(STORE_KEY))) {
					stored = JSON.parse(stored);
					for (o in stored) {
						doReapplySize(o, stored[o]);
					}
				}
			};
		}

		var /** @alias module:wc/ui/menu/htreesize */ instance = new TreeSizer();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/menu/htreesize~dto An object which stores information about a htree or a htree
		 * submenu.
		 * @property {String} [height] The CSS height of the item including units.
		 * @property {String} [width] The CSS width of the item including units.
		 */
	});
