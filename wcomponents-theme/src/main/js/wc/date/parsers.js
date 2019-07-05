define(["wc/date/Parser", "wc/i18n/i18n"], function(Parser, i18n) {

	/**
	 * Provides the set of configured date Parser instances.
	 * @constructor
	 */
	function Parsers() {
		var parsers,
			typeEnum = {
				STANDARD: "standard",
				PAST: "past",
				PARTIAL: "partial",
				PARTIAL_PAST: "partialPast"
			};

		this.type = typeEnum;  // Make the type enum public.

		/**
		 * Gets a configured parser instance.
		 * @param {wc/date/parsers~type} [type] The type of parser to retrieve, defaults to STANDARD.
		 * @return {wc/date/Parser} A configured date parser.
		 */
		this.get = function(type) {
			var result;
			if (!parsers) {
				initParsers();  // lazy init parser instances
			}
			result = parsers[typeEnum.STANDARD];
			if (type) {
				if (parsers.hasOwnProperty(type)) {
					result = parsers[type];
				} else {
					console.warn("Invalid parser type", type);
				}
			}
			return result;
		};

		/**
		 * Initialises the "parsers" instance variable lazily, on demand.
		 * @function
		 * @private
		 */
		function initParsers() {
			var shortcuts = ["ytm", "+-"],
				standardMasks = shortcuts.concat(i18n.get("datefield_masks_full").split(",")),
				partialMasks = standardMasks.concat(i18n.get("datefield_masks_partial").split(","));

			/*
			 * Creates a new instance of a Parser
			 */
			function createParser(masks, expandYearIntoPast, rolling) {
				var result = new Parser();
				result.setRolling(!!rolling);
				result.setMasks(masks || standardMasks);
				result.setExpandYearIntoPast(!!expandYearIntoPast);
				return result;
			}

			parsers = {};
			parsers[typeEnum.STANDARD] = createParser();
			parsers[typeEnum.PAST] = createParser(null, true);
			parsers[typeEnum.PARTIAL] = createParser(partialMasks);
			parsers[typeEnum.PARTIAL_PAST] = createParser(partialMasks, true);
		}
	}

	return new Parsers();
});
