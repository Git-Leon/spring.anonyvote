Repository-specific instructions for automated coding agents (AI + humans)

Purpose
-------
These instructions capture working rules and conventions discussed during development of this repository so future AI agents and contributors follow the same workflow.

Core rules (do not change without explicit maintainer approval)
-----------------------------------------------------------
- This repository builds and verifies with Maven. Always run a local verification before proposing or pushing code that affects behavior:
  - mvn -B verify  # runs tests and generates JaCoCo artifacts
- JaCoCo outputs are generated to target/site/jacoco and target/jacoco.exec. CI expects the report at target/site/jacoco.
- The repository's CI is configured to run on push to `master` (and `main`), and publishing of JaCoCo reports/pages is triggered by pushes to `master`.

Branching and push rules
------------------------
- Feature work SHOULD be done on topic branches. Use clear branch names (e.g. `feature/<short>-description`, `fix/<short>`, `docs/<short>`).
- Direct pushes to `master` are permitted only when a maintainer or the repository owner has explicitly requested a push that should trigger a deployment. In other words:
  - Don't push to `master` for routine feature development. Open a PR from a branch.
  - If the repository owner asks for direct push-to-master (for example to apply an urgent hotfix or for a deliberate deployment), it is allowed.
- For automated agents: by default create a branch and open a PR unless the task explicitly states the change is an intentional deployment to `master`.

CI / verification expectations
-----------------------------
- Local pre-merge validation: run `mvn -B verify` locally and ensure:
  - All unit/integration tests pass
  - JaCoCo report is generated at `target/site/jacoco`
  - The jacoco:check thresholds configured in the `pom.xml` are satisfied
- CI runs `mvn -B verify` on PRs and pushes to `master`. The GitHub workflows upload JaCoCo artifacts and may publish reports to Pages.

Deployment and monitoring
-------------------------
- This repo's workflows will publish JaCoCo reports on push to `master`. Deployments (production or Pages publishing) should be intentional and only run after verification.
- Post-deployment checks (when applicable): check CI logs and any configured host logs (for example Render or hosting provider logs) to confirm behavior. Render-related monitoring/log checking was discussed as part of release validation and may be used as a follow-up check.

Agent workflow defaults
----------------------
- Always be conservative: do not broaden policy or push to `master` unless the change is explicitly intended to deploy.
- Run local build and tests before creating a PR or pushing to a shared branch.
- Keep commits small and focused; use clear commit messages prefixed by scope (style, fix, feat, chore) when possible.
- If an edit requires changing CI thresholds or publishing behavior, mention this clearly in the PR and request maintainer review.

Notes and constraints
---------------------
- These rules are a codification of decisions made during the current development conversation; they do not replace explicit maintainer directions.
- Do not add or remove CI workflows in a push to `master` unless the change has been reviewed.

Where to look in this repository
--------------------------------
- CI workflows: `.github/workflows/ci.yml` and `publish-jacoco.yml` — these run `mvn -B verify` and publish JaCoCo artifacts.
- PR template: `.github/PULL_REQUEST_TEMPLATE.md` — follow its checklist (it already references `mvn -B verify`).

If you are uncertain
--------------------
- Ask a human maintainer or open a PR and request review. Prefer PRs for non-trivial changes.
