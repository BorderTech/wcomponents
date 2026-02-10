import { WComponentSet } from "../WComponent.tsx";
import { type WComponentNode } from "../data.ts";

// The generic WComponents field container.
// Extracts classes, label data, and attributes to pass down to the actual field implementations (which should
// be provided as its children).
export default function WField(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const input = wcNode.children.find((child) => (child as Element).tagName === "ui:input");
	if (!input) return <></>;

	const className = wcNode.className ? " " + wcNode.className : "";
	const inputWidth = wcNode.attributes["inputWidth"]
		? ` wc_inputwidth wc_fld_inpw_${wcNode.attributes["inputWidth"]}`
		: "";

	const extraAttributes: { [key: string]: string } = {};
	const label = wcNode.children.find((child) => (child as Element).tagName === "ui:label") as Element;
	if (label) {
		extraAttributes.labelId = label.getAttribute("id") ?? "";
		extraAttributes.labelText = label.textContent;
	}

	return (
		<div id={wcNode.id} className={`wc-field${className}${inputWidth}`}>
			<WComponentSet xmlNodes={Array.from(input.childNodes)} extraAttributes={extraAttributes} />
		</div>
	);
}
