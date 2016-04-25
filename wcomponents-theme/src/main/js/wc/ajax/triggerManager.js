/**
 * Manager for collections of AJAX triggers. The instance returned by this module should be your first forst of call to
 * get at or manipulate ajax triggers.
 *
 * @see {@link module:wc/ajax/Trigger}
 *
 * @module
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/getAncestorOrSelf
 */
define(["wc/dom/getAncestorOrSelf", "wc/dom/tag"],
	/** @param getAncestorOrSelf wc/dom/getAncestorOrSelf @param tag wc/dom/tag @ignore */
	function(getAncestorOrSelf, tag) {
		"use strict";

		/**
		 * Provides a manager for collections of Ajax Triggers.
		 * @constructor
		 * @alias module:wc/ajax/triggerManager~TriggerManager
		 * @private
		 */
		function TriggerManager() {
			var ALIAS = "data-wc-ajaxalias",
				triggerRegister = {};


			/**
			 * Is an element a HTML form control or an A element?
			 *
			 * @function
			 * @private
			 * @param {Element} element The element to test
			 * @returns {Boolean} true if the element is a form control.
			 * @todo Just return the output of Array.some...
			 */
			function isFormControlOrLink(element) {
				var result = [tag.INPUT, tag.BUTTON, tag.SELECT, tag.TEXTAREA, tag.A].some(function(item) {
					return element.tagName === item;
				});
				return result;
			}

			/**
			 * Get a container associated with an ajax trigger based on an element inside that container. This is used
			 * to find the ajax trigger parent ANCHOR or FIELDSET when the element itself is not a trigger but maybe it
			 * is nested in one.
			 *
			 * For now just look for anchors (buttons do not need this). This is to handle this case:
			 * <code>&lt;a id="iAmTheTrigger"&gt;foo&lt;span&gt;bar&lt;/span&gt;&lt;/a&gt;</code>
			 * Where the span element would be reported as the event target if the user clicks on the 'bar' part of
			 * 'foobar'. Now also handles the case where we register the trigger on a fieldset (for a bunch of radio
			 * buttons).
			 *
			 * NOTE: Do not be fooled into iterating over parent nodes to find an AJAX trigger. This *will* give
			 * you false positives.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element which we thought was a trigger but wasn't.
			 * @returns {?Element} The container which is the "actual" trigger element - i.e. the one with the id which
			 *    is associated with the trigger. This is either a A element or a FIELDSET element.
			 */
			function getTriggerContainer(element) {
				var result;
				if (element.tagName === tag.A) {  // if we pass in an A element then we know it is not itself the trigger container.
					if (element.parentNode) {
						result = getAncestorOrSelf(element.parentNode, tag.A);
					}
				}
				else {
					result = getAncestorOrSelf(element, tag.A);
				}
				if (!result && isFormControlOrLink(element)) {
					result = getAncestorOrSelf(element, tag.FIELDSET);
				}
				return result;
			}

			/**
			 * Add a {@link  module:wc/ajax/Trigger} to the trigger registry.
			 * @function module:wc/ajax/triggerManager.addTrigger
			 * @param { module:wc/ajax/Trigger} trigger The trigger to add.
			 */
			this.addTrigger = function(trigger) {
				var id = trigger.id;
				if (this.getTrigger(id)) {
					console.info("Overwriting existing trigger", id);
				}
				triggerRegister[id] = trigger;
			};

			/**
			 * Get an AJAX Trigger for a particular element, identified by itself or its ID.
			 * @function module:wc/ajax/triggerManager.getTrigger
			 * @param {(String|Element)} ref The ID of the trigger to retrieve OR a DOM element which may be associated
			 *    with a trigger. The way an element and a trigger are associated is abstracted away in this black box.
			 *    Note, passing the ID as a string bypasses checks for AJAX triggers related in the DOM, think of it as
			 *    totally "DOM unaware" - this gives you a higher performance option but with less power. You need to
			 *    pass an element for "DOM awareness".
			 * @param {Boolean} [ignoreAncestor] If true will not check to see if DOM ancestor is a trigger.
			 * @returns {?module:wc/ajax/Trigger} The trigger, if any.
			 */
			this.getTrigger = function(ref, ignoreAncestor) {
				var result, id, triggerParams, _ref;
				if (ref) {
					if (ref.constructor === String) {
						// Just treating this as a string, no DOM awareness.
						if (triggerRegister.hasOwnProperty(ref)) {
							console.log("Finding trigger by id");
							result = triggerRegister[ref];
						}
					}
					else {  // it must be a DOM element
						if ((id = ref.id)) {  // try id
							console.log("Found trigger by element (id match)");
							result = this.getTrigger(id);
						}
						if (!result && (id = ref.name)) {  // try name (esp. radio buttons)
							console.log("Found trigger by element (name match)");
							result = this.getTrigger(id);
						}
						if (!result && (id = ref.getAttribute(ALIAS))) {
							console.log("Finding trigger by alias match");
							result = this.getTrigger(id);
						}
						if (!result && !ignoreAncestor) {
							_ref = getTriggerContainer(ref);
							if (_ref) {
								console.log("Looking for ancestor trigger");
								result = this.getTrigger(_ref, ignoreAncestor);
							}
						}
					}
				}
				if (result) {
					if (ref.getAttribute && (triggerParams = ref.getAttribute("data-wc-params"))) {
						result._triggerParams = triggerParams;
					}
					else {
						delete result._triggerParams;
					}
				}
				return result;
			};

			/**
			 * Remove an ajax trigger from the trigger registry.
			 * @function module:wc/ajax/triggerManager.removeTrigger
			 * @param {String} id The id of the element associated with the trigger.
			 */
			this.removeTrigger = function(id) {
				delete triggerRegister[id];
			};
		}

		return /** @alias module:wc/ajax/triggerManager */new TriggerManager();
	});
