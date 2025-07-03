import {
	Autocomplete,
	CircularProgress,
	TextField,
	ListItem,
	ListItemAvatar,
	Avatar,
	ListItemText,
	Badge,
} from "@mui/material";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { useDebounce } from "use-debounce";
import WarningIcon from "@mui/icons-material/Warning";

export const SearchBar = ({key}: {key?: string}) => {
	const [query, setQuery] = useState("");
	const [debounceQuery] = useDebounce(query, 250, { maxWait: 500 });
	const navigate = useNavigate();
	const queryClient = useQueryClient();

	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			await queryClient.invalidateQueries();
		},
	});
	const { isLoading, data } = useApiData(
		"search_node",
		{
			query: debounceQuery,
		},
		{
			enabled: debounceQuery.length > 0,
			gcTime: 30000
		},
	);

	const results: {
		id: string;
		name: string;
		type: string;
		img?: string;
		imgMimeType?: string;
		isValid: boolean;
	}[] = data?.data?.results?.[0]?.result?.data || [];

	return (
		<Autocomplete
			sx={{ width: 300 }}
			isOptionEqualToValue={(option, value) => option.name === value.name}
			getOptionKey={(option) => option.id}
			getOptionLabel={(option) => `${option.name} (${option.type})`}
			options={results}
			loading={isLoading}
			renderOption={(props, option) => (
				<ListItem {...props} key={option.id}>
					<ListItemAvatar key={`${option.id}.avatar`}>
						<Badge
							invisible={option.isValid}
							badgeContent={<WarningIcon color="warning" />}
						>
							<Avatar
								src={
									option.img
										? `data:${option.imgMimeType};base64,${option.img}`
										: ""
								}
								alt={option.name}
							/>
						</Badge>
					</ListItemAvatar>
					<ListItemText
						key={`${option.id}.text`}
						primary={option.name}
						secondary={option.type}
						primaryTypographyProps={{ style: { marginRight: 8 } }}
					/>
				</ListItem>
			)}
			onChange={(_event, value) => {
				if (value) {
					jumpMutation.mutate({
						node_id: value.id,
					});
					navigate("/home");
				}
			}}
			renderInput={(params) => (
				<TextField
					{...params}
					label="Rechercher"
					variant="outlined"
					size="small"
					value={query}
					onChange={(e) => setQuery(e.target.value)}
					slotProps={{
						input: {
							...params.InputProps,
							endAdornment: (
								<>
									{isLoading ? (
										<CircularProgress color="inherit" size={20} />
									) : null}
									{params.InputProps.endAdornment}
								</>
							),
							sx: { pr: 0.5 },
						},
					}}
					sx={{ display: { xs: "none", md: "inline-block" }, mr: 1 }}
				/>
			)}
		/>
	);
};
