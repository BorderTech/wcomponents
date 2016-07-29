define(function() {

	/**
	 * Returns the difference between two arrays.
	 * @param arr1 The array to compare against.
	 * @param arr2 The array to compare.
	 * @returns {Array} The items found in arr1 but not in arr2.
	 */
	function arrDiff(arr1, arr2) {
		if (arr1 && arr2 && Array.isArray(arr1) && Array.isArray(arr2)) {
			return arr1.filter(function(item) {
				return arr2.indexOf(item) < 0;
			});
		}
		return [];
	}
	return arrDiff;
});
