import { DesktopDatePicker } from "@mui/x-date-pickers";
import { type WComponentNode } from "../data.ts";

export default function WDateField(props: { wcNode: WComponentNode }) {
	const { wcNode } = props;

	return <DesktopDatePicker label={wcNode.attributes["labelText"]} />;
}
