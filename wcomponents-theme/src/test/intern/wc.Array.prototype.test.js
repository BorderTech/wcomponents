define(["intern!object", "intern/chai!assert"], function(registerSuite, assert) {
	"use strict";
	/**
	 * NOTE: the wc/Array.prototype function will not actually be tested
	 * if there is a native implementation in the browser - that is ok because
	 * our code should never be run under those circumstances.
	 */

	/**
	 * Filter function used in some tests
	 */
	function isBigEnough(element /* , index, array */ ) {
		return (element >= 10);
	}


	registerSuite({
		name: "Array.prototype",
		testArrayEvery: function() {
			var arrPass = [12, 54, 18, 130, 44];
			assert.isTrue(arrPass.every(isBigEnough));
		},
		testArrayEveryFails: function() {
			var arrFail = [12, 5, 8, 130, 44];
			assert.isFalse(arrFail.every(isBigEnough));
		},
		testArraySome: function() {
			var arrPass = [12, 5, 8, 1, 4];
			assert.isTrue(arrPass.some(isBigEnough));
		},
		testArraySomeFails: function() {
			var arrFail = [2, 5, 8, 1, 4];
			assert.isFalse(arrFail.some(isBigEnough));
		},

		testArrayFilterReturnsExpectedArray: function() {
			var arr = [12, 5, 8, 130, 44],
				expectedResult = [12, 130, 44];
			assert.deepEqual(arr.filter(isBigEnough), expectedResult);
		},
		testArrayFilterDoesNotMutateOriginal: function() {
			var arr = [12, 5, 8, 130, 44],
				arrClone = [12, 5, 8, 130, 44];
			arr.filter(isBigEnough);
			assert.deepEqual(arr, arrClone, "original array should not be mutated");
		},
		testArrayFilter: function() {/* Depends on a working Array.every */
			var arr = [12, 5, 8, 130, 44],
				passed = false, result;
			result = arr.filter(isBigEnough);
			passed = result.every(isBigEnough);
			assert.isTrue(passed);
		},
		testArrayMap: function() {
			var arr = [1, 4, 9],
				expectedResult = [1, 2, 3];
			assert.deepEqual(arr.map(Math.sqrt), expectedResult);
		},
		testArrayMapDoesNotMutateOriginal: function() {
			var arr = [1, 4, 9],
				arrClone = [1, 4, 9];
			arr.map(Math.sqrt);
			assert.deepEqual(arr, arrClone, "original array should not be mutated");
		},
		testArrayForEach: function() {
			var arr = ["zero", "one", "three"],
				test = {
					zero: -1,
					one: -1,
					two: -1,
					three: -1
				};
			function forEachHelper(element, index) {
				test[element] = index;
			}
			arr.forEach(forEachHelper);
			assert.strictEqual(test["zero"], 0);
			assert.strictEqual(test["one"], 1);
			assert.strictEqual(test["two"], -1);
			assert.strictEqual(test["three"], 2);
		},
		testArrayForEachDoesNotMutateOriginal: function() {
			var arr = ["zero", "one", "three"],
				arrClone = ["zero", "one", "three"],
				test = {
					zero: -1,
					one: -1,
					two: -1,
					three: -1
				};
			function forEachHelper(element, index) {
				test[element] = index;
			}
			arr.forEach(forEachHelper);
			assert.deepEqual(arr, arrClone, "original array should not be mutated");
		},
		testArrayReduce: function() {
			var arr = [0, 1, 2, 3],
				expectedResult = 6;
			function reduceHelper(a, b) {
				return a + b;
			}
			assert.strictEqual(arr.reduce(reduceHelper), expectedResult);
		},
		testArrayReduceDoesNotMutate: function() {
			var arr = [0, 1, 2, 3],
				arrClone = [0, 1, 2, 3];
			function reduceHelper(a, b) {
				return a + b;
			}
			arr.reduce(reduceHelper);
			assert.deepEqual(arr, arrClone, "original array should not be mutated");
		},
		testArrayReduceRight: function() {
			var arr = [0, 1, 2, 3],
				expectedResult = 6;
			function reduceHelper(a, b) {
				return a + b;
			}
			assert.strictEqual(arr.reduceRight(reduceHelper), expectedResult);
		},
		testArrayReduceRightDoesNotMutate: function() {
			var arr = [0, 1, 2, 3],
				arrClone = [0, 1, 2, 3];
			function reduceHelper(a, b) {
				return a + b;
			}
			arr.reduceRight(reduceHelper);
			assert.deepEqual(arr, arrClone, "original array should not be mutated");
		},
		testArrayReduce2: function() {
			var arr = [
					[0, 1],
					[2, 3],
					[4, 5]
				],
				expectedResult = [0, 1, 2, 3, 4, 5];

			function reduceHelper(a, b) {
				return a.concat(b);
			}
			assert.deepEqual(arr.reduce(reduceHelper, []), expectedResult);
		},
		testArrayReduceRight2: function() {
			var arr = [
					[0, 1],
					[2, 3],
					[4, 5]
				],
				expectedResult = [4, 5, 2, 3, 0, 1];
			function reduceHelper(a, b) {
				return a.concat(b);
			}
			assert.deepEqual(arr.reduceRight(reduceHelper, []), expectedResult);
		},
		testArrayIndexOf: function() {
			var arr = [2, 5, 9, 2];
			assert.strictEqual(arr.indexOf(2), 0);
			assert.strictEqual(arr.indexOf(7), -1);
			assert.strictEqual(arr.indexOf(9, -2), 2);
		},
		testArrayLastIndexOf: function() {
			var arr = [2, 5, 9, 2];
			assert.strictEqual(arr.lastIndexOf(2), 3);
			assert.strictEqual(arr.lastIndexOf(7), -1);
			assert.strictEqual(arr.lastIndexOf(2, 3), 3);
			assert.strictEqual(arr.lastIndexOf(2, 2), 0);
			assert.strictEqual(arr.lastIndexOf(2, -2), 0);
			assert.strictEqual(arr.lastIndexOf(2, -1), 3);
		}
	});
});
