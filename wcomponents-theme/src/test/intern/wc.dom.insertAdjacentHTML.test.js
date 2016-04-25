define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	var testHolder, subContainer,
		INSERT_ID = "testInsertAdjacentHTMLContainerinsertedElement",
		SUB_ID = "testInsertAdjacentHTMLContainerSubcontainer",
		CONTENT_ID = "testInsertAdjacentHTMLContainerSubContent",
		htmlToInsert = "<p id=\"" + INSERT_ID + "\">Hello</p>";

	registerSuite({
		name: "insertAdjacentHTML",
		setup: function() {
			return testutils.setupHelper([], function() {
				testHolder = testutils.getTestHolder();
			});
		},
		beforeEach: function() {
			testHolder.innerHTML = "<div id=\"" + SUB_ID + "\"><span id=\"" + CONTENT_ID + "\">Hello</span></div>";
			subContainer = document.getElementById(SUB_ID);
		},
		afterEach: function() {
			testHolder.innerHTML = "";
		},
		/* Convert a String to an XML DOM object with DOM methods etc */
		testApiPresent: function() {
			var hasApi = (testHolder.insertAdjacentHTML) ? true : false;
			assert.isTrue(hasApi, "insertAdjacentHTML not found!  All these tests will error.");
		},

		/* make sure we do not have the inserted element before we begin */
		testInsertNotThere: function() {
			assert.isNull(document.getElementById(INSERT_ID));
		},

		/* Inserts html immediately before the object. */
		testInsertBeforeBegin: function() {
			var insertedElement;

			subContainer.insertAdjacentHTML("beforeBegin", htmlToInsert);
			insertedElement = document.getElementById(INSERT_ID);
			assert.strictEqual(insertedElement, subContainer.previousSibling);
		},

		/* Inserts html after the start of the object */
		testInsertAfterBegin: function() {
			var insertedElement;
			subContainer.insertAdjacentHTML("afterBegin", htmlToInsert);
			insertedElement = document.getElementById(INSERT_ID);
			assert.strictEqual(insertedElement, subContainer.firstChild);
		},
		/* Inserts html before all other content in the object. */
		testInsertAfterBeginUseNextSibling: function() {
			var content = document.getElementById(CONTENT_ID),
				insertedElement;

			subContainer.insertAdjacentHTML("afterBegin", htmlToInsert);
			insertedElement = document.getElementById(INSERT_ID);
			assert.strictEqual(insertedElement.nextSibling, content);
		},

		/* Inserts html immediately before the end of the object */
		testInsertBeforeEnd: function() {
			var insertedElement;

			subContainer.insertAdjacentHTML("beforeEnd", htmlToInsert);
			insertedElement = document.getElementById(INSERT_ID);
			assert.strictEqual(insertedElement, subContainer.lastChild);
		},
		/* Inserts html immediately after all other content in the object. */
		testInsertBeforeEndUsePrevSibling: function() {
			var content = document.getElementById(CONTENT_ID),
				insertedElement;

			subContainer.insertAdjacentHTML("beforeEnd", htmlToInsert);
			insertedElement = document.getElementById(INSERT_ID);
			assert.strictEqual(insertedElement.previousSibling, content);
		},

		/*
		 * Inserts html immediately after the end of the object.
		 */
		testInsertAfterEnd: function() {
			var insertedElement;

			subContainer.insertAdjacentHTML("afterEnd", htmlToInsert);
			insertedElement = document.getElementById(INSERT_ID);
			assert.strictEqual(insertedElement, subContainer.nextSibling);
		}
	});
});
