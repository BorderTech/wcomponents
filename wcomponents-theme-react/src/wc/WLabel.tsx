import { type WComponentNode } from "../data.ts";

// Labels (ui:label) are generally consumed by field components and rendered as part of the MUI form inputs,
// but sometimes they appear on their own and need to be explicitly handled.
export default function WLabel(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return (
		<label id={wcNode.id} htmlFor={wcNode.attributes["for"]} className="wc-label">
			{wcNode.value}
		</label>
	);
}
