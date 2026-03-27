Watcher wrapper
================

What
----
This repository includes a small wrapper script `scripts/watch_issue_and_rerun-wrapper.sh` that keeps the existing
watcher (`scripts/watch_issue_and_rerun.sh`) running reliably. The wrapper:

- Writes a PID file at `.monitor-watcher.pid` containing the wrapper process ID.
- Writes/rotates logs at `.monitor-watcher.log` (keeps recent rotated files).
- Restarts the child watcher on failure with exponential-ish backoff and gives up after repeated failures.

Why
---
The watcher is useful to automatically re-run the monitor workflow when there are new comments on the related
GitHub Issue. The wrapper makes it safe to run the watcher on a developer workstation or a long-running VM without
manually re-launching it after transient failures.

How to run
----------

Start the wrapper in the background (POSIX shell). The wrapper now writes runtime files into `target/`:

```bash
nohup bash scripts/watch_issue_and_rerun-wrapper.sh >> target/monitor-watcher.log 2>&1 &
echo $! > target/monitor-watcher.pid
```

Check status by inspecting the PID and log under `target/`:

```bash
cat target/monitor-watcher.pid
ps -p $(cat target/monitor-watcher.pid) -f || true
tail -n 200 target/monitor-watcher.log
```

Notes and maintenance
---------------------
- The wrapper will exit if the child fails repeatedly (safe failure). Review `.monitor-watcher.log` to diagnose.
- For production usage, create a proper service (systemd unit on Linux, Task Scheduler / NSSM on Windows) that runs
  the wrapper and restarts the wrapper on machine boot. This README intentionally keeps instructions minimal.

License
-------
Same as repository (see top-level LICENSE).
