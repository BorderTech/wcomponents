define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var template, testHolder; // , i = 0;

//		function getNewKeyName() {
//			return "key" + i++;
//		}

		registerSuite({
			name: "template",
			setup: function() {
				testHolder = testutils.getTestHolder();
				return testutils.setupHelper(["wc/template"]).then(function(arr) {
					template = arr[0];
					testHolder = testutils.getTestHolder();
				});
			},
			beforeEach: function() {
				testHolder.innerHTML = "";
			},
//			afterEach: function() {
//			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testProcess: function() {
				return new Promise(function(win, lose) {
					var testTemplate = "<p>{{name}}</p>",
						name = "WComponents",
						expected = "<div><p>WComponents</p></div>",
						target = document.createElement("div");
					testHolder.appendChild(target);
					template.process({
						source: testTemplate,
						target: target,
						context: {"name": name},
						callback: function() {
							try {
								assert.strictEqual(testHolder.innerHTML, expected, "Template should have been processed.");
								win();
							} catch (ex) {
								lose(ex);
							}
						}
					});
				});
			},
			testProcessWithPosition: function() {
				return new Promise(function(win, lose) {
					var testTemplate = "<p>{{name}}</p>",
						name = "WComponents",
						target,
						expected = "<div><div></div><p>WComponents</p></div>";
					testHolder.innerHTML = "<div><div></div></div>";
					target = testHolder.firstElementChild.firstElementChild;
					template.process({
						source: testTemplate,
						target: target,
						context: {"name": name},
						position: "afterEnd",
						callback: function () {
							try {
								assert.strictEqual(testHolder.innerHTML, expected, "Template should have been processed.");
								win();
							} catch (ex) {
								lose(ex);
							}

						}
					});
				});
			}
		});
	});

// ,
// testRegisterHelperFunction: function() {
// 	var func = function(key) {
// 			return key;
// 		},
// 		token = getNewKeyName(),
// 		expected = "bar",
// 		testTemplate = "{{" + token + " 'bar'}}",
// 		target = document.createElement("div");
// 	try {
// 		template.registerHelper(func, token);
// 		template.process({source: testTemplate, target: target});
// 		assert.strictEqual(target.innerHTML, expected, "Template should have been processed using helper");
// 	}
// 	finally {
// 		Handlebars.unregisterHelper(token);
// 	}
// },
// testRegisterHelperFunctionSafeString: function() {
// 	var func = function(key) {
// 			var p = document.createElement("p");
// 			p.innerHTML = key;
// 			return p.outerHTML;
// 		},
// 		token = getNewKeyName(),
// 		expected = "<p>bar</p>",
// 		testTemplate = "{{" + token + " 'bar'}}",
// 		target = document.createElement("div");
// 	try {
// 		template.registerHelper(func, token, template.PROCESS.SAFE_STRING);
// 		template.process({source: testTemplate, target: target});
// 		assert.strictEqual(target.innerHTML, expected, "Template should have been processed using helper and Handlebars.SafeString");
// 	}
// 	finally {
// 		Handlebars.unregisterHelper(token);
// 	}
// },
// testRegisterHelperFunctionEscape: function() {
// 	var func = function(key) {
// 			var p = document.createElement("p");
// 			p.innerHTML = key;
// 			return p.outerHTML;
// 		},
// 		token = getNewKeyName(),
// 		expected = "&amp;lt;p&amp;gt;bar&amp;lt;/p&amp;gt;",
// 		testTemplate = "{{" + token + " 'bar'}}",
// 		target = document.createElement("div");
// 	try {
// 		template.registerHelper(func, token, template.PROCESS.ESCAPE_EXPRESSION);
// 		template.process({source: testTemplate, target: target});
// 		assert.strictEqual(target.innerHTML, expected, "Template should have been processed using helper and Handlebars.escapeExpression");
// 	}
// 	finally {
// 		Handlebars.unregisterHelper(token);
// 	}
// },
// testRegisterHelperObject: function() {
// 	var token1 = getNewKeyName(),
// 		token2 = getNewKeyName(),
// 		testTemplate = "<h1>{{" + token1 + " 'foo'}}</h1><p>{{" + token2 + " 'bar'}}</p>",
// 		expected = "<h1>foo</h1><p>BAR</p>",
// 		func1 = function(arg) {
// 			return arg;
// 		},
// 		func2 = function(arg) {
// 			return arg.toUpperCase();
// 		},
// 		obj = {},
// 		target = document.createElement("div");
// 	try {
// 		obj[token1] = func1;
// 		obj[token2] = func2;
// 		template.registerHelper(obj);
// 		template.process({source: testTemplate, target: target});
// 		assert.strictEqual(target.innerHTML, expected, "Template should have been processed using both helpers");
// 	}
// 	finally {
// 		Handlebars.unregisterHelper(token1);
// 		Handlebars.unregisterHelper(token2);
// 	}
// },
// testUnregisterHelper: function() {
// 	var func = function(key) {
// 			return key;
// 		},
// 		token = getNewKeyName(),
// 		testTemplate = "{{" + token + " 'bar'}}",
// 		target = document.createElement("div");
// 	// Register a helper which would result in the template being processed as in testRegisterHelperFunction
// 	template.registerHelper(func, token);
// 	template.unregisterHelper(token);
// 	try {
// 		template.process({source: testTemplate, target: target});
// 	}
// 	catch (e) {
// 		// no op
// 	}
// 	finally {
// 		assert.notEqual(target.innerHTML, "bar", "Template should not have been processed using helper");
// 	}
// },
// testRegisterHelperFunctionNoToken: function() {
// 	try {
// 		template.registerHelper(function(key) {
// 			return key;
// 		});
// 		assert.isFalse(true, "registerHelper with function and no token should have thrown an error");
// 	}
// 	catch (e) {
// 		assert.isTrue(true);
// 	}
// }