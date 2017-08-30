define(["wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/formUpdateManager",
	"wc/dom/getFilteredGroup",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/ui/ajaxRegion"],
	function(event, initialise, formUpdateManager, getFilteredGroup, shed, Widget, ajaxRegion) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/shuffler~Shuffler
		 * @private
		 */
		function Shuffler() {
			var MOVE_BUTTON = new Widget("button", "wc_sorter"),
				CONTAINER = new Widget("", "wc-shuffler"),
				SHUFFLER_SELECT,
				UP = "up",
				DOWN = "down",
				TOP = "top",
				BOTTOM = "bottom";

			function writeState(form, stateContainer) {
				/*
				 * "Clean up" the state of the dual multi select control.
				 * IE ensure the correct options are selected/deselected in the submit element,
				 * based on the selections made in the available/chosen elements.
				 */
				function _writeState(container) {
					SHUFFLER_SELECT = SHUFFLER_SELECT || new Widget("select", "wc_shuffler");
					var list = SHUFFLER_SELECT.findDescendant(container),
						options, i, len, next;

					if (list && !shed.isDisabled(list)) {
						options = list.options;
						for (i = 0, len = options.length; i < len; i++) {
							next = options[i];
							formUpdateManager.writeStateField(stateContainer, container.id, next.value);
						}
					}
				}
				Array.prototype.forEach.call(CONTAINER.findDescendants(form), _writeState);
			}

			/**
			 * Moves options within a select or optgroup. Options in an optgroup are bound by that optgroup.
			 * @function
			 * @private
			 * @param {Element} element The button which causes all the fuss.
			 */
			function move(element) {
				var selected, i,
					select = document.getElementById(element.getAttribute("aria-controls")),
					position = element.value,
					container;

				/*
				 * Given an option we look up position and move the option accordingly (if possible)
				 * This is not as easy as it sounds!
				 * If we are moving an option up we see if it has a previous sibling and if the
				 * 	previous sibling is not selected we move the option. We do the selected test
				 *  to prevent the situation which would occur when attempting to move a consecutive
				 *  group of options.
				 * If we are moving the option down we want to find the sibling after the next sibling.
				 *   If we find one we put the option before it. If we don't we look for the option's
				 *   next sibling (this means the option is the penultimate sibling) and if we find one
				 *   we make the option the last option of its parent. If the option has no next sibling
				 *   it is already at the bottom and is not moved.
				 * If we are moving the option to the top we get the parent elment's first child and if
				 *   it is not the option we put the option before it (no good trying to move an option
				 *   before itself!)
				 * If we are moving the option to the bottom of the list we check if it has a next sibling
				 *   and if so make it the parent element's last child, otherwise it is already the last
				 *   child
				 *
				 * @param option the option element to move
				 */
				function _moveIt(option) {
					var reference,
						parent = option.parentNode;
					switch (position) {
						case UP:
							reference = option.previousSibling;
							if (reference && selected.indexOf(reference) === -1) {  // the test on selected is to prevent a group of consecutive options at the top or bottom fighting each other
								parent.insertBefore(option, reference);
							}
							break;
						case DOWN:
							if ((reference = option.nextSibling)) {
								reference = reference.nextSibling;  // we want the option after the next option (if there is one)
							}
							if (reference) {
								if (selected.indexOf(option.nextSibling) === -1 || selected.indexOf(reference) === -1) {
									parent.insertBefore(option, reference);
								}
							} else if ((reference = option.nextSibling) && selected.indexOf(reference) === -1) {
								// this will happen if we try to move the penultimate child down
								parent.appendChild(option);
							}
							break;
						case TOP:
							if ((reference = parent.firstChild) && reference !== option) {
								parent.insertBefore(option, reference);
							}
							break;
						case BOTTOM:
							if (option.nextSibling) {
								parent.appendChild(option);
							}
							break;
					}
				}

				if (select && (selected = getFilteredGroup(select)).length > 0) {
					if (position === TOP || position === DOWN) {
						for (i = selected.length - 1; i >= 0; --i) {  // reverse the order of move to top
							_moveIt(selected[i]);
						}
					} else {
						selected.forEach(_moveIt);
					}
					// If we are in a WShuffler we will have to manually fire any ajax triggers
					if ((container = CONTAINER.findAncestor(element)) && ajaxRegion.getTrigger(container, true)) {
						ajaxRegion.requestLoad(container, null, true);
					}
				}
			}

			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = MOVE_BUTTON.findAncestor($event.target)) && !shed.isDisabled(element)) {
					move(element);
				}
			}

			/**
			 * Set up shuffle controller.
			 * @function module:wc/ui/shuffler.initialise
			 * @param {Element} element The element being initialised, usually document.body
			 */
			this.initialise = function (element) {
				event.add(element, event.TYPE.click, clickEvent);
				formUpdateManager.subscribe(writeState);
			};

			/**
			 * Get the Widget that descibes a shuffler.
			 * @function module:wc/ui/shuffler.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget}
			 */
			this.getWidget = function() {
				return CONTAINER;
			};
		}

		/**
		 * Provides functionality for changing the order of options in a list (select or optgroup).
		 *
		 * @module
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/formUpdateManager
		 * @requires module:wc/dom/getFilteredGroup
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 *
		 * @todo Document private members.
		 */
		var instance = new Shuffler();
		initialise.register(instance);
		return instance;
	});
