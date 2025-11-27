import { useEffect, useState } from "react";
import { WComponent } from "./WComponent.tsx";
import { getClientLayout, type WComponentNode } from "./data.ts";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

export default function App() {
	const [application, setApplication] = useState<WComponentNode | null>(null);

	useEffect(() => {
		getClientLayout()
			.then((c) => setApplication(c))
			.catch((e) => console.error(e));
	}, []);

	console.log("render");
	return (
		<>
			{!application && <p>No valid XML application structure</p>}
			<LocalizationProvider dateAdapter={AdapterDayjs}>
				<div>{application && <WComponent wcNode={application} />}</div>
			</LocalizationProvider>
		</>
	);
}
