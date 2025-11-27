import { WComponent } from "../WComponent.tsx";
import { getWComponentNodeFromElement, type WComponentNode } from "../data.ts";

export default function WField(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const input = wcNode.children[1] as Element;
	if (input.tagName !== "ui:input") {
		return <></>;
	}

	const className = wcNode.className ? " " + wcNode.className : "";
	const inputWidth = wcNode.attributes["inputWidth"]
		? ` wc_inputwidth wc_fld_inpw_${wcNode.attributes["inputWidth"]}`
		: "";

	const extraAttributes: { [key: string]: string } = {};
	const label = wcNode.children[0] as Element;
	if (label.tagName === "ui:label") {
		extraAttributes.labelId = label.getAttribute("id") ?? "";
		extraAttributes.labelText = label.textContent;
	}

	return (
		<div id={wcNode.id} className={`wc-field${className}${inputWidth}`}>
			<WComponent wcNode={getWComponentNodeFromElement(input.children[0], extraAttributes)} />
		</div>
	);
}
