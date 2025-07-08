import { Outlet } from "react-router";
import { createTheme } from "@mui/material";
import { ReactRouterAppProvider } from "@toolpad/core/react-router";
import { useApiData } from "@/hooks/useApiData";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { frFR as corefrFR } from "@mui/material/locale";
import { frFR } from "@mui/x-date-pickers/locales";
import "dayjs/locale/fr";
import { Toaster } from "react-hot-toast";
import type { Navigation } from "@toolpad/core";

const theme = createTheme(
	{
		cssVariables: true,
		colorSchemes: {
			dark: false,
		},
		typography: {
			fontFamily: "IBM Plex Sans, sans-serif",
		},
	},
	frFR,
	corefrFR,
);

export default function App() {
	const { data, isLoading, error } = useApiData("endpoints?only_applicable&type=byransha.web.View");

	const NAVIGATION =
		isLoading || error || !data?.data?.results
			? ([
					{
						kind: "page",
						title: "Loading...",
						segment: "home",
						icon: <MenuOutlinedIcon />,
					},
				] as Navigation)
			: (data.data.results.map((view) => ({
					kind: "page",
					title: view.endpoint,
					segment: `information/${view.endpoint.replaceAll(" ", "_")}`,
					icon: <MenuOutlinedIcon />,
				})) as Navigation);

	return (
		<LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="fr">
			<Toaster />
			<ReactRouterAppProvider
				navigation={NAVIGATION}
				theme={theme}
				branding={{
					title: "",
					logo: (
						<img
							src="/logo.svg"
							alt="I3S"
							width={"100%"}
							height={"100%"}
							color={"inherit"}
						/>
					),
					homeUrl: "/home",
				}}
			>
				<Outlet />
			</ReactRouterAppProvider>
		</LocalizationProvider>
	);
}
