# Monitoring and deployment log checks

This file explains how automated agents and maintainers should configure monitoring for deployments.

What we added
- `scripts/health_check.sh` — lightweight script that pings an app URL and writes a small log to `logs/health.log` and optionally saves the last response HTML sample to `logs/last_response.html`.
- `.github/workflows/monitor.yml` — a scheduled GitHub Actions workflow (every 15 minutes) that runs the health check. It expects `DEPLOY_URL` to be set in repository Secrets.

Security and secrets
- Do NOT commit secrets to the repository. Store runtime secrets in GitHub Actions secrets (Repository Settings -> Secrets -> Actions).
- The workflow expects two secrets (optional):
  - `DEPLOY_URL` — The URL to check (e.g., `https://myapp.example.com/`). Required for the scheduled monitor.
  - `DEPLOY_API_TOKEN` — (Optional) Bearer token used to query protected endpoints. If provided, the health script will add an `Authorization: Bearer <token>` header.

How to enable monitoring
1. In the repository on GitHub, go to Settings → Secrets → Actions and add `DEPLOY_URL` (and optionally `DEPLOY_API_TOKEN`).
2. The `monitor.yml` workflow will run on schedule and can be manually triggered via the Actions UI.

Local testing
- You can test locally without secrets by running:

```bash
DEPLOY_URL=http://localhost:8080 ./scripts/health_check.sh
```

Notes for automated agents
- Never read or leak secrets from local `.secrets.yml` files; they belong to the operator.
- If a monitoring workflow indicates repeated failures, open an issue and attach the workflow run logs (do not include tokens).
