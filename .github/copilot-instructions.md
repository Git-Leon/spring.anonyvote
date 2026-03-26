# AGENT GUIDELINES

## Purpose

Concise operational rules for automated agents and contributors. Keep changes guarded: prefer feature branches and PRs; do not deploy to production unintentionally.

## Quick rules (must-follow)

- This repository deploys on push to `master`. Avoid unnecessary or accidental pushes to `master`.
- Do feature work on branches and open a pull request to merge into `master`.
- Do not merge or push directly to `master` unless the change is explicitly intended to deploy and an authorized human or an approved automation step authorizes it.
- Before opening a PR for any behavior-affecting change, run a local Maven verification: `mvn -B verify` and ensure tests/coverage pass.
- Never commit secrets or credentials. Use GitHub Actions repository Secrets for runtime/API tokens (e.g. `DEPLOY_URL`, `DEPLOY_API_TOKEN`). Do not copy local secret files into the repository.

## Agent behavior and approvals

- Automated agents (including Copilot/automation scripts) should create feature branches and open PRs for changes. Agents may propose and push changes on feature branches but must not merge to `master` or overwrite `master` without an explicit one-line approval from a repository owner (examples below).
- One-line approvals the agent may act on (pick exactly one):
  - `continue` — merge the named PR into `master` (deploys).
  - `use .secrets.yml` — copy values from local `.secrets.yml` into the repository's GitHub Actions secrets (agent may do this only when explicitly authorized).
  - `enable monitoring` — allow the scheduled monitor workflow to run; requires the `DEPLOY_URL` secret to be present.

## Monitoring and CI notes

- There is a monitoring workflow at `.github/workflows/monitor.yml` and a health check script at `scripts/health_check.sh`.
- The monitoring workflow runs on schedule and manually, and expects `DEPLOY_URL` (required) and optionally `DEPLOY_API_TOKEN` as repository Secrets.
- The monitor has a configurable `MONITOR_THRESHOLD` to reduce false positives; failure handling creates GitHub Issues and attempts deduplication/recovery comments. Review those issues manually before acting on them.

## Naming and documentation conventions

- Prefer explicit, descriptive filenames for `.md` files. Avoid vague names like `instructions.md` — prefer `AGENT_GUIDELINES.md`, `MONITORING.md`, or `copilot-instructions.md`.
- Keep `.github` guidance concise and actionable; avoid duplicating rules across multiple files unless they serve different audiences.

## Security and stability

- Do not add production secrets to the repository. Use GitHub Actions Secrets and restrict their scope.
- Prioritize CI/CD stability: if a workflow or change risks destabilizing CI, prefer delaying deployment and requesting human review.

## If you are uncertain

- Be conservative. Only implement rules that are clearly supported by repository history or explicit maintainers' instructions. When unsure, open a PR with the proposed change and request a human reviewer.

## Examples of allowed agent commands (examples for owners)

- To permit merging a PR now, post a single-line comment in the PR: `continue`.
- To permit copying secrets from a local file into GitHub Secrets, reply with: `use .secrets.yml`.

## Contact / follow-up

If the automated agent needs new permissions (e.g., to set repository secrets, enable workflow runs, or integrate with external alerting), require explicit owner confirmation before proceeding.

---
These instructions are intentionally concise and conservative. When in doubt, open a PR and request human review.