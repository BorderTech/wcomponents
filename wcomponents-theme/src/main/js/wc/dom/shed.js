/**
 * Module for managing some custom faux-events: showing and hiding; enabling and disabling; selecting and deselecting;
 * expanding and collapsing; and making mandatory or optional.
 *
 * <p><strong>S</strong>how<br />
 * <strong>H</strong>ide<br />
 * <strong>E</strong>nable<br />
 * <strong>D</strong>isable<br />
 * 	and now:<br />
 * <strong>D</strong>eselect<br />
 * <strong>S</strong>elect<br />
 * <strong>E</strong>xpand<br />
 * <strong>C</strong>ollpase<br />
 * <strong>M</strong>andate<br />
 * <strong>O</strong>ptional<br />
 * <strong>R</strong>ead only (currently only as a test isReadOnly, not as a set/unset)</p>
 *
 * <p>Encapsulates factors such as:</p>
 * <ul><li>what css class/es to use to show and hide DOM elements</li>
 * <li>what attributes / properties to use to: disable DOM elements; select DOM elements</li>
 * <li>what additional attributes to set (such as aria attributes)</li>
 * <li>publishing the fact that a DOM element has been shown/hidden, de/deselected, and/or
 * enabled/disabled.</li></ul>
 *
 * @module
 * @requires module:wc/Observer
 * @requires module:wc/dom/aria
 * @requires module:wc/dom/impliedARIA
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/disabledLink
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/getLabelsForElement
 * @requires module:wc/dom/role
 *
 * @todo re-order code, document private methods.
 */
define(["wc/Observer",
		"wc/dom/aria",
		"wc/dom/impliedARIA",
		"wc/dom/classList",
		"wc/dom/disabledLink",
		"wc/dom/tag",
		"wc/dom/Widget",
		"wc/dom/getLabelsForElement",
		"wc/dom/role"],
	/** @param Observer wc/Observer @param aria wc/dom/aria @param impliedAria wc/dom/impliedARIA @param classList wc/dom/classList @param disabledLink wc/dom/disabledLink @param tag wc/dom/tag @param Widget wc/dom/Widget @param getLabelsForElement wc/dom/getLabelsForElement @param $role wc/dom/role @ignore */
	function(Observer, aria, impliedAria, classList, disabledLink, tag, Widget, getLabelsForElement, $role) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/shed~Shed
		 * @private
		 */
		function Shed() {
			var observer,
				/** @var {module:wc/dom/shed~actions} @private */
				actions = {
					SHOW: "show",
					HIDE: "hide",
					ENABLE: "enable",
					DISABLE: "disable",
					DESELECT: "deselect",
					SELECT: "select",
					MIX: "mix",
					EXPAND: "expand",
					COLLAPSE: "collapse",
					MANDATORY: "mandatory",
					OPTIONAL: "optional"},
				ARIA_STATE = {"expanded": "aria-expanded",
							"readonly": "aria-readonly"},
				NATIVE_STATE = {},
				ANY_SEL_STATE = "${wc.common.helper.anySelectedState}",
				SELECT_WD,
				DISABLED = "disabled",
				HIDDEN = "hidden",
				REQUIRED = "required",
				CHECKED = "checked",
				SELECTED = "selected",
				CLASS_REQUIRED = "wc_req",
				OPEN = "open",
				useChildren;

			ARIA_STATE[DISABLED] = "aria-disabled";
			ARIA_STATE[REQUIRED] = "aria-required";
			ARIA_STATE[SELECTED] = ["aria-selected",
									"aria-checked",
									"aria-pressed"];
			NATIVE_STATE[REQUIRED] = "required";
			NATIVE_STATE[DISABLED] = "disabled";


			function disabledMandatoryHelper(element, STATE, reverse) {
				var _nativeState = NATIVE_STATE[STATE],
					_ariaState = ARIA_STATE[STATE],
					nativeSupported = impliedAria.supportsNativeState(element, STATE),
					role = $role.get(element, true),
					supported,
					ariaSupported,
					func;

				if (role) {
					supported = aria.getSupported(role);
					ariaSupported = (supported && supported[_ariaState]);
				}

				if (ariaSupported || nativeSupported) {
					if (reverse) {
						element.removeAttribute(_nativeState);
						element.removeAttribute(_ariaState);
					}
					else if (nativeSupported) {
						element.setAttribute(_nativeState, _nativeState);
					}
					else if (ariaSupported) {
						element.setAttribute(_ariaState, "true");
					}
					if (STATE === DISABLED) {
						/*
						* READ THIS CAREFULLY
						* when we disable an element using aria-disabled we have to set an explicit
						* tabIndex of -1. The ONLY other explicit tabIndices we ever set is 0 for
						* elements which are not natively focusable. So what we are going to do here
						* is the following:
						* * IF !reverse set it to -1
						* * else
						* 	* If element has a role set tabIndex to 0
						* 	* otherwise remove the tabIndex
						* This will result in the possibility of a natively focusable element getting an
						* explicit tabIndex of 0 but this has no real impact and is quicker than attempting
						* to work out if the element is natively focusable
						* Note: Assumes nobody else is mucking around with tabIndex
						*/
						if (element.hasAttribute("tabIndex")) {
							if (!reverse) {
								element.tabIndex = -1;
							}
							else if ($role.get(element)) {
								element.tabIndex = 0;
							}
							else {
								element.removeAttribute("tabIndex");
							}
						}
					}
				}
				/* Special case for FIELDSETS (again) if they are being made mandatory/optional because they support
				 * neither required nor aria-required.
				 *
				 * NOTE: do the native/aria stuff first because the fieldset may have a role. */
				else if (STATE === REQUIRED && element.tagName === tag.FIELDSET) {
					func = reverse ? "remove" : "add";
					classList[func](element, CLASS_REQUIRED);
				}
				else {
					applyStateToChildren(element, STATE, reverse);
				}
			}

			/*
			 * Helper for disabledMandatoryHelper.
			 * @private
			 * @function
			 */
			function applyStateToChildren(element, STATE, reverse) {
				var kids, i, len, func;
				// cannot set state on the target but may be able to set it on its children. So we go into child tree until we find something to which we can apply the STATE change.
				if (useChildren || (useChildren !== false && (useChildren = !!element.children))) {
					kids = element.children;  // FF 3.5, Safari and IE
				}
				else {
					kids = element.childNodes;
				}
				if (kids && kids.length) {
					if (STATE === REQUIRED) {
						func = reverse ? actions.OPTIONAL : actions.MANDATORY;
					}
					else {
						func = reverse ? actions.ENABLE : actions.DISABLE;
					}
				}

				for (i = 0, len = kids.length; i < len; i++) {
					if (useChildren || kids[i].nodeType === Node.ELEMENT_NODE) {
						// don't try disabling text nodes, comments etc
						instance[func](kids[i]);
					}
				}
			}

			/*
			 * helper for determining if an element show be expanded using the OPEN attribute
			 * rather than the HIDDEN attribute
			 */
			function expandWithOpen(element) {
				return element.tagName === tag.DETAILS;
			}

			/**
			 * Allows you to query and or set the native "selected state" of a DOM element.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to be manipulated.
			 * @param {boolean} [value] true if the item is to be selected, explicitly false if it is to be deselcted if
			 *    neither true nor false then the selected state will not be modified at all
			 * @param {boolean} [mix] If true then the checkbox will be set to "indeterminate". Only relevant when all
			 * the following are true:
			 *
			 *  * element is a checkbox (or has checkbox role); and
			 *  * value is false.
			 *
			 * @returns {?(Boolean|int)} A property of {@link module:wc/dom/shed.state} or null if it does not
			 *     natively support a selected state. Note that that mixed (indeterminate) and checked is ignored.
			 */
			function getSetNativeSelected(element, value, mix) {
				var result = false, attribute, selectElement;

				if (impliedAria.supportsNativeState(element, CHECKED)) {
					attribute = CHECKED;
				}
				else if (impliedAria.supportsNativeState(element, SELECTED)) {
					attribute = SELECTED;
				}
				if (attribute) {
					if (value === true) {
						element.setAttribute(attribute, attribute);
						element[attribute] = true;
					}
					else if (value === false) {
						element[attribute] = false;
						element.removeAttribute(attribute);
						if (attribute === CHECKED) {
							element.indeterminate = !!mix;
						}
						/*
						 * This is for a well known browser which takes an interesting direction when
						 * attempting to unset the checked attribute. SO DON'T REMOVE IT!
						 */
						if (element[attribute]) {
							if (attribute === SELECTED) {
								// this is the case in IE when in a single select (NOT in a multi)
								SELECT_WD = SELECT_WD || new Widget("select");
								selectElement = SELECT_WD.findAncestor(element);
								selectElement.selectedIndex = 0;  // this is the default in other browsers
							}
							else if (attribute === CHECKED) {  // this appears to be fixed in IE8 so I moved it to the second test
								element.checked = false;
								element[attribute] = false;
								// delete element[attribute];  // don't do this, it breaks webkit
							}
						}
					}
					if (element.indeterminate) {
						result = instance.state.MIXED;
					}
					else {
						result = element[attribute] ? true : false;
					}
				}
				return result;
			}

			function hasAncestorInState(node, state, stopAt) {
				var result = false,
					parent = node,
					_stopAt = stopAt || document.body;
				while ((parent = parent.parentNode) && parent.nodeType === Node.ELEMENT_NODE && parent.tagName.toUpperCase() !== _stopAt.tagName.toUpperCase()) {
					if (instance[state](parent)) {
						result = true;
						break;
					}
				}
				return result;
			}

			/**
			 * Sets or clears an attribute on an element.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element on which to set or clear the attribute.
			 * @param {string} attribute The name of the attribute to set or clear.
			 * @param {Object} value The value to set or any falsey value except false or zero to remove the attribute.
			 * If the value is not a string .toString will be called on it when the attribute is being set.
			 */
			function setMyAttribute(element, attribute, value) {
				if (element) {
					if (value || value === false || value === 0) {
						element.setAttribute(attribute, value.toString());
					}
					else {
						element.removeAttribute(attribute);
					}
				}
			}

			/*
			 * helper for determining if an element show be shown using the OPEN attribute
			 * rather than the HIDDEN attribute
			 */
			function showWithOpen(element) {
				return element.tagName === tag.DIALOG;
			}

			/**
			 * Helper to set various states.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element which we will act on.
			 * @param {String} attribute The attribute to set on the element.
			 * @param {Variant} [action] The value of the attribute to set.
			 */
			function shedHelper(element, attribute, action) {
				var labels, i;

				if (showWithOpen(element)) {
					attribute = OPEN;
					action = action ? null : OPEN;
				}
				setMyAttribute(element, attribute, action);
				if (element.form) {
					labels = getLabelsForElement(element, true);
					if ((i = labels.length)) {
						while (i--) {
							setMyAttribute(labels[i], attribute, action);
						}
					}
				}
			}

			/**
			 * <p>Manages the "select", "deselect" and "mix" methods.</p>
			 * <p><strong>NOTE:</strong> The "mixed" (or "indeterminate") state only applies to checkboxes. A checkbox
			 * in "mixed" state actually means different things for native checkboxes and aria-checkboxes:</p>
			 * <ul>
			 *    <li>Aria-checkboxes have one mixed state: "mixed" which is equivalent to "unchecked" for all intents and
			 *        purposes.</li>
			 *    <li>Native checkboxes have two mixed states: "mixed + checked" and "mixed + unchecked". In this case mixed
			 *    does not tell us anything about the state of the checkbox as it will be reported to the server. For
			 *    the purpose of this class we ignore "mixed + checked", this is simply interpreted as "checked".</li></ul>
			 *
			 * @function
			 * @private
			 * @param {(Boolean|int)} action A property of {@link module:wc/dom/shed.state} SELECTED, DESELECTED or MIXED.
			 * @param {Element} element The element to which we apply the state.
			 */
			function selectHelper(action, element) {
				var preferred, i, len,
					supported,
					role = $role.get(element, true),
					mixed = (action === instance.state.MIXED);

				if (role && !(impliedAria.supportsNativeState(element, ANY_SEL_STATE))) {
					supported = aria.getSupported(role);
					// If there is a required attribute use that one
					preferred = ARIA_STATE[SELECTED].filter(function(attr) {
						return supported[attr] === aria.REQUIRED;
					});
					if (!preferred.length) {
						// If there is a supported attribute which is SET then use that one
						preferred = ARIA_STATE[SELECTED].filter(function(attr) {
							return (supported[attr] === aria.SUPPORTED && element.getAttribute(attr) !== null);
						});
						if (!preferred.length) {
							// Otherwise get a list of supported "select" attributes
							preferred = ARIA_STATE[SELECTED].filter(function(attr) {
								return supported[attr] === aria.SUPPORTED;
							});
						}
					}
					len = Math.min(preferred.length, 1);  // we only want to use the first attribute we found above
					for (i = 0; i < len; i++) {
						shedHelper(element, preferred[i], mixed ? "mixed" : action);
					}
				}
				else {
					getSetNativeSelected(element, !!action, mixed);
				}
			}

			/**
			 * Tests if this element has an attribute which matches the provided value.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to test.
			 * @param {string} attribute The name of the attribute to check.
			 * @param {Object} value The value to check.
			 * @returns {(null|boolean)} true if the element has the attribute and the value matches;
			 *    false if the element has the attribute and the value does not match; or
			 *    null if the element does not have the attribute at all
			 */
			function isThisMyAttribute(element, attribute, value) {
				var result = element.getAttribute(attribute);
				if (result !== null) {
					result = (value.toString() === result);
				}
				return result;
			}


			/** @var {module:wc/dom/shed~actions} module:wc/dom/shed.actions An object map used to indicate the various events managed and published by shed. */
			this.actions = actions;

			/**
			 * Show an element in the UI.
			 *
			 * @function module:wc/dom/shed.show
			 * @param {Element} element The element to show.
			 * @param {Boolean} [quiet] If true then do not publish this show event.
			 */
			this[actions.SHOW] = function (element, quiet) {
				shedHelper(element, HIDDEN, null);
				if (!quiet) {
					instance.publish(element, actions.SHOW);
				}
			};

			/**
			 * Hide an element in the UI.
			 *
			 * @function module:wc/dom/shed.hide
			 * @param {Element} element The element to hide.
			 * @param {Boolean} [quiet] If true then do not publish this hide event.
			 */
			this[actions.HIDE] = function (element, quiet) {
				shedHelper(element, HIDDEN, HIDDEN);
				if (!quiet) {
					instance.publish(element, actions.HIDE);
				}
			};

			/**
			 * Enable an element in the UI.
			 *
			 * @function module:wc/dom/shed.enable
			 * @param {Element} element The element to enable.
			 * @param {Boolean} [quiet] If true then do not publish this enable event.
			 */
			this[actions.ENABLE] = function (element, quiet) {
				disabledMandatoryHelper(element, NATIVE_STATE[DISABLED], true);
				if (!quiet) {
					instance.publish(element, actions.ENABLE);
				}
			};

			/**
			 * Attempt to disable an element using either its native disabled attribute if this is supported or, if it
			 * has a role (or implied role) the aria-disabled attribute. If disabled is able to be set then we
			 * optionally publish this.
			 *
			 * @function module:wc/dom/shed.disable
			 * @param {Element} element The element to disable.
			 * @param {Boolean} [quiet] If true then do not publish this disable event.
			 */
			this[actions.DISABLE] = function (element, quiet) {
				disabledMandatoryHelper(element, NATIVE_STATE[DISABLED], false);
				if (!quiet) {
					instance.publish(element, actions.DISABLE);
				}
			};

			/**
			 * Deselect a selectable element in the UI. This is generally done on WAI-ARIA roled widgets rather than
			 * natively selectable elements but that is not a requirement.
			 *
			 * @function module:wc/dom/shed.deselect
			 * @param {Element} element The element to deselect.
			 * @param {Boolean} [quiet] If true then do not publish this deselect event.
			 */
			this[actions.DESELECT] = function (element, quiet) {
				selectHelper(instance.state.DESELECTED, element);
				if (!quiet) {
					instance.publish(element, actions.DESELECT);
				}
			};

			/**
			 * Select a selectable element in the UI. This is generally done on WAI-ARIA roled widgets rather than
			 * natively selectable elements but that is not a requirement.
			 *
			 * @function module:wc/dom/shed.select
			 * @param {Element} element The element to select.
			 * @param {Boolean} [quiet] If true then do not publish this select event.
			 */
			this[actions.SELECT] = function (element, quiet) {
				selectHelper(instance.state.SELECTED, element);
				if (!quiet) {
					instance.publish(element, actions.SELECT);
				}
			};

			/**
			 * Set a selectable element's selected state to indeterminate.
			 *
			 * @function module:wc/dom/shed.mix
			 * @param {Element} element The element to set to indeterminate.
			 * @param {Boolean} [quiet] If true then do not publish this event.
			 */
			this[actions.MIX] = function (element) {
				selectHelper(instance.state.MIXED, element);
				instance.publish(element, actions.MIX);
			};

			/**
			 * Expand an element in the UI.
			 *
			 * @function module:wc/dom/shed.expand
			 * @param {Element} element The element to expand.
			 * @param {Boolean} [quiet] If true then do not publish this event.
			 */
			this[actions.EXPAND] = function (element, quiet) {
				if (expandWithOpen(element)) {
					setMyAttribute(element, OPEN, OPEN);
				}
				else {
					setMyAttribute(element, ARIA_STATE.expanded, true);
				}
				if (!quiet) {
					instance.publish(element, actions.EXPAND);
				}
			};

			/**
			 * Collapse an element in the UI.
			 *
			 * @function module:wc/dom/shed.collapse
			 * @param {Element} element The element to collapse.
			 * @param {Boolean} [quiet] If true then do not publish this event.
			 */
			this[actions.COLLAPSE] = function (element, quiet) {
				if (expandWithOpen(element)) {
					setMyAttribute(element, OPEN, null);
				}
				else {
					setMyAttribute(element, ARIA_STATE.expanded, false);
				}
				if (!quiet) {
					instance.publish(element, actions.COLLAPSE);
				}
			};

			/**
			 * Set an element to be mandatory.
			 *
			 * @function module:wc/dom/shed.mandatory
			 * @param {Element} element The element to make mandatory.
			 * @param {Boolean} [quiet] If true then do not publish this event.
			 */
			this[actions.MANDATORY] = function (element, quiet) {
				disabledMandatoryHelper(element, REQUIRED, false);
				if (!quiet) {
					instance.publish(element, actions.MANDATORY);
				}
			};

			/**
			 * Set an element to be optional.
			 *
			 * @function module:wc/dom/shed.optional
			 * @param {Element} element The element to make optional.
			 * @param {Boolean} [quiet] If true then do not publish this event.
			 */
			this[actions.OPTIONAL] = function (element, quiet) {
				disabledMandatoryHelper(element, REQUIRED, true);
				if (!quiet) {
					instance.publish(element, actions.OPTIONAL);
				}
			};

			/**
			 * Determine if an element has an ancestor which is disabled.
			 *
			 * @function module:wc/dom/shed.hasDisabledAncestor
			 * @param {Element} node The element to test
			 * @param {String} [stopAt] a tag name which defines where we stop looking. If not defined we stop at BODY.
			 * @returns {Boolean} true if the element has a disabled ancestor.
			 */
			this.hasDisabledAncestor = function(node, stopAt) {
				return hasAncestorInState(node, "isDisabled", stopAt);
			};

			/**
			 * Determine if an element has an ancestor which is hidden. NOTE: now we can rely on an element not having
			 * dimension if it is inside a hidden container we can probably do away with this.
			 *
			 * @function module:wc/dom/shed.hasHiddenAncestor
			 * @param {Element} node The element to test
			 * @param {String} [stopAt] a tag name which defines where we stop looking. If not defined we stop at BODY.
			 * @returns {Boolean} true if the element has a hidden ancestor.
			 */
			this.hasHiddenAncestor = function(node, stopAt) {
				return hasAncestorInState(node, "isHidden", stopAt);
			};

			/**
			 * Determine if the element is disabled.
			 *
			 * @function module:wc/dom/shed.isDisabled
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is disabled.
			 */
			this.isDisabled = function (element) {
				return element.hasAttribute(DISABLED) || element.getAttribute(ARIA_STATE[DISABLED]) === "true";
			};

			/**
			 * Determine if the element is in an expanded state.
			 *
			 * @function module:wc/dom/shed.isExpanded
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is expanded.
			 */
			this.isExpanded = function (element) {
				var result = false;
				if (expandWithOpen(element)) {
					result = element.hasAttribute(OPEN);
				}
				else {
					result = !!isThisMyAttribute(element, ARIA_STATE.expanded, true);
				}
				return result;
			};

			/**
			 * Determine if the element is hidden in accordance with the way shed hides things.
			 *
			 * @function module:wc/dom/shed.isHidden
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is hidden.
			 */
			this.isHidden = function (element) {
				var result = false;
				if (showWithOpen(element)) {
					result = !element.hasAttribute(OPEN);
				}
				else {
					result = !!isThisMyAttribute(element, HIDDEN, HIDDEN);
				}
				return result;
			};

			/**
			 * Determine if the element is marked as required.
			 *
			 * @function module:wc/dom/shed.isMandatory
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is required.
			 */
			this.isMandatory = function (element) {
				var result = false;

				if (element.getAttribute(NATIVE_STATE[REQUIRED]) || element.getAttribute(ARIA_STATE[REQUIRED]) === "true" ||
					(element.tagName === tag.FIELDSET && classList.contains(element, CLASS_REQUIRED))) {
					result = true;
				}
				return result;
			};

			/**
			 * Determine if the element is in a "read only" state.
			 *
			 * @function module:wc/dom/shed.isReadOnly
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is read only.
			 */
			this.isReadOnly = function (element) {
				return element.readOnly || !!isThisMyAttribute(element, ARIA_STATE.readonly, true);
			};

			/**
			 * Determine if an element supports selection either natively or via aria role.
			 * For example a radio button returns true, a text input returns false.
			 *
			 * @function module:wc/dom/shed.isSelectable
			 * @param {Element} element The element to test.
			 * @returns {boolean} true if the element is selectable.
			 */
			this.isSelectable = function (element) {
				var result = false, role, supported;
				if (impliedAria.supportsNativeState(element, ANY_SEL_STATE)) {
					result = true;
				}
				else {
					role = $role.get(element, true);
					if ((supported = aria.getSupported(role))) {
						supported = ARIA_STATE[SELECTED].filter(function(attr) {
							return (supported[attr] === aria.SUPPORTED || supported[attr] === aria.REQUIRED);
						});
						result = !!supported.length;
					}
				}
				return result;
			};

			/**
			 * Determine if an element is current selected. Selected means either:
			 *
			 * * the element has an aria role which supports any of the states in ARIA_STATE[SELECTED] and the correct
			 *   state attribute for that role is set to true.; OR
			 * * the element does not have an aria role which supports any of the states in ARIA_STATE[SELECTED]
			 *   but the element does have native support for a "selectable" attribute and that attribute is set
			 *
			 * @function module:wc/dom/shed.isSelected
			 * @param {Element} element The element to test.
			 * @returns {(Boolean|int)} A property of {@link module:wc/dom/shed.state} being:
			 *
			 *    * SELECTED (which equates to true) if this element is selected; or
			 *    * MIXED (which equates to false) if mixed; otherwise
			 *    * DESELECTED (which equates to false).
			 */
			this.isSelected = function (element) {
				var i,
					next,
					result = false,
					nextResult,
					level,
					supported,
					role = $role.get(element, true);
				if (role && !(impliedAria.supportsNativeState(element, ANY_SEL_STATE))) {
					supported = aria.getSupported(role);
					for (i = (ARIA_STATE[SELECTED].length - 1); i >= 0; i--) {
						next = ARIA_STATE[SELECTED][i];
						level = supported[next];
						if (level) {
							nextResult = isThisMyAttribute(element, next, true);
							if (nextResult !== null) {
								if (nextResult) {
									result = instance.state.SELECTED;
								}
								else if (isThisMyAttribute(element, next, "mixed")) {
									result = instance.state.MIXED;
								}
								else {
									result = instance.state.DESELECTED;
								}
								break;  // take the first one we find - there should not be more than one
							}
							else if (level === aria.REQUIRED) {
								throw new TypeError("Required ARIA attribute not found! " + next);
							}
						}
					}
				}
				else {
					result = getSetNativeSelected(element);
				}
				return result;
			};

			/**
			 * Notify all subscribers that an action was performed. Action will be the name of the public method called
			 * on this class.
			 *
			 * @function module:wc/dom/shed.publish
			 * @param {Element} element The element to test.
			 * @param {string} action One of {@link module:wc/dom/shed~actions}, e.g. "show" or "hide".
			 */
			this.publish = function(element, action) {
				if (observer) {
					observer.setFilter(action);
					observer.notify(element, action);
				}
			};

			/**
			 * Holds a map for indicating/getting the selected state of a component.
			 * @constant module:wc/dom/shed.state
			 * @type {Object}
			 * @property {Boolean} SELECTED  true,
			 * @property {Boolean} DESELECTED false,
			 * @property {int} MIXED: 0
			 */
			this.state = {
				SELECTED: true,
				DESELECTED: false,
				MIXED: 0
			};

			/**
			 * Be notified of an element being shown or hidden.
			 *
			 * @function module:wc/dom/shed.subscribe
			 * @param {string} type The action you want to be notified about (one of shed.actions)
			 * @param {Function} subscriber A callback function, will be passed the args: (element, action)
			 * @returns {?Function} The result of observer.subscribe
			 */
			this.subscribe = function (type, subscriber) {
				function _subscribe(_type, _subscriber) {
					return observer.subscribe(_subscriber, {group: _type});
				}

				if (!observer) {
					observer = new Observer();
					instance.subscribe = _subscribe;
				}
				return _subscribe(type, subscriber);
			};

			/**
			 * Toggles a state of an element.
			 *
			 * @function module:wc/dom/shed.toggle
			 * @param {Element} element The element to act on.
			 * @param {String} action The state to toggle, any one of {@link module:wc/dom/shed~actions}.
			 *   Note that the action passed just gives the "flavor" of the toggle. For example it does not matter
			 *   whether you pass SHOW or HIDE, they are equivalent for toggling. Note that tri-state checkboxes cycle
			 *   from mixed to UNCHECKED. This is specified here: {@link http://www.w3.org/TR/wai-aria-practices/#checkbox}
			 * @param {Boolean} [quiet] If true then do not publish.
			 */
			this.toggle = function (element, action, quiet) {
				var func;
				switch (action) {
					case actions.SHOW:
					case actions.HIDE:
						func = instance.isHidden(element) ? instance[actions.SHOW] : instance[actions.HIDE];
						break;
					case actions.ENABLE:
					case actions.DISABLE:
						func = instance.isDisabled(element) ? instance[actions.ENABLE] : instance[actions.DISABLE];
						break;
					case actions.DESELECT:
					case actions.SELECT:
						func = instance.isSelected(element) !== instance.state.DESELECTED ? instance[actions.DESELECT] : instance[actions.SELECT];
						break;
					case actions.EXPAND:
					case actions.COLLAPSE:
						func = instance.isExpanded(element) ? instance[actions.COLLAPSE] : instance[actions.EXPAND];
						break;
					default:
						throw new TypeError("Unknown action: " + action);
				}
				func(element, quiet);
			};

			/**
			 * Unsubscribe from a SHED action. Public for testing.
			 *
			 * @function module:wc/dom/shed._unsubscribe
			 * @param {String} type The action you want to unsubscribe from (one of shed.actions)
			 * @param {Function} subscriber The subscriber to unsubscribe.
			 * @returns {?Function} The result of {@link module:wc/Observer#subscribe}
			 * @ignore
			 */
			this._unsubscribe = function (type, subscriber) {
				var result;
				if (observer) {
					result = observer.unsubscribe(subscriber, type);
				}
				return result;
			};
		}

		var /** @alias module:wc/dom/shed */instance = new Shed();
		disabledLink.setDisabled(function(element) {
			return instance.isDisabled(element);
		});

		return instance;


		/**
		 * The actions supported by the module.
		 * @typedef {Object} module:wc/dom/shed~actions
		 * @property {String} SHOW "show"
		 * @property {String} HIDE "hide"
		 * @property {String} ENABLE "enable"
		 * @property {String} DISABLE "disable"
		 * @property {String} DESELECT "deselect"
		 * @property {String} SELECT "select"
		 * @property {String} MIX "mix"
		 * @property {String} EXPAND "expand"
		 * @property {String} COLLAPSE "collapse"
		 * @property {String} MANDATORY "mandatory"
		 * @property {String} OPTIONAL "optional"
		 */
	});
