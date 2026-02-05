import { TextField } from "@mui/material";
import type { WComponentNode } from "../data.ts";

export default function WTextField(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	const disabled = wcNode.attributes["disabled"] === "true" || wcNode.attributes["readOnly"] === "true";
	const required = wcNode.attributes["required"] === "true";

	return (
		<TextField
			id={wcNode.id}
			name={wcNode.id}
			defaultValue={wcNode.value}
			disabled={disabled}
			required={required}
			slotProps={{ htmlInput: { maxLength: wcNode.attributes["maxLength"] } }}
			label={wcNode.attributes["labelText"]}
		/>
	);
}
