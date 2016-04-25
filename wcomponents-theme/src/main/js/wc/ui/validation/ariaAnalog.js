/**
 * <p>Client side validation controller for aria-analogs. This is abstract and MUST be extended. You must, at the
 * very least, define this.ITEM and this.SELECT_MODE (usually by grabbing them from the AriaAnalog component
 * equivalent).</p>
 *
 * <p>In addition you should instantiate this.exclusiveSelect (usually by grabbing it from the AriaAnalog component
 * equivalent).</p>
 *
 * <p>Finally, you probably want to override this.validate and this.revalidate.</p>
 *
 * @module wc/ui/validation/ariaAnalog
 * @requires module:wc/dom/group
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/validation/validationManager
 * @requires module:wc/ui/validation/isComplete
 */
define(["wc/dom/group",
		"wc/dom/shed",
		"wc/ui/validation/validationManager",
		"wc/ui/validation/isComplete"],
	/** @param group wc/dom/group @param shed wc/dom/shed @param validationManager wc/ui/validation/validationManager @param isComplete wc/ui/validation/isComplete @ignore */
	function(group, shed, validationManager, isComplete) {
		"use strict";

		/**
		 * @constructor
		 * @abstract
		 * @alias module:wc/ui/validation/ariaAnalog~ValidationAriaAnalog
		 * @private
		 */
		function ValidationAriaAnalog() {}

		/**
		 * @var {int} Must be over-ridden based on the exclusiveSelect property of the ariaAnalog.
		 */
		ValidationAriaAnalog.prototype.exclusiveSelect = -1;

		/**
		 * This should be over-ridden if your component has to be validated and must be a function. If this is not set
		 * then any extending class will ONLY have a completeness test so that it can participate in container
		 * validation.
		 * @function
		 * @protected
		 * @abstract
		 */
		ValidationAriaAnalog.prototype.validate = null;

		/**
		 * This should be over-ridden if your component has to be validated and must be a function. If this is not set
		 * then any extending class will ONLY have a completeness test so that it can participate in container
		 * validation.
		 * @function
		 * @protected
		 * @abstract
		 */
		ValidationAriaAnalog.prototype.revalidate = null;

		/**
		 * Filter function for isComplete.isCompleteHelper. Retuen true if complete.
		 * @function
		 * @private
		 * @param {type} next The component being passed in from the array in isComplete.isCompleteHelper.
		 * @returns {boolean} True if the component 'next' is complete (selected).
		 */
		function isCompleteFilter(next) {
			var _result = shed.isSelected(next);
			if (_result && this.exclusiveSelect === this.SELECT_MODE.SINGLE) {
				_result = !next.getAttribute("${wc.common.attribute.optionIsNull}");
			}
			return _result;
		}

		/**
		 * Generic isComplete subscriber function for selectable components.
		 * @function
		 * @protected
		 * @param {Element} container The DOM node being validated for completeness.
		 * @returns {boolean} true if complete
		 */
		ValidationAriaAnalog.prototype.isCompleteSubscriber = function(container) {
			return isComplete.isCompleteHelper(container, this.ITEM, isCompleteFilter.bind(this), this);
		};

		/**
		 * Get a group container for the aria-analog group.
		 * @see {@link module:wc/dom/ariaAnalog#getGroupContainer}.
		 * @function
		 * @public
		 * @param {Element} element The start point to get the container.
		 * @returns {Element} The containing element for the group.
		 */
		ValidationAriaAnalog.prototype.getGroupContainer = function(element) {
			var result;
			if (this.CONTAINER) {
				result = group.getContainer(element, this.CONTAINER);
			}
			return result;
		};

		/**
		 * React to shed select/deselect to update validation.
		 * @function
		 * @protected
		 * @param {Element} element The element being [de]selected.
		 */
		ValidationAriaAnalog.prototype.shedSubscriber = function(element) {
			var container = this.getGroupContainer(element);
			if (container) {
				this.revalidate(container);
			}
		};

		/**
		 * Set up the validating components and bind any validation handlers.
		 * @function
		 * @public
		 */
		ValidationAriaAnalog.prototype.initialise = function() {
			var isCompleteBound = (this.isCompleteSubscriber ? this.isCompleteSubscriber.bind(this) : null),
				validateBound = (this.validate ? this.validate.bind(this) : null),
				shedBound = (this.revalidate ? this.shedSubscriber.bind(this) : null);

			if (validateBound) {
				validationManager.subscribe(validateBound);
			}
			if (isCompleteBound) {
				isComplete.subscribe(isCompleteBound);
			}
			if (shedBound) {
				shed.subscribe(shed.actions.SELECT, shedBound);
				shed.subscribe(shed.actions.DESELECT, shedBound);
			}
		};

		var /** @alias module:wc/ui/validation/ariaAnalog */ instance = new ValidationAriaAnalog();

		if (typeof Object.freeze !== "undefined") {
			Object.freeze(instance);
		}
		return instance;
	});

