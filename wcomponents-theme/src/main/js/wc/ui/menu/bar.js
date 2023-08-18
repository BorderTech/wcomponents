define(["wc/ui/menu/core",
	"wc/array/toArray",
	"wc/dom/event" ,
	"wc/dom/keyWalker",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/dom/initialise",
	"wc/dom/uid",
	"wc/i18n/i18n",
	"wc/timers",
	"wc/ui/ajax/processResponse",
	"wc/ui/viewportUtils",
	"wc/ui/menu/menuItem"],
function(abstractMenu, toArray, event, keyWalker, shed, Widget, initialise, uid, i18n, timers,
	processResponse, viewportUtils) {
	"use strict";
	let instance;

	const barTemplate = context => `
		<div id="${context.id}" role="presentation" class="wc-submenu ${context.class}"
			${context.tooltip ? `title=${context.tooltip}` : ""}>
			<button type="button" role="menuitem" aria-haspopup="true" class="wc-nobutton wc-submenu-o${context.opener.class}"
				${context.opener.tooltip ? `title=${context.opener.tooltip}` : ""}
				aria-controls="${context.contentId}">
			</button>
			<div id="${context.contentId}" class="wc_submenucontent wc-content" role="menu" aria-expanded="${context.open}">
				<button class="wc-menuitem wc_closesubmenu wc-nobutton wc-invite" role="menuitem" type="button">
					<span class="wc-decoratedlabel"><span class="fa fa-caret-left wc_dlbl_seg" aria-hidden="true"></span><span class="wc-labelbody wc_dlbl_seg">${context.closeText}</span></span>
				</button>
				${context.items}
			</div>
		</div>`;

	/* Unused dependencies:
	 * We will need "wc/ui/menu/menuItem" if we have any selectable items so we get it just in case rather than
	 * doing a convoluted XPath lookup in XSLT. */

	/**
	 * Extends menu functionality to provide a specific implementation of a menu bar.
	 * @constructor
	 * @alias module:wc/ui/menu/bar~Menubar
	 * @extends module:wc/ui/menu/core~AbstractMenu
	 * @private
	 */
	function Menubar() {
		const MENU_FIXED = "wc_menu_fix",
			BURGER_MENU_CLASS = "wc_menu_hbgr",
			hamburgerSelector = `div.${BURGER_MENU_CLASS}`,
			decoratedLabelSelector = ".wc-decoratedlabel";
		let resizeTimer,
			RESPONSIVE_MENU,
			SEPARATOR;

		/**
		 * The descriptors for this menu type.
		 * @var
		 * @type {module:wc/dom/Widget}
		 * @protected
		 * @override
		 */
		this._wd = {};

		/**
		 * The description of the ROOT node of a BAR or FLYOUT menu.
		 * @var
		 * @type {module:wc/dom/Widget}
		 * @public
		 * @override
		 */
		this.ROOT = new Widget("", "", {role: "menubar"});

		/**
		 * The role which is applied to the ROOT node of a BAR or FLYOUT menu.
		 * @var
		 * @type {String}
		 * @protected
		 * @override
		 **/
		this._role.MENU = "menubar";

		/**
		 * Determines if the current element/item is the first or last item in a menu/submenu.
		 *
		 * @function
		 * @private
		 * @param {Element} element The menu item/opener to test.
		 * @param {Element} root The root element of the current menu.
		 * @param {Boolean} [isLast] true if we want to know if the element is the last in the menu, otherwise we
		 *     want to know if it is first.
		 * @returns {Boolean} true if first/last item.
		 */
		function isFirstLastItem(element, root, isLast) {
			const direction = isLast ? keyWalker.MOVE_TO.NEXT : keyWalker.MOVE_TO.PREVIOUS;

			/* get the element which would be focussed if we were to use findFn without
			 * allowing cycling and forcing depthFirstNavigation false. If we don't get anything then
			 * the element passed in is the first/last*/
			const target = instance._getTargetItem(element, direction, root, false);
			if (target) {
				return element === target;  // if the target is the same as target then element is first &/or last
			}
			return true;
		}


		//		function openTopLevelSibling(element, next) {
		//			var branch = element,
		//				result, target;

		//			if (instance._isBranch(branch)) {
		//				result =  branch;
		//				branch = branch.parentNode;
		//			}

		//			while (branch) {
		//				if ((branch = instance._getBranch(branch))) {
		//					result =  branch;
		//					branch = branch.parentNode;
		//				}
		//			}

		//			if (result) {
		//				target = instance._getTargetItem(result, (next ? keyWalker.MOVE_TO.PREVIOUS : keyWalker.MOVE_TO.NEXT), instance.getRoot(result), true);
		//			}

		//			if(target) {
		//				instance[instance._FUNC_MAP.ACTION](target);
		//			}
		//		}

		//		this.openPreviousTopLevelSibling = function(element) {
		//			openTopLevelSibling(element);
		//		};

		//		this.openNextTopLevelSibling = function (element) {
		//			openTopLevelSibling(element, true);
		//		};

		/**
		 * Reset the key map according to the currently focused item. In the top level the  left and right go to
		 * siblings and down goes to children (if any) in sub-menus up and down go to siblings, right to child (if
		 * any) and left to parent.
		 *
		 * @function
		 * @protected
		 * @override
		 * @param {Element} item The item which has focus.
		 */
		this._remapKeys = function(item) {
			let _item = item;
			const VK_UP = "ArrowUp",
				VK_DOWN = "ArrowDown",
				VK_RIGHT = "ArrowRight",
				VK_LEFT = "ArrowLeft",
				root = this.getRoot(_item);

			if (!root) {
				return;
			}
			const submenu = this.getSubMenu(_item);
			if (submenu) {
				const branch = this._getBranch(submenu);
				let grandparent;
				if (branch && (grandparent = this.getSubMenu(branch))) {
					// more than one level deep.
					/* If a submenu left closes the current branch and right will
					 * trigger the action if the item is a branch or opener but
					 * otherwise do nothing. */
					this._keyMap[VK_LEFT] = this._FUNC_MAP.CLOSE_MY_BRANCH;
					this._keyMap[VK_UP] = keyWalker.MOVE_TO.PREVIOUS;
					this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.NEXT;
				} else {
					// this._keyMap[VK_LEFT] = "openPreviousTopLevelSibling";
					this._keyMap[VK_LEFT] = null;
				}

				if (this._isBranchOrOpener(_item)) {
					this._keyMap[VK_RIGHT] = this._FUNC_MAP.ACTION;
				} else {
					// this._keyMap[VK_RIGHT] = "openNextTopLevelSibling";
					this._keyMap[VK_RIGHT] = null;
				}
				/* Up and down is  a bit more convoluted.
				 * We need to know if we are in a sub menu immediately below the root
				 * menu. We can do this by getting the current branch and then
				 * testing if *it* has an ancestor branch, if it does we are nested.
				 */
				if (!grandparent) {
					// we are in a submenu under MENU
					if (this._isBranchOrOpener(_item)) {
						_item = this._getBranch(_item);
					}
					if (_item) {
						if (isFirstLastItem(_item, root)) {
							// If I am the first submenu item UP should go to the opener
							this._keyMap[VK_UP] = keyWalker.MOVE_TO.PARENT;
						} else {
							this._keyMap[VK_UP] = keyWalker.MOVE_TO.PREVIOUS;
						}
						// this is no an else if because an item can be both first and last
						if (isFirstLastItem(_item, root, true)) {
							// If I am the last submenu item then DOWN should go to the opener
							this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.PARENT;
						} else {
							this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.NEXT;
						}
					}
				}
			} else {
				this._keyMap[VK_LEFT] = keyWalker.MOVE_TO.PREVIOUS;
				this._keyMap[VK_RIGHT] = keyWalker.MOVE_TO.NEXT;
				// if I am a branch/opener and the menu is open then I have to cycle through my children
				if (this._isOpener(_item)) {
					_item = this._getBranch(_item);
				}
				if (this._isBranch(_item) && (_item = this._getBranchExpandableElement(_item))) {
					if (shed.isExpanded(_item)) {
						this._keyMap[VK_UP] = keyWalker.MOVE_TO.LAST_CHILD; // "lastChildItem";
						this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.CHILD;
					} else {
						this._keyMap[VK_UP] = this._FUNC_MAP.ACTION;
						this._keyMap[VK_DOWN] = this._FUNC_MAP.ACTION;
					}
				}
			}
		};

		/**
		 * Set the initial key map for a bar menu.
		 *
		 * @see http://www.w3.org/TR/wai-aria-practices/#menu
		 * @function
		 * @protected
		 * @override
		 */
		this._setupKeymap = function() {
			this._keyMap = {
				ArrowLeft: keyWalker.MOVE_TO.PREVIOUS,
				ArrowRight: keyWalker.MOVE_TO.NEXT,
				Home: keyWalker.MOVE_TO.FIRST,
				End: keyWalker.MOVE_TO.LAST,
				Escape: this._FUNC_MAP.ESCAPE,
				ArrowUp: this._FUNC_MAP.ACTION,
				ArrowDown: this._FUNC_MAP.ACTION
			};
		};

		/**
		 * Clear the iconified state of any menu.
		 *
		 * @function
		 * @private
		 * @param {Element} nextMenu the menu to manipulate
		 */
		function removeIconified(nextMenu) {
			if (!nextMenu.classList.contains(MENU_FIXED)) {
				return;
			}

			try {
				const burger = nextMenu.querySelector(hamburgerSelector);
				if (!burger) {
					return;
				}

				const submenuContent = instance._wd.submenu.findDescendant(burger);
				if (!submenuContent) {
					return;
				}

				let current;
				while ((current = submenuContent.firstChild)) {
					if (current.classList.contains("wc_closesubmenu")) {
						submenuContent.removeChild(current);
					} else {
						nextMenu.appendChild(current);
					}
				}
				burger.parentNode.removeChild(burger);

			} finally {
				nextMenu.classList.remove(MENU_FIXED);
			}
		}

		/**
		 * Attach a close button to a submenu if required.
		 *
		 * @function
		 * @private
		 * @param {Element} el the element to test and (possibly) manipulate.
		 */
		function attachSubMenuCloseButton(el) {
			if (el && instance.isSubMenu(el)) {
				const branch = instance._getBranch(el);
				let opener;
				if (branch && (opener = instance._getBranchOpener(branch))) {
					let label = opener.querySelector(decoratedLabelSelector);

					if (label) {
						const tempWrapper = document.createElement("span");
						const closeButtonHTML = `<button class="wc-menuitem wc_closesubmenu wc-nobutton wc-invite" role="menuitem" type="button">${label.outerHTML}</button>`;
						tempWrapper.insertAdjacentHTML("afterbegin", closeButtonHTML);
						const closeButton = tempWrapper.firstChild;
						label = closeButton.querySelector(decoratedLabelSelector);
						if (label) {
							label.insertAdjacentHTML("afterbegin", '<i class="fa fa-caret-left wc_dlbl_seg" aria-hidden="true"></i>');
						}
						Array.prototype.forEach.call(closeButton.querySelectorAll("[id]"), function(next) {
							next.id = uid();
						});
						if (el.hasChildNodes()) {
							el.insertBefore(closeButton, el.firstChild);
						}
					}
				}
			}
		}

		/**
		 * Array iterator function for {@link module:wc/ui/menu/bar~toggleIconMenus} which processes each
		 * menu found and manipulates it for improved display and usability on mobile devices. Each sub-menu has a
		 * close button added to the top and when the BAR menu is in the HEADER panel (role "banner") we collapse
		 * the entire menu into a sub-menu and add a launcher to where the top-level items used to be.
		 *
		 * @function
		 * @private
		 * @param {Element} nextMenu The menu to be processed.
		 */
		function makeIconified(nextMenu) {
			if (nextMenu.classList.contains(MENU_FIXED)) {
				return;
			}
			i18n.translate(["menu_open_label", "menu_close_label"]).then(function(strings) {
				const props = {
					id: uid(),
					class: BURGER_MENU_CLASS,
					opener: {
						class: "wc_hbgr fa fa-bars",
						tooltip: strings[0]
					},
					contentId: uid(),
					open: false,
					closeText: strings[1],
					items: nextMenu.innerHTML
				};
				nextMenu.innerHTML = barTemplate(props);
				nextMenu.classList.add(MENU_FIXED);
			});
		}

		/**
		 * Determine if the iconification of any menus has to be toggled and call the appropriate manipulation
		 * function if required.
		 *
		 * @function
		 * @private
		 * @param {Element} el the element to test which may be a menu, submenu or something containing a menu.
		 */
		function toggleIconMenus(el) {
			let candidates;
			const element = el || document.body;
			if (instance.isSubMenu(element)) {
				return;
			}
			RESPONSIVE_MENU = RESPONSIVE_MENU || instance.ROOT.extend(["wc-respond"]);

			if (RESPONSIVE_MENU.isOneOfMe(element)) {
				candidates = [element];
			} else {
				candidates = toArray(RESPONSIVE_MENU.findDescendants(element));
			}

			if (!candidates.length) {
				return;
			}

			candidates = candidates.filter(function(next) {
				return next.childNodes.length > 1;
			});

			if (candidates.length) {
				if (viewportUtils.isPhoneLike()) {
					candidates.forEach(makeIconified);
				} else {
					candidates.forEach(removeIconified);
				}
			}
		}

		/**
		 * Resize event sets up a timer to undertake menu manipulation.
		 * @function
		 * @private
		 */
		function resizeEvent(/* $event */) {
			if (resizeTimer) {
				timers.clearTimeout(resizeTimer);
			}
			resizeTimer = timers.setTimeout(toggleIconMenus, 100);
		}

		/**
		 * Set the orientation on vertical separators.
		 *
		 * @function
		 * @private
		 * @param {HTMLElement} element any element which may contain a bar/flyout menu separators
		 */
		function setSeparatorOrientation(element) {
			if (!SEPARATOR) {
				SEPARATOR =  new Widget("hr");
				SEPARATOR.descendFrom(instance.ROOT, true);
			}
			Array.prototype.forEach.call(SEPARATOR.findDescendants(element), function(next) {
				next.setAttribute("aria-orientation", "vertical");
			});
		}

		/**
		 * Pre-insertion ajax subscriber function to set the orientation of vertical separators in an ajax response.
		 *
		 * @function
		 * @private
		 * @param {Element} element the target element, not used
		 * @param {DocumentFragment} documentFragment the DocumentFragment to be inserted
		 */
		function ajaxSubscriber(element, documentFragment) {
			setSeparatorOrientation(documentFragment);
		}

		function attachClosebuttons(container) {
			const el = container || document.body;
			if (container && instance.isSubMenu(container)) {
				attachSubMenuCloseButton(container);
			}
			Array.prototype.forEach.call(instance._wd.submenu.findDescendants(el), attachSubMenuCloseButton);
		}

		/**
		 * Extended initialisation for bar/flyout menus. Should not be called manually.
		 *
		 * @function
		 * @public
		 * @param {Element} element the element being initialised
		 */
		this.initialise = function(element) {
			this.constructor.prototype.initialise.call(this, element);
			attachClosebuttons();
			toggleIconMenus(element);
			setSeparatorOrientation(element);
			processResponse.subscribe(ajaxSubscriber);
			processResponse.subscribe(attachClosebuttons, true);
			processResponse.subscribe(toggleIconMenus, true);
			event.add(window, "resize", resizeEvent, 1);
		};
	}

	Menubar.prototype = abstractMenu;

	/**
	 * Menu controller extension for WMenu of type BAR and type FLYOUT. These are menus which are horizontal at the top
	 * level and if they have submenus they are transient fly-out artifacts.
	 *
	 * @see http://www.w3.org/TR/wai-aria-practices/#menu
	 *
	 * @module
	 * @extends module:wc/ui/menu/core
	 *
	 * @requires module:wc/ui/menu/core
	 * @requires module:wc/array/toArray
	 * @requires module:wc/dom/event
	 * @requires module:wc/dom/keyWalker
	 * @requires module:wc/dom/shed
	 * @requires module:wc/dom/Widget
	 * @requires module:wc/dom/initialise
	 * @requires module:wc/dom/uid
	 * @requires module:wc/i18n/i18n
	 * @requires module:wc/timers
	 * @requires module:wc/ui/ajax/processResponse
	 * @requires module:wc/ui/viewportUtils
	 */
	instance = new Menubar();
	instance.constructor = Menubar;
	return initialise.register(instance);
});
