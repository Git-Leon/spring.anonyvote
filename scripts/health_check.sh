#!/usr/bin/env bash
set -euo pipefail
# Simple health check script for local or remote deployments.
# Usage: ./scripts/health_check.sh [URL]
# If URL is not provided, it will use $DEPLOY_URL or default to http://localhost:8080/

OUT_DIR="logs"
mkdir -p "$OUT_DIR"
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
URL="${1:-${DEPLOY_URL:-http://localhost:8080/}}"
API_TOKEN="${API_TOKEN:-}" # optional, do not hardcode secrets here

LOG_FILE="$OUT_DIR/health.log"

echo "[$TIMESTAMP] Checking: $URL" | tee -a "$LOG_FILE"

if [ -n "$API_TOKEN" ]; then
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $API_TOKEN" "$URL" || echo "000")
else
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || echo "000")
fi

echo "[$TIMESTAMP] HTTP status: $STATUS" | tee -a "$LOG_FILE"

if [ "$STATUS" = "200" ]; then
  echo "[$TIMESTAMP] OK" | tee -a "$LOG_FILE"
  exit 0
else
  echo "[$TIMESTAMP] NOT OK - status $STATUS" | tee -a "$LOG_FILE"
  # Optionally dump a small sample of the HTML for diagnosis
  if [ -n "$(command -v curl)" ]; then
    TMP_HTML="$OUT_DIR/last_response.html"
    if [ -n "$API_TOKEN" ]; then
      curl -s -H "Authorization: Bearer $API_TOKEN" "$URL" -o "$TMP_HTML" || true
    else
      curl -s "$URL" -o "$TMP_HTML" || true
    fi
    echo "[${TIMESTAMP}] Saved sample response to $TMP_HTML" | tee -a "$LOG_FILE"
  fi
  exit 2
fi
