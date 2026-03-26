# Agent guidelines — repository automation


This repository contains concise, prescriptive guidance for automated agents and CI automation.

Purpose
- Record the repository owner's explicit preference: they will not intervene going forward and expect automated agents to use discretion when making changes.

Key rules (short)
- Default to safe, conservative operations. Prefer creating a feature branch and opening a pull request for changes.
- Run local verification before proposing a PR: `mvn -B verify`. Ensure tests pass and JaCoCo reports are generated at `target/site/jacoco`.
- Do NOT push directly to `master`/`main` unless explicitly instructed by a human maintainer.
- If an urgent, intentional deployment or hotfix is explicitly requested, the maintainer may ask for a direct push to `master`. In that case follow the explicit instructions in the request.
- When making changes, keep commits small, focused, and with clear messages prefixed (e.g., `feat/`, `fix/`, `docs/`, `style/`).
- Avoid committing secrets or sensitive data. Never add contents of `.secrets.yml`, environment files, tokens, or keys to the repository. If a secret appears, create an issue or PR that removes it and follow escalation rules.

Decision-making guidance (when the owner is not intervening)
- Prefer low-risk, reversible changes: docs, tests, minor fixes, CI improvements.
- For behavioral or design changes that could affect users or deployment, open a draft PR and include a short rationale and test verification details.
- If a change requires privileged access (deploy keys, external services, publishing), stop and create an issue describing what is needed, including clear next steps and potential risks.

Escalation and visibility
- Open PRs for human review whenever there is any non-trivial risk, user-facing change, or configuration/dependency upgrade.
- Use repository issues to request access, secrets rotation, or explicit approvals.

Filenames and document naming
- Use clear, specific filenames for repository documents. Avoid vague names such as `instructions.md`, `notes.md`, or `misc.md` when the file has a specific purpose.
- Preferred names and examples:
  - `CONTRIBUTING.md` — contribution guidelines
  - `CODE_OF_CONDUCT.md` — community conduct rules
  - `PULL_REQUEST_TEMPLATE.md` — PR template
  - `DEPLOYMENT.md` or `RELEASE_PROCESS.md` — deployment or release instructions
  - `SECURITY.md` — security contact and disclosure policy
- If you create a new markdown doc, choose a name that clearly signals intent and place it under `.github/` only if it is a repository-level policy or workflow.

Maintenance
- Keep this file small and focused. If the user's preference or policy changes, update this file and reference the change in a PR.

Recorded preference: the repository owner will not intervene and expects automated agents to act with discretion (do not assume permission to deploy). If in doubt, open a PR or issue for human confirmation.
