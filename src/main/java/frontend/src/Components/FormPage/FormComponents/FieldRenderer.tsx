import React from "react";
import { Typography } from "@mui/material";
import FormField from "./FormField.js";
import { createKey, listField, typeComponent } from "@/utils/utils";
import NestedFields from "./NestedFields";

const FieldRenderer = ({
  parentId,
  fields,
  visited = new Set(),
  expandedFields,
  toggleField,
  handleChangingForm,
  rootId,
}: {
  parentId: string;
  fields: any[];
  visited?: Set<any>;
  expandedFields: any;
  toggleField: (fieldName: string, nodeId: string) => void;
  handleChangingForm: (name: string, id: string) => void;
  rootId: number;
}) => {
  if (!fields || !Array.isArray(fields)) return null;

  return fields.map((subField) => {
    const { id, name, type } = subField;
    if (!name || name === "graph") return null;
    const subFieldKey = createKey(subField.id, subField.name);

    if (visited.has(id)) {
      return (
        <Typography key={`cycle-${id}`} color="error">
          Circular reference detected for {name}
        </Typography>
      );
    }

    const isFieldExpanded = expandedFields[subFieldKey] || false;

    return (
      <React.Fragment key={subFieldKey}>
        <FormField
          field={subField}
          fieldKey={subFieldKey}
          isExpanded={isFieldExpanded}
          onToggleField={toggleField}
          onChangingForm={handleChangingForm}
          defaultValue={subField.value}
          parentId={parentId}
        />

        {!(
          typeComponent.includes(type) ||
          (listField.includes(type) && subField.isDropdown)
        ) && (
          <NestedFields
            fieldKey={subFieldKey}
            field={subField}
            rootId={rootId}
            isToggle={isFieldExpanded}
          />
        )}
      </React.Fragment>
    );
  });
};

export default FieldRenderer;
