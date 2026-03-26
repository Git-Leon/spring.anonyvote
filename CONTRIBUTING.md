# Contributing

Thanks for contributing to this project! A few small notes to help you get started.

- Run tests and generate coverage locally:

```bash
mvn test jacoco:report
```

- Open coverage HTML report:

  - File: `target/site/jacoco/index.html`
  - Or view the XML at `target/site/jacoco/jacoco.xml` for CI processing.

- CI

  - A GitHub Actions workflow is included at `.github/workflows/ci.yml`.
  - It runs `mvn -B verify` on pushes and PRs to `main`/`master` and uploads the JaCoCo report as an artifact.

- If you need to push these local changes to a remote repository, initialize a git repo and push to your fork:

```bash
git init
git add .
git commit -m "Add CI workflow, tests, and coverage enforcement"
# then add remote and push
```

If you'd like help creating a GitHub Pages action to publish the HTML report automatically, I can add that as well.