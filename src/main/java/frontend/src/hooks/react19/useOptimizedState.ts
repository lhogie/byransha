import React, {
  useState,
  useCallback,
  useMemo,
  useRef,
  useEffect,
  useDeferredValue,
  useTransition,
} from "react";

/**
 * Enhanced state hook with React 19 concurrent features
 * Provides optimized state management with automatic transitions
 */
export function useOptimizedState<T>(
  initialValue: T | (() => T),
  options: {
    deferUpdates?: boolean;
    transitionUpdates?: boolean;
    debounceMs?: number;
  } = {},
) {
  const {
    deferUpdates = false,
    transitionUpdates = true,
    debounceMs = 0,
  } = options;

  const [state, setState] = useState(initialValue);
  const [isPending, startTransition] = useTransition();
  const debounceTimer = useRef<number | undefined>(undefined);

  // Deferred value for non-urgent updates
  const deferredState = useDeferredValue(state);
  const actualState = deferUpdates ? deferredState : state;

  const setOptimizedState = useCallback(
    (newValue: T | ((prev: T) => T)) => {
      const updateState = () => {
        if (transitionUpdates) {
          React.startTransition(() => {
            setState(newValue);
          });
        } else {
          setState(newValue);
        }
      };

      if (debounceMs > 0) {
        if (debounceTimer.current) {
          clearTimeout(debounceTimer.current);
        }
        debounceTimer.current = window.setTimeout(updateState, debounceMs);
      } else {
        updateState();
      }
    },
    [transitionUpdates, debounceMs],
  );

  // Cleanup debounce timer
  useEffect(() => {
    return () => {
      if (debounceTimer.current) {
        clearTimeout(debounceTimer.current);
      }
    };
  }, []);

  return [actualState, setOptimizedState, isPending] as const;
}

/**
 * Hook for managing async state with React 19 concurrent features
 */
export function useAsyncState<T, E = Error>(
  asyncFn: () => Promise<T>,
  deps: React.DependencyList = [],
  options: {
    immediate?: boolean;
    retryCount?: number;
    retryDelay?: number;
  } = {},
) {
  const { immediate = true, retryCount = 0, retryDelay = 1000 } = options;

  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<E | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isPending, startTransition] = useTransition();

  const retryAttempts = useRef(0);
  const mounted = useRef(true);

  const execute = useCallback(async () => {
    if (!mounted.current) return;

    React.startTransition(() => {
      setIsLoading(true);
      setError(null);
    });

    try {
      const result = await asyncFn();

      if (mounted.current) {
        React.startTransition(() => {
          setData(result);
          setIsLoading(false);
        });
      }
    } catch (err) {
      if (mounted.current) {
        if (retryAttempts.current < retryCount) {
          retryAttempts.current++;
          setTimeout(() => {
            if (mounted.current) {
              execute();
            }
          }, retryDelay);
        } else {
          React.startTransition(() => {
            setError(err as E);
            setIsLoading(false);
          });
        }
      }
    }
  }, [...deps, retryCount, retryDelay, asyncFn]);

  const retry = useCallback(() => {
    retryAttempts.current = 0;
    execute();
  }, [...deps, execute]);

  useEffect(() => {
    mounted.current = true;
    if (immediate) {
      execute();
    }

    return () => {
      mounted.current = false;
    };
  }, [execute, immediate]);

  // Deferred values for better performance
  const deferredData = useDeferredValue(data);
  const deferredError = useDeferredValue(error);

  return {
    data: deferredData,
    error: deferredError,
    isLoading,
    isPending,
    execute,
    retry,
  };
}

/**
 * Hook for optimized list management with React 19 features
 */
export function useOptimizedList<T>(
  initialItems: T[] = [],
  keyExtractor: (item: T, index: number) => string | number = (_, index) =>
    index,
) {
  const [items, setItemsState] = useState<T[]>(initialItems);
  const [isPending, startTransition] = useTransition();

  // Deferred items for non-urgent updates
  const deferredItems = useDeferredValue(items);

  const addItem = useCallback((item: T) => {
    React.startTransition(() => {
      setItemsState((prev) => [...prev, item]);
    });
  }, []);

  const removeItem = useCallback(
    (key: string | number) => {
      React.startTransition(() => {
        setItemsState((prev) =>
          prev.filter((item, index) => keyExtractor(item, index) !== key),
        );
      });
    },
    [keyExtractor],
  );

  const updateItem = useCallback(
    (key: string | number, updater: (item: T) => T) => {
      React.startTransition(() => {
        setItemsState((prev) =>
          prev.map((item, index) =>
            keyExtractor(item, index) === key ? updater(item) : item,
          ),
        );
      });
    },
    [keyExtractor],
  );

  const moveItem = useCallback((fromIndex: number, toIndex: number) => {
    React.startTransition(() => {
      setItemsState((prev) => {
        const newItems = [...prev];
        const [movedItem] = newItems.splice(fromIndex, 1);
        newItems.splice(toIndex, 0, movedItem);
        return newItems;
      });
    });
  }, []);

  const sortItems = useCallback((compareFn: (a: T, b: T) => number) => {
    React.startTransition(() => {
      setItemsState((prev) => [...prev].sort(compareFn));
    });
  }, []);

  const clearItems = useCallback(() => {
    React.startTransition(() => {
      setItemsState([]);
    });
  }, []);

  // Memoized computed values
  const itemsMap = useMemo(() => {
    const map = new Map<string | number, T>();
    deferredItems.forEach((item, index) => {
      map.set(keyExtractor(item, index), item);
    });
    return map;
  }, [deferredItems, keyExtractor]);

  const itemsCount = deferredItems.length;
  const isEmpty = itemsCount === 0;

  return {
    items: deferredItems,
    itemsMap,
    itemsCount,
    isEmpty,
    isPending,
    addItem,
    removeItem,
    updateItem,
    moveItem,
    sortItems,
    clearItems,
    setItems: useCallback((newItems: T[]) => {
      React.startTransition(() => {
        setItemsState(newItems);
      });
    }, []),
  };
}

/**
 * Hook for managing form state with React 19 optimizations
 */
export function useOptimizedForm<T extends Record<string, unknown>>(
  initialValues: T,
  options: {
    validate?: (values: T) => Partial<Record<keyof T, string>>;
    onSubmit?: (values: T) => Promise<void> | void;
    debounceValidation?: number;
  } = {},
) {
  const { validate, onSubmit, debounceValidation = 300 } = options;

  const [values, setValues, isValuesUpdating] = useOptimizedState(
    initialValues,
    {
      transitionUpdates: true,
      debounceMs: 0,
    },
  );

  const [errors, setErrors, isErrorsUpdating] = useOptimizedState<
    Partial<Record<keyof T, string>>
  >({}, { transitionUpdates: true, debounceMs: debounceValidation });

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isPending, startTransition] = useTransition();

  // Validate form when values change
  useEffect(() => {
    if (validate) {
      React.startTransition(() => {
        const newErrors = validate(values);
        setErrors(newErrors);
      });
    }
  }, [values, validate, setErrors]);

  const updateField = useCallback(
    (field: keyof T, value: unknown) => {
      React.startTransition(() => {
        setValues((prev) => ({ ...prev, [field]: value }));
      });
    },
    [setValues],
  );

  const updateFields = useCallback(
    (updates: Partial<T>) => {
      React.startTransition(() => {
        setValues((prev) => ({ ...prev, ...updates }));
      });
    },
    [setValues],
  );

  const resetForm = useCallback(() => {
    React.startTransition(() => {
      setValues(initialValues);
      setErrors({});
    });
  }, [initialValues, setValues, setErrors]);

  const handleSubmit = useCallback(
    async (e?: React.FormEvent) => {
      e?.preventDefault();

      if (!onSubmit) return;

      // Validate before submit
      if (validate) {
        const validationErrors = validate(values);
        if (Object.keys(validationErrors).length > 0) {
          setErrors(validationErrors);
          return;
        }
      }

      setIsSubmitting(true);
      try {
        await onSubmit(values);
      } catch (error) {
        console.error("Form submission error:", error);
      } finally {
        setIsSubmitting(false);
      }
    },
    [values, validate, onSubmit, setErrors],
  );

  // Deferred values for better performance
  const deferredValues = useDeferredValue(values);
  const deferredErrors = useDeferredValue(errors);

  const isValid = Object.keys(deferredErrors).length === 0;
  const isDirty =
    JSON.stringify(deferredValues) !== JSON.stringify(initialValues);

  return {
    values: deferredValues,
    errors: deferredErrors,
    isValid,
    isDirty,
    isSubmitting,
    isPending: isPending || isValuesUpdating || isErrorsUpdating,
    updateField,
    updateFields,
    resetForm,
    handleSubmit,
  };
}

/**
 * Hook for debounced values with React 19 transitions
 */
export function useOptimizedDebounce<T>(value: T, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  const [isPending, startTransition] = useTransition();

  useEffect(() => {
    const timer = window.setTimeout(() => {
      React.startTransition(() => {
        setDebouncedValue(value);
      });
    }, delay);

    return () => {
      clearTimeout(timer);
    };
  }, [value, delay]);

  return [debouncedValue, isPending] as const;
}
