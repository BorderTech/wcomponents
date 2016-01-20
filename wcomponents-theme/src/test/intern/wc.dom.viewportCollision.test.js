define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var viewportCollision,
			getViewportSize,
			PX = "px",
			testHolder;

		/**
		 * This function runs the single direction collision tests which test a particular absolutely positioned
		 * element which is known to be outside of viewport.
		 *
		 * It is important that the viewport is "neutralized" by resetting its scroll before calculating the collision
		 *
		 * @function
		 * @private
		 * @param {String} position The CSS dimension in which we are colliding.
		 * @param {String} collDirection the viewportCollision object's property we want to investigate for a particular collision.
		 * @param {boolean} expectGreater Indicates if we expect the collision value to be greater or less than 0.
		 */
		function doCollisionTest (position, collDirection, expectGreater) {
			var vps,
				msg = "Expected that the element would collide in direction " + position + ", with collision property " + collDirection,
				element = document.getElementById("collide") || assert.isTrue(false, "where did my element go?"),
				collision;

			// reset the element's position styles - just incase they were set.
			element.style.top = "";
			element.style.bottom = "";
			element.style.left = "";
			element.style.right = "";

			 // There is a chance that the viewport could be resized between tests so we recalculate dimensions for the test at the last possible moment.
			vps = getViewportSize(true);

			// Reset the element dimensions so it is smaller than the viewport.
			element.style.width = Math.floor(vps.width / 2) + PX;
			element.style.height = Math.floor(vps.height / 2) + PX;

			// Position the element to cause a viewport collision.
			element.style[position] = "-100px";

			// Reset all scrolls
			document.body.scrollTop = 0;  // browsers
			document.documentElement.scrollTop = 0;  // IE
			document.body.scrollLeft = 0;  // browsers
			document.documentElement.scrollLeft = 0;  // IE
			// Immediately get the viewport collision - Intern may scroll again!
			collision = viewportCollision(element);

			if (expectGreater) {
				assert.isTrue(collision[collDirection] > 0, msg);
			}
			else {
				assert.isTrue(collision[collDirection] < 0, msg);
			}
		}

		registerSuite({
			name: "domViewportCollision",
			setup: function() {
				return testutils.setupHelper(["wc/dom/viewportCollision", "wc/dom/getViewportSize"], function(c, s) {
					viewportCollision = c;
					getViewportSize = s;
					testHolder = testutils.getTestHolder();
					testHolder.innerHTML = '<div id = "collide" style = "position:absolute;">colliding</div><div id = "nocollide">OK</div>';
				});
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testCollideRight: function() {
				doCollisionTest("right", "e", true);
			},
			testCollideBottom: function() {
				doCollisionTest("bottom", "s", true);
			},
			testCollideLeft: function() {
				doCollisionTest("left", "w", false);
			},
			testCollideTop: function() {
				doCollisionTest("top", "n", false);
			},
			testNoCollideAbsolutelyPositioned: function() {
				var element,
					collision,
					expected = {"n": 0, "e": 0, "s": 0, "w": 0},
					o,
					vps;

				element = document.getElementById("collide")|| assert.isTrue(false, "where did my element go?");


				// calculate the viewport size as late as possible
				vps = getViewportSize(true);

				// make sure element is smaller than viewport
				element.style.width = Math.floor(vps.width / 2) + PX;
				element.style.height = Math.floor(vps.height / 2) + PX;

				// reset the element's position styles - just incase they were set but make top and left both 0 so there is no collision.
				element.style.top = 0 + PX;
				element.style.bottom = "";
				element.style.left = 0 + PX;
				element.style.right = "";
				// Reset all scrolls
				document.body.scrollTop = 0;  // browsers
				document.documentElement.scrollTop = 0;  // IE
				document.body.scrollLeft = 0;  // browsers
				document.documentElement.scrollLeft = 0;  // IE
				collision = viewportCollision(element);

				for (o in expected) {
					if (expected.hasOwnProperty(o)) {
						assert.strictEqual(expected[o], collision[o], "Did not expect a collision on " + o);
					}
				}
			}

		});
	});
