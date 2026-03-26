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

# Run curl and capture HTTP status, response body, and verbose/debug output to logs.
# This makes TLS/handshake errors visible in the uploaded artifacts.
TMP_BODY="$OUT_DIR/response_body.tmp"
TMP_DEBUG="$OUT_DIR/curl_debug.txt"

if [ -n "$API_TOKEN" ]; then
  # send verbose output to debug file and capture the body separately
  STATUS=$(curl -s -S --max-time 10 -w "%{http_code}" -o "$TMP_BODY" -H "Authorization: Bearer $API_TOKEN" "$URL" 2>"$TMP_DEBUG" || echo "000")
else
  STATUS=$(curl -s -S --max-time 10 -w "%{http_code}" -o "$TMP_BODY" "$URL" 2>"$TMP_DEBUG" || echo "000")
fi

echo "[$TIMESTAMP] HTTP status: $STATUS" | tee -a "$LOG_FILE"

if [ "$STATUS" = "200" ]; then
  echo "[$TIMESTAMP] OK" | tee -a "$LOG_FILE"
  # Save a small sample of the body and the debug output for good measure
  head -c 65536 "$TMP_BODY" > "$OUT_DIR/last_response.html" || true
  echo "[${TIMESTAMP}] Saved sample response to $OUT_DIR/last_response.html" | tee -a "$LOG_FILE"
  echo "[${TIMESTAMP}] Saved curl debug to $TMP_DEBUG" | tee -a "$LOG_FILE"
  rm -f "$TMP_BODY" || true
  exit 0
else
  echo "[$TIMESTAMP] NOT OK - status $STATUS" | tee -a "$LOG_FILE"
  # Dump a small sample of the HTML and the verbose curl debug for diagnosis
  if [ -n "$(command -v curl)" ]; then
    head -c 65536 "$TMP_BODY" > "$OUT_DIR/last_response.html" || true
    echo "[${TIMESTAMP}] Saved sample response to $OUT_DIR/last_response.html" | tee -a "$LOG_FILE"
    echo "[${TIMESTAMP}] Saved curl debug to $TMP_DEBUG" | tee -a "$LOG_FILE"
  fi
  rm -f "$TMP_BODY" || true
  exit 2
fi
