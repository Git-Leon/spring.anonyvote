#!/usr/bin/env bash
set -euo pipefail

# Wrapper to keep scripts/watch_issue_and_rerun.sh running reliably.
# - writes a stable PID file
# - rotates the log (keep 3)
# - restarts on failure with backoff, exits after too many repeated failures

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SCRIPT="$BASE_DIR/scripts/watch_issue_and_rerun.sh"
LOG="$BASE_DIR/.monitor-watcher.log"
PIDFILE="$BASE_DIR/.monitor-watcher.pid"
MAX_RESTARTS=6
BACKOFF_BASE=5
ROTATE_COUNT=3

rotate_log() {
  if [ -f "$LOG" ]; then
    size=$(wc -c <"$LOG" || echo 0)
    # rotate if bigger than 200k
    if [ "$size" -gt 204800 ]; then
      for i in $(seq $ROTATE_COUNT -1 1); do
        if [ -f "$LOG.$i" ]; then
          mv "$LOG.$i" "$LOG.$((i+1))" || true
        fi
      done
      mv "$LOG" "$LOG.1" || true
    fi
  fi
}

write_pid() {
  echo "$$" > "$PIDFILE"
}

cleanup() {
  rm -f "$PIDFILE"
  exit 0
}

trap cleanup INT TERM EXIT

echo "[watcher-wrapper] starting: $(date -u)" >> "$LOG"
rotate_log
write_pid

restart_count=0
while true; do
  echo "[watcher-wrapper] launching child at $(date -u), restart_count=$restart_count" >> "$LOG"
  if bash "$SCRIPT" >> "$LOG" 2>&1; then
    echo "[watcher-wrapper] child exited normally at $(date -u)" >> "$LOG"
    # If the child exits normally, reset restart counter and sleep longer
    restart_count=0
    sleep 60
  else
    rc=$?
    restart_count=$((restart_count+1))
    echo "[watcher-wrapper] child failed with exit $rc at $(date -u), restart_count=$restart_count" >> "$LOG"
    if [ "$restart_count" -ge "$MAX_RESTARTS" ]; then
      echo "[watcher-wrapper] too many failures, giving up at $(date -u)" >> "$LOG"
      exit 1
    fi
    backoff=$((BACKOFF_BASE * restart_count))
    echo "[watcher-wrapper] sleeping $backoff seconds before restart" >> "$LOG"
    sleep $backoff
  fi
done
