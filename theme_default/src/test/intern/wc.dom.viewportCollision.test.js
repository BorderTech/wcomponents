define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var viewportCollision,
			getViewportSize,
			HEIGHT,
			WIDTH,
			DIV_WIDTH,
			DIV_HEIGHT,
			OFFSET = -100,
			PX = "px",
			vps,
			testHolder;

		function setSizeAndPosition(element, collide) {
			element.style.width = DIV_WIDTH + PX;
			element.style.height = DIV_HEIGHT + PX;

			element.style.top = "";
			element.style.bottom = "";
			element.style.left = "";
			element.style.right = "";

			if (collide) {
				element.style[collide] = OFFSET + PX;
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
			beforeEach: function() {
				/**
				 * There is a chance that the viewport could be resized between tests so I
				 * want to recalculate some appropriate dimensions for the test elements during setup.
				 */
				vps = getViewportSize(true);
				HEIGHT = vps.height;
				WIDTH = vps.width;
				DIV_WIDTH = Math.round(WIDTH / 2);
				DIV_HEIGHT = Math.round(HEIGHT / 2);
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testCollideRight: function() {
				var element = document.getElementById("collide");
				setSizeAndPosition(element, "right");
				assert.isTrue(viewportCollision(element).e > 0);
			},
			/* testCollideBottom: function() {
				var element = document.getElementById("collide");
				setSizeAndPosition(element, "bottom");
				assert.isTrue(viewportCollision(element).s > 0);
			},*/
			testCollideLeft: function() {
				var element = document.getElementById("collide");
				setSizeAndPosition(element, "left");
				assert.isTrue(viewportCollision(element).w < 0);
			},
			testCollideTop: function() {
				var element = document.getElementById("collide");
				setSizeAndPosition(element, "top");
				assert.isTrue(viewportCollision(element).n < 0);
			},
			testNoCollide: function() {
				var element = document.getElementById("nocollide"),
					collision = viewportCollision(element),
					expected = {"n": 0, "e": 0, "s": 0, "w": 0}, o;
				for (o in expected) {
					assert.strictEqual(expected[o], collision[o]);
				}
			}
			/* ,
			testNoCollideAbsolutelyPositioned: function() {
				var element = document.getElementById("collide"),
					collision,
					expected = {"n": 0, "e": 0, "s": 0, "w": 0}, o;
				// recalculate these as a belt and braces no-one-is-playing-silly-buggers measure
				vps = getViewportSize(true);
				HEIGHT = vps.height;
				WIDTH = vps.width;
				DIV_WIDTH = Math.round(WIDTH / 2);
				DIV_HEIGHT = Math.round(HEIGHT / 2);
				element.style.width = DIV_WIDTH + PX;
				element.style.height = DIV_HEIGHT + PX;
				element.style.left = 0 + PX;
				element.style.top = 0 + PX;
				collision = viewportCollision(element);
				for (o in expected) {
					assert.strictEqual(expected[o], collision[o]);
				}
			}*/

		});
	});
