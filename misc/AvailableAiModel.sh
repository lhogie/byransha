#!/bin/bash
JSON=()
LIST=("Qwen 3.5 27B" "Qwen 3.5 9B " "DeepSeek-R1 8B" "Qwen 3.5 4B " "deepseek-coder-v2-lite" "granite-4.0-h")
eval "llmfit --cli" > model.txt
cat model.txt | grep -E "Good|Perfect" > model.txt
echo "****"
echo "Testing if models pre selected are in model.txt"
printf "%s\n" "${LIST[@]}" | grep -iFf - model.txt
echo "****"
echo "Models Reccomended by llmfit for JSON (agent/script consumption):"
JSON=$(eval "llmfit recommend --json --limit 10")
echo "$JSON" | grep -oP '"name":\s*"\K[^"]+' | head -n 10
echo "****"
echo "Models reccommanded and found in model.txt:"
cat model.txt | grep -F -f <(echo "$JSON" | grep -oP '"name":\s*"\K[^"]+' | head -n 10)

