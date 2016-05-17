/**
 * This is an IE bug fix (specifically IE8).
 *
 * BUG: When a descendant of FORM has 'focus' the form submit event will fire when the enter key is pressed even if the
 * element is not an input element which supports submit on ENTER.
 *
 * This "fix" is limited in that it will not prevent the IE bug if the enter key is pressed before the page has finished
 * loading, which can be late on a large page.
 *
 * @module
 * @private
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @deprecated IE8 support is now in a rot state.
 */
define(["wc/dom/event", "wc/dom/focus", "wc/dom/initialise", "wc/dom/Widget"],
	/** @param event @param focus @param initialise @param Widget @ignore */
	function(event, focus, initialise, Widget) {
		"use strict";

		function FixDefaultSubmitControl() {
			var FORM_WD = new Widget("form"),
				FILE_UPLOAD = new Widget("INPUT", "", {type: "file"});

			this.initialise = function (element) {
				event.add(element, event.TYPE.keypress, keyEvent, 100);
				console.log("initialising IE default submit bug fix");
			};

			/*
			 * @param $event the keypress event
			 */
			function keyEvent($event) {
				var keyCode = $event.keyCode, element = $event.target;
				if (!$event.defaultPrevented && (keyCode === KeyEvent.DOM_VK_RETURN)) {
					if (FILE_UPLOAD.isOneOfMe(element) || (!focus.isTabstop(element) && FORM_WD.findAncestor(element))) {
						$event.preventDefault();
					}
				}
			}
		}
		var fixDefaultSubmitControl = new FixDefaultSubmitControl();
		initialise.addBodyListener(fixDefaultSubmitControl);
		return /** @alias module:wc/fix/defaultSubmit_ie8 */ fixDefaultSubmitControl;
	});
