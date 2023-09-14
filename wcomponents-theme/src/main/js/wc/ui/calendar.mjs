/**
 * Provides a calendar based date chooser control for use by WDateFields when a native date control is not available
 * and WPartialDateFields in all cases.
 */

import addDays from "wc/date/addDays.mjs";
import copy from "wc/date/copy.mjs";
import dayName from "wc/date/dayName.mjs";
import daysInMonth from "wc/date/daysInMonth.mjs";
import getDifference from "wc/date/getDifference.mjs";
import monthName from "wc/date/monthName.mjs";
import today from "wc/date/today.mjs";
import interchange from "wc/date/interchange.mjs";
import event from "wc/dom/event.mjs";
import focus from "wc/dom/focus.mjs";
import shed from "wc/dom/shed.mjs";
import viewportCollision from "wc/dom/viewportCollision.mjs";
import getBox from "wc/dom/getBox.mjs";
import i18n from "wc/i18n/i18n.mjs";
import isNumeric from "wc/isNumeric.mjs";
import dateField from "wc/ui/dateField.mjs";
import initialise from "wc/dom/initialise.mjs";
import timers from "wc/timers.mjs";
import wcconfig from "wc/config.mjs";

const DATE_KEY = "date_key",
	CONTAINER_ID = "wc_calbox",
	DAY_CONTAINER_ID = "wc_caldaybox",
	MONTH_SELECT_ID = "wc_calmonth",
	YEAR_ELEMENT_ID = "wc_calyear",
	CONTROL_ATTRIBUTE = "aria-controls",
	AUTO_VALIDATE_WAIT = 250,  // delay after year is changed before the calendar is refreshed
	CLASS = {
		SOUTH: "wc_colsth",
		WEST: "wc_colwest",
		TODAY: "wc_wdf_today",
		DATE_BUTTON: "wc_wdf_pick",
		LAST: "wc_cal_last"
	},
	pickableSelector = `button.${CLASS.DATE_BUTTON}`,
	calbuttonSelector = "button.wc_wdf_mv",
	closebuttonSelector = "button.wc_wdf_cls",
	rowSelector = "tr",
	MIN_ATTRIB = "min",
	MAX_ATTRIB = "max",
	conf = wcconfig.get("wc/ui/calendar", {
		min: 1000,
		max: 9999
	}),
	INITED_ATTRIB = "wc/ui/calendar.BOOTSTAPPED";

let isOpening = false,
	yearChangedTimeout,
	refocusId;

function calendarTemplate(context) {
	const currentMonth = today.get().getMonth();
	const monthOption = (name, idx) => `<option ${currentMonth === idx ? "selected" : ""}>${name}</option>`;
	/*
	 * A template helper that takes a day name and returns the shortest possible meaningful
	 * abbreviation for use when building a month-view calendar as each "day of week" column header.
	 * For example in English this suffices: M, T, W, T, F, S, S (even though S and S are theoretically
	 * ambiguous we can tell by their relative position what they are, same with T and T).
	 * As long as we aren't working with a language where every day of the week starts with the same
	 * letter we're golden.
	 */
	const dayCol = (name) => `<th><abbr title="${name}">${name.charAt(0)}</abbr></th>`;

	return `
	<div class="wc-row">
		<div class="wc-column">
			<select id="${MONTH_SELECT_ID}" title="${context.monthLabel}"><!-- DON'T GIVE NAME! -->
				${context.monthName ? context.monthName.map(monthOption).join("\n") || "" : ""}
			</select>
			<input id="${YEAR_ELEMENT_ID}" value="${context.fullYear}" autocomplete="off" type="number" title="${context.yearLabel}" min="1000" max="9999"/><!-- DON'T GIVE NAME! -->
		</div>
		<div class="wc-column">
		<button type="button" class="wc_wdf_mv wc_btn_icon wc-invite" value="-1" title="${context.lastMonth}"><i aria-hidden="true" class="fa fa-calendar-minus-o"></i></button>
			<button type="button" class="wc_wdf_mv wc_btn_icon wc-invite" value="t" title="${context.today}"><i aria-hidden="true" class="fa fa-calendar-o"></i></button>
			<button type="button" class="wc_wdf_mv wc_btn_icon wc-invite" value="1" title="${context.nextMonth}"><i aria-hidden="true" class="fa fa-calendar-plus-o"></i></button>
		</div>
		<div class="wc-column">
			<button type="button" class="wc_wdf_cls wc_btn_icon wc-invite" title="${context.closeLabel}"><i aria-hidden="true" class="fa fa-calendar-times-o"></i></button>
		</div>
	</div>
	<table id="wc_calendar" cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr>
				${context.dayName ? context.dayName.map(dayCol).join("\n") || "" : ""}
			</tr>
		</thead>
		<tbody id="${DAY_CONTAINER_ID}">
			${`<tr>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>`.repeat(6)}
		</tbody>
	</table>`;
}

/**
 * @returns {HTMLSelectElement}
 */
function findMonthSelect() {
	return /** @type {HTMLSelectElement} */(document.getElementById(MONTH_SELECT_ID));
}

/**
 *
 * @returns {HTMLInputElement}
 */
function findYearField() {
	return /** @type {HTMLInputElement} */(document.getElementById(YEAR_ELEMENT_ID));
}

/**
 *
 * @param {boolean} disable
 */
function resetMonthPickerOptions(disable) {
	const monthSelect = findMonthSelect();
	if (monthSelect?.options?.length) {
		for (const element of Array.from(monthSelect.options)) {
			if (disable) {
				shed.disable(element, true);
			} else {
				shed.enable(element, true);
			}
		}
	}
}

function getMinMaxMonthDay(input, isMax, isDay) {
	let defaultVal = -1;
	const what = (isDay ? "day" : "month"),
		attrib = (isMax ? MAX_ATTRIB : MIN_ATTRIB);

	if (isMax) {
		defaultVal = isDay ? 32 : 12;
	}
	const constraint = input?.getAttribute(attrib);
	if (!constraint) {
		// nothing to do
		return defaultVal;
	}
	const xfrObj = interchange.toValues(constraint);
	const result = Number(xfrObj[what]);

	return isDay ? result : result - 1;
}

/**
 * Shift the focus from the current day element to either the next day LEFT, RIGHT, UP or DOWN depending on the
 * direction the user has indicated
 * @function
 * @private
 * @param {Element} currentElement The element that is currently active (ie the one to which our navigation is relative)
 * @param {string} direction Left, right, up or down (expressed in "arrow key" literals).
 */
function navigateDayLeftRightUpDown(currentElement, direction) {
	// UP    7 days before
	// DOWN  7 days after
	// LEFT  1 day before
	// RIGHT 1 day after
	const days = document.getElementById(DAY_CONTAINER_ID).querySelectorAll(pickableSelector);
	let i = days.length;
	while (i--) {
		if (days[i] === currentElement) {
			break;
		}
	}
	switch (direction) {
		case "ArrowLeft":
			i = Math.max(0, i - 1);
			break;
		case "ArrowUp":
			i = Math.max(0, i - 7);
			break;
		case "ArrowRight":
			i = Math.min(days.length - 1, i + 1);
			break;
		case "ArrowDown":
			i = Math.min(days.length - 1, i + 7);
			break;
	}
	if (currentElement !== days[i]) {  // eg we are already on the last day and the direction is ARROW_RIGHT
		focus.setFocusRequest(days[i]);
	}
}

/**
 * Gets an integer value from an input element for those UAs which do not correctly implement input type="number".
 * @function
 * @private
 * @param element the input holding the year value
 * @returns number or NaN if the input is not numeric
 */
function getYearValueAsNumber(element) {
	let result = element.value.trim();
	if (result && isNumeric(result)) {
		result = parseInt(result, 10);
	} else {
		result = NaN;
	}
	return result;
}

/*
 * check if the month or year was changed, then rebuild calendar
 */
function refresh() {
	const yearField = findYearField(),
		input = getInputForCalendar(),
		limit = getLimits(yearField, input),
		year = getYearValueAsNumber(yearField);

	// ignore invalid years
	if (!isNaN(year)) {
		retrieveDate(function(current) {
			const newDate = setYear(current, year);  // YEAR
			const month = setMonth(newDate, year, limit);  // MONTH
			setDay(current, newDate, year, month, limit);  // DAY

			setDate(newDate, false);
		});
	}
}

/*
 * Helper for refresh.
 * @private
 * @function
 */
function getLimits(yearField, input) {
	return {
		yearMin: yearField.getAttribute(MIN_ATTRIB),
		yearMax: yearField.getAttribute(MAX_ATTRIB),
		monthMin: getMinMaxMonthDay(input),
		monthMax: getMinMaxMonthDay(input, true),
		dayMin: getMinMaxMonthDay(input, false, true),
		dayMax: getMinMaxMonthDay(input, true, true)
	};
}

/*
 * Helper for refresh.
 * @private
 * @function
 */
function setYear(date, year) {
	const newDate = copy(date);
	newDate.setDate(1);  // ALWAYS set date to something less than 29 !!BEFORE!! calling setMonth
	newDate.setFullYear(year);
	return newDate;
}

/**
 * Helper for refresh.
 * @private
 * @function
 */
function setMonth(date, year, limit) {
	const monthSelect = findMonthSelect(),
		month = monthSelect.selectedIndex;
	if (limit.yearMin === year || limit.yearMax === year) {
		if (limit.yearMin === year) {
			if ((limit.monthMin || limit.monthMin === 0) && limit.monthMin > month) {
				date.setMonth(limit.monthMin);
				monthSelect.selectedIndex = limit.monthMin;
			} else {
				date.setMonth(month);
			}
		}
		if (limit.yearMax === year) {
			if (limit.monthMax && limit.monthMax < month) {
				date.setMonth(limit.monthMax);
				monthSelect.selectedIndex = limit.monthMax;
			} else {
				date.setMonth(month);
			}
		}
	} else {
		date.setMonth(month);
	}
	return monthSelect.selectedIndex;
}

/*
 * Helper for refresh.
 * @private
 * @function
 */
function setDay(current, date, year, month, limit) {
	// check if the date was rolled forward
	// this can happen if we go from, say, 31 march back to feb
	let days = current.getDate();
	const daysMax = daysInMonth(date.getFullYear(), date.getMonth() + 1);

	if (limit.yearMin === year || limit.yearMax === year) {
		if (limit.monthMin === month) {
			if (limit.dayMin > days) {
				days = limit.dayMin;
			}
		} else if (limit.monthMax === month) {
			if (limit.dayMax < days) {
				days = limit.dayMax;
			}
		}
	}
	if (days > daysMax) {
		date.setDate(daysMax);
	} else {
		date.setDate(days);
	}
}

/**
 * Refresh the displayed month when the year field is changed.
 * @function
 * @private
 * @param {HTMLInputElement} yearElement The input element holding the year.
 */
function yearChanged(yearElement) {
	const min = yearElement.getAttribute(MIN_ATTRIB) || conf.min,
		max = yearElement.getAttribute(MAX_ATTRIB) || conf.max;
	timers.clearTimeout(yearChangedTimeout);
	yearChangedTimeout = timers.setTimeout(function() {
		const value = getYearValueAsNumber(yearElement);
		if (!isNaN(value) && value >= min && value <= max) {
			yearElement.value = value;
			refresh();
		}
	}, AUTO_VALIDATE_WAIT);
}

/**
 * Hide the calendar.
 *
 * @param {Boolean} [ignoreFocusReset] If true do not attempt to re-focus the calendar icon this is required by
 * {@link module:wc/ui/calendar~selectDay} which needs to focus the dateField not the calendar icon
 * in order to bootstrap the field.
 */
function hideCalendar(ignoreFocusReset) {
	const cal = getCal();

	// touching = null;
	if (cal && !shed.isHidden(cal, true)) {
		// focus the dateField if required
		let input;
		if (!ignoreFocusReset && (input = getInputForCalendar(cal))) {
			refocusId = input.id;
		} else {
			refocusId = null;
		}
		shed.hide(cal);
	}
}

/**
 * Helper for keydown event listener which handles key presses on year input.
 *
 * @function
 * @private
 * @param {HTMLInputElement} element The calendar's year input.
 * @param {string} keyCode The keydown event's key literal.
 * @returns {Boolean} true if the event's default action is to be prevented.
 */
function keydownHelperChangeYear(element, keyCode) {
	yearChanged(element);
	return keyCode === "Enter";  // do not submit on enter/return in year field
}

/**
 * Helper for keydown event listener which handles key presses on date pick buttons.
 *
 * @function
 * @private
 * @param {Element} element the target of the keydown event previously determined as a picker button.
 * @param {KeyboardEvent} $event the keydown event.
 * @returns {Boolean} true if the event is to have its default action prevented.
 */
function keydownHelperDateButton(element, $event) {
	const keyCode = $event.code;
	if (keyCode.startsWith("Arrow")) {
		navigateDayLeftRightUpDown(element, keyCode);
		return true;
	}
	if (keyCode === "KeyT") {
		setDate(new Date(), true);
	}
	if (keyCode === "Tab") {
		if (!$event.shiftKey && element.classList.contains(CLASS.LAST)) {  // tabbing fwd past last day
			focus.setFocusRequest(findMonthSelect());  // move focus to first element
			return true;
		}
	}
	return false;
}

/**
 * Key listeners in the calendar: ESC to close the calendar, ARROW and TAB key walking, SPACE & ENTER key
 * selection of 'pickable' date, Year change handler is target is the year field.
 * @function
 * @private
 * @param {KeyboardEvent & { target: HTMLElement }} $event The keydown event.
 */
function _calendarKeydownEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}

	let element = $event.target,
		handled = false;
	const shiftKey = $event.shiftKey,
		keyCode = $event.key;

	if (keyCode === "Escape") {
		hideCalendar();
		handled = true;  // if the date field is in a dialog, do not close dialog
	} else if (element.id === YEAR_ELEMENT_ID && keyCode !== "Tab" && keyCode !== "Shift") {
		handled = keydownHelperChangeYear(/** @type {HTMLInputElement} */(element), keyCode);
	} else if (keyCode === "Tab" && shiftKey && element === findMonthSelect()) {  // tabbing back past month select
		getOrCreateCal(function(cal) {
			const buttons = cal.querySelectorAll(pickableSelector);
			focus.setFocusRequest(buttons[buttons.length - 1]);  // move focus to last element
		});
		handled = true;
	} else {
		element = element.closest(pickableSelector);
		if (element) {
			handled = keydownHelperDateButton(element, $event);
		}
	}

	if (handled) {
		$event.preventDefault();
	}
}

/**
 * changeEvent listener for month select. Updates the displayed month.
 * @function
 * @private
 * @param {Event} $event A change event.
 */
function monthChangeEvent($event) {
	if (!$event.defaultPrevented) {
		refresh();
	}
}

/**
 * changeEvent listener for year input. Updates the displayed month in the calendar.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLInputElement }} $event A change event.
 */
function yearChangeEvent($event) {
	yearChanged($event.target);
}

/**
 * Builds the actual HTML calendar component
 * @returns {Promise<HTMLElement>}
 */
function create() {
	const _today = today.get();
	return i18n.translate([
		"datefield_calendarMonthLabel",
		"datefield_calendarYearLabel",
		"datefield_lastMonth",
		"datefield_today",
		"datefield_nextMonth",
		"datefield_close"]).then(([monthLabel, yearLabel, lastMonth, todayLabel, nextMonth, closeLabel]) => {

		const calendarProps = {
			dayName: dayName.get(true),
			monthName: monthName.get(),
			fullYear: _today.getFullYear(),
			monthLabel,
			yearLabel,
			lastMonth,
			today: todayLabel,
			nextMonth,
			closeLabel
		};
		const container = document.createElement("div");
		container.id = CONTAINER_ID;
		container.setAttribute("role", "dialog");
		document.body.appendChild(container);
		container.innerHTML = calendarTemplate(calendarProps);
		event.add(container, "keydown", _calendarKeydownEvent);
		event.add(findMonthSelect(), "change", monthChangeEvent);
		event.add(findYearField(), "change", yearChangeEvent);
		return container;
	});

}

/**
 * Get the calendar's containing element.
 * @returns {HTMLElement} The calendar.
 */
function getCal() {
	return document.getElementById(CONTAINER_ID);
}

/**
 * Finds the calendar DOM element if it exists otherwise creates it.
 * @param {function(HTMLElement): void} callback Called with the calendar DOM element.
 */
function getOrCreateCal(callback) {
	const cal = getCal();
	if (cal) {
		callback(cal);
	} else {
		create().then(callback);
	}
}

/*
 * retrieve a stored date for a picker. If one has not been stored return the current date @returns a date object
 */
function retrieveDate(callback) {
	/**
	 * @param {Element} cal
	 */
	getOrCreateCal(cal => {
		const millis = parseInt(cal.dataset[DATE_KEY]);
		if (millis || millis === 0) {
			const dateObj = new Date(millis);
			callback(dateObj);
		} else {
			callback(null);
		}
	});
}

/**
 *
 * @param {Element} [$cal]
 * @returns {HTMLInputElement}
 */
function getInputForCalendar($cal) {
	const cal = ($cal || getCal());
	let inputId, result;
	if (cal && (inputId = cal.getAttribute(CONTROL_ATTRIBUTE))) {
		result = /** @type {HTMLInputElement} */(document.getElementById(inputId));
	}
	return result;
}

/*
 * when a date picker has been set we store the date so that we can reset it if it is reopened @param dateObj
 * the date object to store
 */
function storeDate(dateObj) {
	getOrCreateCal(function(cal) {
		if (!dateObj || dateObj.constructor !== Date) {
			throw new TypeError("storeDate expects a date object");
		}
		const millis = dateObj.getTime();
		console.log("storing date", new Date(millis));
		cal.dataset[DATE_KEY] = String(millis);
	});
}

/**
 * cal.dataset[DATE_KEY] is how the date is passed back and forth from the date input and the calendar
 * control. when we set the date we rebuild the calendar to show this date as the default selected @param date
 * the date object to set the calendar to @param [setFocus] true to focus after setting date @param
 * [setSelected] true to set the date as the current selection
 * @param {Date} date
 * @param {boolean} setFocus
 * @param {boolean} [setSelected]
 */
function setDate(date, setFocus, setSelected) {
	getOrCreateCal(function(cal) {
		const _date = copy(date),  // do not change date
			_today = new Date(),
			monthIndex = _date.getMonth(),
			year = _date.getFullYear(),
			monthElement = findMonthSelect(),
			yearElement = findYearField();

		storeDate(_date);
		monthElement.selectedIndex = monthIndex;
		yearElement.value = String(year);
		// getDay returns 0 = sun 6 = sat, rotate to 0 = mon, 6 = sun
		_date.setDate(1);
		let dayOfWeek = _date.getDay();
		if (--dayOfWeek === -1) {
			dayOfWeek = 6;
		}
		// calculate the first and last days of the calendar
		addDays(-1 * dayOfWeek, _date);
		const endDate = copy(_date);
		addDays(34, endDate);


		let monthEnd = false;
		let inMonth = false;

		let maxYear = Number(yearElement.getAttribute(MAX_ATTRIB));
		let minYear = Number(yearElement.getAttribute(MIN_ATTRIB));
		let input, disableEverything, maxMonth, maxDay, minMonth, minDay, lastDay, focusDay;

		if (minYear || maxYear) {
			resetMonthPickerOptions(false);  // make sure all months are enabled

			input = getInputForCalendar(cal);

			if ((maxYear && year >= maxYear)) {
				if (year > maxYear) {
					disableEverything = true;
				} else {
					maxMonth = getMinMaxMonthDay(input, true);

					if (maxMonth || maxMonth === 0) {
						// disable after maxMonth
						for (let i = maxMonth + 1; i < monthElement.options.length; ++i) {
							shed.disable(monthElement.options[i], true);
						}
						if (maxMonth === monthIndex) {
							maxDay = getMinMaxMonthDay(input, true, true);
						}
					} else {  // should never be here
						resetMonthPickerOptions(false);
					}
				}
			}

			if (minYear && year <= minYear) {
				if (year < minYear) {
					disableEverything = true;
				} else {
					minMonth = getMinMaxMonthDay(input);
					if (minMonth || minMonth === 0) {
						// disable before minMonth
						for (let i = 0; i < minMonth; ++i) {
							if (!monthElement.options[i]) {
								break;
							}
							shed.disable(monthElement.options[i], true);
						}
						if (minMonth === monthIndex) {
							minDay = getMinMaxMonthDay(input, false, true);
						}
					} else {  // should never be here
						resetMonthPickerOptions(false);
					}
				}
			}

			if (disableEverything) {
				resetMonthPickerOptions(true);
			} else if ((minMonth && minMonth > monthIndex) || ((maxMonth || maxMonth === 0) && maxMonth < monthIndex)) {
				// if we did not disableEverything because we were in the wrng year we may still have to disable all days if we are in the wrong month
				disableEverything = true;
			}
		} else {
			resetMonthPickerOptions(false);  // make sure all months are enabled if the date field does not have min/max limit
		}

		const tbody = cal.querySelector("tbody");
		const weeks = Array.from(tbody.querySelectorAll("tr"));
		// build each week
		for (const week of weeks) {
			if (monthEnd) {
				shed.hide(week);
				break;
			} else {
				shed.show(week);
			}

			let days = Array.from(week.querySelectorAll("td"));

			// build each day
			for (const day of days) {
				day.innerHTML = "";

				let text = _date.getDate();

				// if in current month make the element pickable
				if (monthIndex === _date.getMonth()) {
					inMonth = true;
					day.innerHTML = "<button type='button' class='wc-nobutton wc-invite " + CLASS.DATE_BUTTON + "' value='" + text + "'>" + text + "</button>";
					let button = day.firstElementChild;
					lastDay = button;

					if (disableEverything || (minDay && text < minDay) || (maxDay && text > maxDay)) {
						shed.disable(button, true);
					} else {
						shed.enable(button, true);
					}
					let inputDate;
					if (setSelected) {
						if (getDifference(_date, date) === 0) {
							shed.select(button, true);
						}
					} else {
						input = getInputForCalendar(cal);
						if (input?.value) {
							inputDate = dateField.getValue(input);
							if (inputDate) {
								inputDate = interchange.toDate(inputDate);
								if (getDifference(_date, inputDate) === 0) {
									shed.select(button, true);
								}
							}

						}
					}

					if (getDifference(_date, _today) === 0) {
						button.classList.add(CLASS.TODAY);
					}

					if (setFocus && !focusDay && (getDifference(_date, date) === 0)) {
						focusDay = button;
					}
				} else {
					day.appendChild(document.createTextNode(String(text)));
				}
				addDays(1, _date);
				if (inMonth && monthIndex !== _date.getMonth()) {
					monthEnd = true;
				}
			}  // end of days in a week
		}  // end of weeks

		if (lastDay) {
			lastDay.classList.add(CLASS.LAST);
		}
		if (focusDay) {
			focus.setFocusRequest(focusDay);
		}
	});
}


function setMinMaxYear(input) {
	let year;
	if (!(input && (year = findYearField()))) {
		// nothing to do
		return;
	}
	let attrs = {
		min: MIN_ATTRIB,
		max: MAX_ATTRIB
	};
	Object.keys(attrs).forEach(key => {
		const val = input.getAttribute(attrs[key]);
		if (val) {
			const xfrObj = interchange.toValues(val);
			if (xfrObj.year) {
				year.setAttribute(attrs[key], xfrObj.year);
			}
		} else {
			year.setAttribute(attrs[key], conf[key]);
		}
	});
}

/**
 * Show the calendar.
 * @function
 * @private
 * @param {HTMLButtonElement} element The calendar launch button.
 */
function show(element) {
	getOrCreateCal(function(cal) {
		const inputId = element.value;
		cal.setAttribute(CONTROL_ATTRIBUTE, inputId);

		// get the date to use as the default. If there is a date in the input we use that,
		// otherwise we default to today.
		const input = document.getElementById(inputId);
		setMinMaxYear(input);

		const dateString = dateField.getValue(input);
		let date = dateString ? interchange.toDate(dateString) : null;

		if (!date) {
			date = new Date();
			const min = input.getAttribute(MIN_ATTRIB);
			let  constrained = interchange.toDate(min);
			if (constrained && getDifference(constrained, date, false) > 0) {
				date = constrained;
			}
			const max = input.getAttribute(MAX_ATTRIB);
			constrained = interchange.toDate(max);
			if (constrained && getDifference(constrained, date, false) < 0) {
				date = constrained;
			}
		}
		setDate(date, false, !!dateString);

		/*
		 * NOTE: element is the calendar launch button. Do NOT insert the calendar into the DOM before this
		 * button as it has focus and a certain well known browser will lose focus from the element if the DOM
		 * is manipulated preceding that element.
		 */
		if (element.nextSibling) {
			element.parentNode.insertBefore(cal, element.nextSibling);
		} else {
			element.parentNode.appendChild(cal);
		}
		shed.show(cal);
	});
}

/**
 * Does rudimentary collision detection for the calendar so if the calendar is going to overflow the viewport
 * adds a class to move it.
 * @function
 * @private
 * @param {HTMLElement} cal the calendar.
 */
function detectCollision(cal) {
	let collision = viewportCollision(cal);
	const initiallyCollideSouth = collision.s > 0,
		initiallyCollideWest = collision.w < 0;

	/*
	 * NOTE: default open is below input field and lined up at the right edge of the combo so we do not need to
	 * do north collision. If fixing a west collision causes an east collision we can move the calendar west until
	 * it is either fully in viewport OR touches the left edge of the viewport leaving a horizontal scroll.
	 */
	if (initiallyCollideSouth) {
		const top = cal.offsetTop;
		if (!isNaN(top)) {
			cal.style.top = (top - collision.s) + "px";
		}
	}
	if (initiallyCollideWest) {
		cal.classList.add(CLASS.WEST);
		collision = viewportCollision(cal);
		if (collision.e > 0) {
			const box = getBox(cal);
			const left = Math.min(box.left, collision.e);  // we have to move this far left to move the entire calendar into view
			if (left > 0) {
				cal.style.left = (-1 * left) + "px";
			}
		}
	}
}

/**
 *
 * @param {HTMLButtonElement} element
 */
function changeMonth(element) {
	const _today = new Date();

	if (element.value === "t") {
		setDate(_today, true);
		return;
	}

	const monthList = findMonthSelect();
	const numberOfMonths = monthList.options.length;  // should be 12 but who knows when this may change!!
	const yearBox = findYearField();
	// If we do not have a year set then default to this year before change
	let currentYear = getYearValueAsNumber(yearBox);
	let maxYear;
	if (isNaN(currentYear)) {
		currentYear = _today.getFullYear();
		yearBox.value = String(currentYear);
	}
	try {
		if (element.value === "-1") {
			let minYear;
			// go to previous month
			if (monthList.selectedIndex === 0) {
				// change the year first. If we do not have a year set then default to this year then change
				minYear = yearBox.getAttribute(MIN_ATTRIB);
				if (!minYear || minYear < yearBox.value) {
					yearBox.value = String(currentYear - 1);
					monthList.selectedIndex = numberOfMonths - 1;
				}
				return;
			}
			minYear = yearBox.getAttribute(MIN_ATTRIB);
			if (minYear && minYear === yearBox.value) {
				if (getMinMaxMonthDay(getInputForCalendar()) < monthList.selectedIndex) {
					monthList.selectedIndex = monthList.selectedIndex - 1;
				}
			} else {
				monthList.selectedIndex = monthList.selectedIndex - 1;
			}
			return;
		}
		if (monthList.selectedIndex === numberOfMonths - 1) {  // go to next month
			// change the year first. If we do not have a year set then default to this year then change
			maxYear = yearBox.getAttribute(MAX_ATTRIB);
			if (!maxYear || Number(maxYear) > Number(yearBox.value)) {
				yearBox.value = currentYear + 1;
				monthList.selectedIndex = 0;
			}
			return;
		}
		maxYear = yearBox.getAttribute(MAX_ATTRIB);
		if (maxYear && maxYear === yearBox.value) {  // if we have a max on the year input we have a max date, so we need to get the max month if the current year is equal to the max year
			if (getMinMaxMonthDay(getInputForCalendar(), true) > monthList.selectedIndex) {
				monthList.selectedIndex = monthList.selectedIndex + 1;
			}
		} else {
			monthList.selectedIndex = monthList.selectedIndex + 1;
		}
	} finally {
		refresh();
	}
}

/**
 * Actually does the work of activating a calendar launch button. Shows the calendar if it is possible to do so.
 *
 * @function
 * @private
 * @param {HTMLInputElement|HTMLButtonElement} element The launch control button or date input.
 */
function doLaunch(element) {
	try {
		if (element && !isOpening && !shed.isDisabled(element)) {
			isOpening = true;
			show(element);
		}
	} finally {
		isOpening = false;
	}
}

/**
 * The user has clicked a day in the calendar, set the selected date into the date input.
 * @function
 * @private
 * @param {HTMLButtonElement} dayElement The selected day.
 */
function selectDay(dayElement) {
	getOrCreateCal(function(calendar) {
		const input = getInputForCalendar(calendar);

		if (input && !shed.isDisabled(input)) {
			retrieveDate(function(date) {
				const day = dayElement.value;
				date.setDate(day);

				const sb = [date.getDate(), (date.getMonth() + 1), date.getFullYear()];

				input.value = sb.join(" ");
				focus.setFocusRequest(input, function(_el) {
					dateField.acceptFirstMatch(_el);
				});
				hideCalendar(true);
			});
		} else {
			hideCalendar();  // should never get here!
		}
	});
}

function clearMinMaxYear() {
	const year = findYearField();
	if (year) {
		year.setAttribute(MIN_ATTRIB, conf.min);
		year.setAttribute(MAX_ATTRIB, conf.max);
	}
}

/**
 * Calendar icon click listener.
 * @param {MouseEvent & {target: HTMLElement}} $event
 */
function clickEvent({ defaultPrevented, target }) {
	let element;
	if (!defaultPrevented) {
		element = /** @type {HTMLButtonElement} */(target.closest(dateField.getLaunchWidget()));
		if (element) {
			doLaunch(element);
		} else if (getCal()) {  // by using getCal() we can by-pass a widget descriptor lookup if the calendar has never been opened as document.getElementById is very fast.
			element = /** @type {HTMLButtonElement} */(target.closest(pickableSelector));
			if (element) {
				selectDay(element);
				return;
			}
			element = /** @type {HTMLButtonElement} */(target.closest(calbuttonSelector));
			if (element) {
				changeMonth(element);
				return;
			}
			if (target.closest(closebuttonSelector)) {
				hideCalendar();
			}
		}
	}
}

/**
 * Handle a keydown event.
 * @param {KeyboardEvent & { currentTarget: HTMLElement }} $event
 */
function keydownEvent($event) {
	const { currentTarget, altKey, metaKey, key } = $event;
	/** @type {HTMLButtonElement} */
	const launcher = (key === "ArrowDown" && (altKey || metaKey)) ? dateField.get(currentTarget).querySelector(dateField.getLaunchWidget()) : null;
	if (launcher) {
		doLaunch(launcher);
		$event.preventDefault();
	}
}

/**
 * Positions the calendar relative to its input element.
 * @param {HTMLElement} [element] The calendar element (if you already have it, otherwise we'll find it for you).
 */
function position(element) {
	const cal = element || getCal();
	if (cal && !shed.isHidden(cal, true)) {
		const fixed = (window.getComputedStyle && window.getComputedStyle(cal).position === "fixed");
		if (fixed) {
			const input = getInputForCalendar(cal);
			if (input) {
				const box = getBox(input);
				cal.style.top = box.bottom + "px";
			}
		} else {
			cal.style.top = "";
		}
		detectCollision(cal);
	}
}

/**
 * Handle show hide on container.
 * @param element
 * @param action
 */
function containerShowHide(element, action) {
	if (action === shed.actions.HIDE) {
		element.removeAttribute(CONTROL_ATTRIBUTE);
		clearMinMaxYear();
		element.classList.remove(CLASS.WEST);
		element.style.left = "";
		element.style.top = "";
		element.removeAttribute("style");  // remove any inline styles
		// touching = null;
		if (refocusId) {
			const input = document.getElementById(refocusId);
			if (input && focus.canFocus(input)) {
				focus.setFocusRequest(input);
			}
			refocusId = null;
		}
	} else if (action === shed.actions.SHOW) {
		position(element);
		focus.focusFirstTabstop(element);
	}
}

/**
 * Handle show/hide
 */
function shedSubscriber(element, action) {
	if (element.id === CONTAINER_ID) {
		containerShowHide(element, action);
		return;
	}
	const cal = action === shed.actions.HIDE ? getCal() : null;
	if (cal && !!(element.compareDocumentPosition(cal) & Node.DOCUMENT_POSITION_CONTAINS)) {  // if we are hiding something inside the calendar it is probably a row
		if (element.matches(rowSelector)) {
			// we have to remove the pickable elements from any dates which are no longer in the visible calendar
			Array.from(element.querySelectorAll(pickableSelector)).forEach(next => next.parentNode.removeChild(next));
		}
	}
}

/**
 * If something causes (or may cause) the calendar to need repositioning the easiest thing to do is simple close it.
 * The use can reopen it when they are done messing with the viewport.
 */
function reposEvent() {
	const getCompStyle = window.getComputedStyle;

	if (!getCompStyle) {
		hideCalendar();
	}
	const cal = getCal();
	if (cal) {
		const top = getCompStyle(cal)["top"];
		const left = getCompStyle(cal)["left"];

		if (!(top === "0px" && left === "0px")) {
			hideCalendar();
		}
	}
}

/**
 * Focus handler to close the calendar is anything outside the current dateField is focused.
 *
 * @function
 * @private
 * @param {FocusEvent & {target:HTMLElement}} $event A focus[in] event.
 */
function focusEvent($event) {
	const target = $event.target;

	if (dateField.isOneOfMe(target, false) && !target[INITED_ATTRIB]) {
		target[INITED_ATTRIB] = true;
		event.add(target, "keydown", keydownEvent);
	}
	const cal = getCal();
	if (target && cal && !shed.isHidden(cal, true)) {
		const element = dateField.get(target);

		if (!element || (element !== dateField.get(getCal()))) { // second: focused a different date field
			hideCalendar(true);
		}
	}
}

const instance = {
	/**
	 * Public for testing.
	 *
	 * @function module:wc/ui/calendar._keydownEventHandler
	 * @public
	 * @ignore
	 */
	_keydownEventHandler: _calendarKeydownEvent
};

const initialiser = {
	/**
	 * Helper for initialising and de-initialising this module.
	 * @param {boolean} init true if initialising, otherwise deinitialising.
	 * @param {Element} element The element being de/initialised, usually document.body.
	 * @function
	 * @private
	 */
	_initialiseHelper: function (init, element) {
		const func = init ? "add" : "remove";
		event[func](element, "focus", focusEvent, null, null, true);
		event[func](element, "click", clickEvent);
	},

	/**
	 * Helper for late initialising and de-initialising this module.
	 * @param {boolean} init true if initialising, otherwise deinitialising.
	 * @function
	 * @private
	 */
	_postInit: function (init) {
		const ar = init ? "add" : "remove",
			su = init ? "subscribe" : "unsubscribe";
		event[ar](window, "resize", reposEvent);
		shed[su](shed.actions.SHOW, shedSubscriber);
		shed[su](shed.actions.HIDE, shedSubscriber);
	},

	/**
	 * Event wire up on initialise.
	 *
	 * @function module:wc/ui/calendar.initialise
	 * @public
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		return initialiser._initialiseHelper(true, element);
	},

	/**
	 * Late initialisation to set up the {@link module:wc/dom/shed} subscribers.
	 * @function module:wc/ui/calendar.postInit
	 * @public
	 */
	postInit: function() {
		initialiser._postInit(true);
	},

	/**
	 * Unsubscribes event listeners etc.
	 * @param {Element} element The element being deinitialised, usually document.body.
	 */
	deinit: function(element) {
		initialiser._initialiseHelper(false, element);
		initialiser._postInit(false);
	}
};

initialise.register(initialiser);

export default instance;
/**
 * @typedef {Object} module:wc/ui/calendar.config() Optional module configuration.
 * @property {?number} min The minimum year to allow in the date picker.
 * @default 1000
 * @property {?number} max The maximum year to allow in the date picker.
 * @default 9999.
 */
