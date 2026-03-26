Contributing guide (repository-specific highlights)

This repository has a few repository-specific conventions captured below. Follow these to keep CI, coverage reporting, and deployments stable.

Before you open a PR or push
----------------------------
- Branching: do feature work on branches (e.g. `feature/..`, `fix/..`, `docs/..`).
- Local verification: run `mvn -B verify` before opening a PR or pushing a branch for review.
  - Confirm tests pass and JaCoCo generates `target/site/jacoco` and `target/jacoco.exec`.

Merging and master
------------------
- The repository's CI will run on pushes to `master`. Pushing to `master` may trigger Pages publishing and other deployment-related workflows.
- Avoid unnecessary pushes to `master`. Use PRs for normal changes so CI can run and reviewers can confirm coverage.
- Direct pushes to `master` are allowed only for intentional deployments or urgent hotfixes and should be authorized by a maintainer.

CI / Coverage
-------------
- CI runs `mvn -B verify` and uploads JaCoCo artifacts. The project enforces JaCoCo checks via the `pom.xml` plugin configuration.
- If your change affects test coverage, include rationale in the PR description and request a careful review.

Deployment validation
---------------------
- After a deployment (push to `master`), check CI logs and any configured hosting logs as appropriate. The repo uses GitHub Actions to build and publish coverage reports; additional hosting logs (Render/etc.) may be part of release validation.

Questions
---------
If you are unsure whether to push directly to `master`, open a PR and ask for maintainer guidance.
