import { LoadingStates } from "@components/Loading/LoadingComponents";
import { lazy } from "react";
import { createBrowserRouter } from "react-router";
import App from "./App.js";
import LazyComponentWrapper from "./components/LazyComponentWrapper";
import {
	AppErrorElement,
	LayoutErrorElement,
} from "./components/RouterErrorBoundaries";
import MainLayout from "./MainLayout.js";

const LoginForm = lazy(() => import("@components/LoginForm/LoginForm"));
const HomePage = lazy(() => import("@components/HomePage/HomePage"));
const AddNodePage = lazy(() => import("@components/AddNode/AddNodePage"));
const FormPage = lazy(() => import("@components/FormPage/FormPage"));
const KView = lazy(() => import("@components/KView/KView"));
const Expand = lazy(() => import("@components/Common/Expand"));

export const router = createBrowserRouter([
	{
		Component: App,
		errorElement: <AppErrorElement />,
		children: [
			{
				path: "/",
				element: (
					<LazyComponentWrapper
						fallback={<LoadingStates.Component message="Loading login..." />}
						errorMessage="Failed to load login form"
					>
						<LoginForm />
					</LazyComponentWrapper>
				),
			},
			{
				element: <MainLayout />,
				errorElement: <LayoutErrorElement />,
				children: [
					{
						path: "/home",
						element: (
							<LazyComponentWrapper
								fallback={<LoadingStates.Grid columns={2} count={4} />}
								errorMessage="Failed to load home page"
							>
								<HomePage />
							</LazyComponentWrapper>
						),
					},

					{
						path: "/home/:typeEndpoint",
						element: (
							<LazyComponentWrapper
								fallback={<LoadingStates.Grid columns={2} count={4} />}
								errorMessage="Failed to load the endpoint page"
							>
								<Expand />
							</LazyComponentWrapper>
						),
					},

					{
						path: "/add-node",
						element: (
							<LazyComponentWrapper
								fallback={
									<LoadingStates.Component message="Loading add node page..." />
								}
								errorMessage="Failed to load add node page"
							>
								<AddNodePage />
							</LazyComponentWrapper>
						),
					},
					{
						path: "/add-node/form/:rootId",
						element: (
							<LazyComponentWrapper
								fallback={<LoadingStates.Component message="Loading form..." />}
								errorMessage="Failed to load form page"
							>
								<FormPage />
							</LazyComponentWrapper>
						),
					},
					{
						path: "/kview/:rootId",
						element: (
							<LazyComponentWrapper
								fallback={<LoadingStates.Component message="Chargement de la vue..." />}
								errorMessage="Échec du chargement de la KView"
							>
								<KView />
							</LazyComponentWrapper>
						),
					},
				],
			},
		],
	},
]);
