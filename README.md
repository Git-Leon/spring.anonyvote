# My First Monolith

* **Objective** - To create a full-stack monolithic application
* **Purpose** - To gain familiarity with connecting a web service to a front end application
* **Description**
   * Run this application by running the main method in `DemoApplication`.
   * Navigate to the server port specified in the [application.properties file](./src/main/resources/application.properties). By default, the port number is `8080`
   * If your application cannot run because something is occupying a port, execute this command from `Git Bash` with the respective port number specified:
       * ``kill -kill `lsof -t -i tcp:8080` ``
   * Navigate to `localhost/8080` from a browser (`Chrome`, or `Firefox`)
   * Take note of the functionality for each button that is available on the webpage.
   * Modify the functionality of the `create` button which fetches data from the [DOM](https://www.w3schools.com/js/js_htmldom.asp), and persists it in our database.

## Part 1 - Clone the project
* Begin by _forking_ this project into a personal repository.
   * To do this, click the `Fork` button located at the top right of this page.
* Navigate to **your github profile** to find the _newly forked repository_.
* Clone the repository from **your account** into your `~/dev` directory.
* Open the newly cloned project in a code editor (IntelliJ, for example).

## Running tests & coverage

To run the unit tests and generate a JaCoCo coverage report locally:

```bash
mvn test jacoco:report
```

The JaCoCo XML report will be generated at `target/site/jacoco/jacoco.xml` and
an HTML report at `target/site/jacoco/index.html` which you can open in a browser.

We've also added a GitHub Actions workflow (`.github/workflows/ci.yml`) which runs
the Maven `verify` lifecycle and uploads the JaCoCo XML as a build artifact for
pull-requests and pushes to the `main`/`master` branches.

## Status (placeholders)

You can add CI and Pages badges here after you push the repository to GitHub.
Replace `<owner>` and `<repo>` with your GitHub account and repository name.

<!-- Replace <owner>/<repo> with your GitHub repo to enable these badges -->
[![CI](https://github.com/<owner>/<repo>/actions/workflows/ci.yml/badge.svg)](https://github.com/<owner>/<repo>/actions/workflows/ci.yml) 
[![JaCoCo report](https://github.com/<owner>/<repo>/actions/workflows/publish-jacoco.yml/badge.svg)](https://<owner>.github.io/<repo>/)

```markdown
[![CI](https://github.com/<owner>/<repo>/actions/workflows/ci.yml/badge.svg)](https://github.com/<owner>/<repo>/actions/workflows/ci.yml)
```

Pages (JaCoCo report) badge placeholder (the Pages site is available after the
first successful deploy):

```markdown
[![JaCoCo report](https://github.com/<owner>/<repo>/actions/workflows/publish-jacoco.yml/badge.svg)](https://<owner>.github.io/<repo>/)
```

## JaCoCo coverage report (published)

The JaCoCo HTML report has been published to GitHub Pages for this repository.

- Public site: https://git-leon.github.io/spring.anonyvote/
- Raw fallback (if the Pages URL returns 404 while GitHub finishes propagation):
   https://raw.githubusercontent.com/Git-Leon/spring.anonyvote/gh-pages/index.html

If the Pages site is not immediately available (HTTP 404), wait a minute and reload —
GitHub sometimes needs a short moment to serve the new Pages content. The full
HTML report is also available as a workflow artifact from the CI debug run if
you need to download it manually.



