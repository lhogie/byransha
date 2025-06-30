import { createBrowserRouter } from "react-router";
import App from "./App.js";
import LoginForm from "../Components/LoginForm/LoginForm";
import MainLayout from "./MainLayout.js";
import HomePage from "../Components/HomePage/HomePage";
import InformationPage from "../Components/InformationPage/InformationPage";
import AddNodePage from "../Components/AddNode/AddNodePage";
import FormPage from "../Components/FormPage/FormPage";

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
