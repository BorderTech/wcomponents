/**
 * @module
 */
define(function () {
	"use strict";
	/**
	 * Convert an Array-like collection to an Array. This allows Array functions to be called on array-like structures
	 * and is useful, for example, if you want to map a NodeList.
	 * @function module:wc/array/toArray
	 * @public
	 * @param {*} collection An array-like collection of items (for example a NodeList).
	 * @returns {Array} The collection "converted" to a real JavaScript array.
	 *
	 * @example require(["wc/array/toArray"], function(toArray) {
	 *    var myCollection = document.querySelectorAll('div.someClass'),
	 *        myArray = (myCollection && myCollection.length)?toArray(myCollection);
	 *    //now I can do array things on a collection of elements... like reversing them so I can operate from the inside out.
	 *    myArray = myArray.reverse();
	 *    myArray.forEach(function(nextItem){
	 *        //now the elements are in reverse source order which can be useful...
	 *        //do stuff...
	 *    });
	 * });
	 *
	 * @example toArray("String");
	 * //will return ["S","t","r","i","n","g"] (for what it's worth).
	 *
	 * @example myNodeList = document.querySelectorAll('.myClass');
	 * myElementArray = toArray(myNodeList);// === [{Element}, {Element}...]
	 *
	 */
	return function (collection) {
		return Array.prototype.filter.call(collection, function () {
			return true;
		});
	};
});
