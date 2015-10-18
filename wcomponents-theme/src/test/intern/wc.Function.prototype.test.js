/* eslint-disable strict */
/* These tests cannot run in strict mode due to needing to default "bind" to window */
define(["intern!object", "intern/chai!assert"], function(registerSuite, assert) {

	/**
	 * NOTE: the wc/Function.prototype function will not actually be tested
	 * if there is a native implementation in the browser - that is ok because
	 * our code should never be run under those circumstances.
	 */

	function whoami() {
		return {
			args: arguments,
			me: this
		};
	}

	registerSuite({
		name: "Function.prototype",
		testBindWithNothingCheckArgLength: function() { /* precondition*/
			var bound = whoami.bind(),
				result = bound();
			assert.strictEqual(result.args.length, 0, "arguments.length");
		},
		testBindWithNothing: function() {
			var bound = whoami.bind(),
				result = bound();
			assert.isTrue((window && (window === result.me)), "this");
		},
		testBindWithThisThisIsNotABoundArg: function() { /* precondition: tests that the "this" arg passed to bind is notan arg to the bound function */
			var me = {},
				bound = whoami.bind(me),
				result = bound();
			assert.strictEqual(result.args.length, 0, "arguments.length");
		},
		testBindWithThis: function() {
			var me = {},
				bound = whoami.bind(me),
				result = bound();
			assert.isTrue(me === result.me, "this");
		},
		testBindNoConflict: function() {/* yes, I know, lots of assets: live with it */
			var me0 = {},
				me1 = {},
				me2 = {},
				bound0 = whoami.bind(me0, me0),
				bound1 = whoami.bind(me1, me1),
				bound2 = whoami.bind(me2, me2),
				result0 = bound0(),
				result1 = bound1(),
				result2 = bound2();
			assert.isTrue(me0 === result0.args[0], "me0");
			assert.isTrue(me1 === result1.args[0], "me1");
			assert.isTrue(me2 === result2.args[0], "me2");
			assert.isTrue(me0 === result0.me, "this0");
			assert.isTrue(me1 === result1.me, "this1");
			assert.isTrue(me2 === result2.me, "this2");
		},
		testBindWithNoThisAndArgsNullNotAnArg: function() {
			var me = {
					bar: "foo"
				},
				bound = whoami.bind(null, me),
				result = bound();
			assert.strictEqual(result.args.length, 1, arguments.length);
		},
		testBindWithNoThisAndArgsCorrectArgUsed: function() {
			var me = {
					bar: "foo"
				},
				bound = whoami.bind(null, me),
				result = bound();
			assert.deepEqual(result.args[0], me);
		},
		testBindWithNoThisAndArgsCorrectlyBound: function() {
			var me = {},
				bound = whoami.bind(null, me),
				result = bound();
			assert.isTrue((window && (window === result.me)), "this");
		},
		testBindWithNoThisAndArgsPrependedCorrectNumberArgs: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(null, me),
				result = bound(you);
			assert.strictEqual(result.args.length, 2, arguments.length);
		},
		testBindWithNoThisAndArgsPrependedCorrectArgs: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(null, me),
				result = bound(you);
			assert.deepEqual(result.args[0], me);
			assert.deepEqual(result.args[1], you);
		},
		testBindWithNoThisAndArgsPrependedCorrectBind: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(null, me),
				result = bound(you);
			assert.isTrue((window && (window === result.me)), "this");
		},
		testBindWithThisAndArgsExpectedLength: function() {
			var me = {
					bar: "foo"
				},
				bound = whoami.bind(me, me),
				result = bound();
			assert.strictEqual(result.args.length, 1, arguments.length);
		},
		testBindWithThisAndArgsCorrectArgs: function() {
			var me = {
					bar: "foo"
				},
				bound = whoami.bind(me, me),
				result = bound();
			assert.deepEqual(result.args[0], me);
		},
		testBindWithThisAndArgsCorrectBind: function() {
			var me = {
					bar: "foo"
				},
				bound = whoami.bind(me, me),
				result = bound();
			assert.isTrue(me === result.me, "this");
		},
		testBindWithThisAndArgsPrependedCorrectLength: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(me, me),
				result = bound(you);
			assert.strictEqual(result.args.length, 2, arguments.length);
		},
		testBindWithThisAndArgsPrependedCorrectArgs: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(me, me),
				result = bound(you);
			assert.deepEqual(result.args[0], me);
			assert.deepEqual(result.args[1], you);
		},
		testBindWithThisAndArgsPrependedCorrectBind: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(me, me),
				result = bound(you);
			assert.isTrue(me === result.me, "this");
		},
		testBindWithThisAndMultipleArgsPrependedCorrectLength: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(me, me, 5, you),
				result = bound(you);
			assert.strictEqual(result.args.length, 4, arguments.length);
		},
		testBindWithThisAndMultipleArgsPrependedCorrectArgs: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(me, me, 5, you),
				result = bound(you);
			assert.deepEqual(result.args[0], me);
			assert.strictEqual(result.args[1], 5);
			assert.deepEqual(result.args[2], you);
			assert.deepEqual(result.args[3], you);
		},
		testBindWithThisAndMultipleArgsPrependedCorrectBind: function() {
			var me = {
					bar: "foo"
				},
				you = {
					foo: "bar"
				},
				bound = whoami.bind(me, me, 5, you),
				result = bound(you);
			assert.isTrue(me === result.me, "this");
		}
	});
});
/* eslint-enable strict */
