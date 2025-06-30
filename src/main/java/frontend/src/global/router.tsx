import { createBrowserRouter } from "react-router";
import App from "./App.js";
import LoginForm from "@components/LoginForm/LoginForm";
import MainLayout from "./MainLayout.js";
import HomePage from "@components/HomePage/HomePage";
import InformationPage from "@components/InformationPage/InformationPage";
import AddNodePage from "@components/AddNode/AddNodePage";
import FormPage from "@components/FormPage/FormPage";

export const router = createBrowserRouter([
	{
		Component: App,
		children: [
			{
				path: "/",
				element: <LoginForm />,
			},
			{
				element: <MainLayout />,
				children: [
					{
						path: "/home",
						element: <HomePage />,
					},
					{
						path: "/information/:viewId",
						element: <InformationPage />,
					},
					{
						path: "/add-node",
						element: <AddNodePage />,
					},
					{
						path: "/add-node/form/:classForm",
						element: <FormPage />,
					},
				],
			},
		],
	},
]);
