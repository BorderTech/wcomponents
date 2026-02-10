// Extracts required CSS classes from XML representation.
export function getClassesFromMarginTag(marginElement: Element): string {
	let value = marginElement.getAttribute("all");
	if (value) {
		return ` wc-margin-all-${value}`;
	} else {
		let className = "";
		value = marginElement.getAttribute("north");
		if (value) {
			className += ` wc-margin-north-${value}`;
		}
		value = marginElement.getAttribute("east");
		if (value) {
			className += ` wc-margin-east-${value}`;
		}
		value = marginElement.getAttribute("south");
		if (value) {
			className += ` wc-margin-south-${value}`;
		}
		value = marginElement.getAttribute("west");
		if (value) {
			className += ` wc-margin-west-${value}`;
		}
		return className;
	}
}
