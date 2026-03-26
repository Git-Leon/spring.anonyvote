# Monitor setup (DEPLOY_URL and optional API token)

This project includes a scheduled monitor workflow at `.github/workflows/monitor.yml` that runs the `scripts/health_check.sh` script against a target URL.

For this repository the public site is:

  https://spring-anonyvote.onrender.com/

Recommended DEPLOY_URL
----------------------
Because the app exposes Spring Actuator, point the monitor at the actuator health endpoint:

  DEPLOY_URL = https://spring-anonyvote.onrender.com/actuator/health

This endpoint should return HTTP 200 and a small JSON body when the app is healthy. The workflow prefers a manual input `url` when you dispatch it manually; otherwise it reads the `DEPLOY_URL` secret.

How to validate locally
-----------------------
From your machine run:

```bash
# HTTP status only
curl -s -o /dev/null -w "%{http_code}\n" "https://spring-anonyvote.onrender.com/actuator/health"

# Full JSON body (requires jq for pretty printing)
curl -s "https://spring-anonyvote.onrender.com/actuator/health" | jq .

# Run the repository health check helper (script will pick DEPLOY_URL env or use provided URL)
./scripts/health_check.sh "https://spring-anonyvote.onrender.com/actuator/health"
```

If the status is `200` the monitor will record the run as successful. Any non-200 will cause the workflow to record a failure and create/update monitor issues per `.github/workflows/monitor.yml`.

If actuator is secured
----------------------
If `/actuator/health` requires authentication, you can either:

- Provide a public `/health` endpoint in the app and use that as `DEPLOY_URL` (see below), or
- Keep using actuator and set `DEPLOY_API_TOKEN` in GitHub Secrets; the monitor will send an `Authorization: Bearer $DEPLOY_API_TOKEN` header.

How to set the secrets in GitHub (UI)
-----------------------------------
1. Go to your repository → Settings → Secrets and variables → Actions → New repository secret
2. Add a secret named `DEPLOY_URL` with the value:

   https://spring-anonyvote.onrender.com/actuator/health

3. (Optional) If your health endpoint requires a token, add `DEPLOY_API_TOKEN` with the token value.

How to set secrets with the GitHub CLI
-------------------------------------
```bash
gh secret set DEPLOY_URL --body "https://spring-anonyvote.onrender.com/actuator/health"
# optional if needed
gh secret set DEPLOY_API_TOKEN --body "<your_token_here>"
```

Notes about the workflow
------------------------
- Manual dispatch: the workflow accepts an optional `url` input (from the Actions UI) which overrides `DEPLOY_URL` for that run.
- The workflow uploads logs to the run artifacts so you can inspect the sample response when a check fails.
- The `MONITOR_THRESHOLD` environment variable (in the workflow) controls how many consecutive failures are required before the monitor escalates an issue.

Add a tiny public /health endpoint (optional)
-------------------------------------------
If you prefer not to use actuator or actuator is protected, you can add a tiny public endpoint that reliably returns 200. Example controller method (Spring Boot):

```java
// inside any @Controller or @RestController
@GetMapping("/health")
@ResponseBody
public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
}
```

After deploying, set `DEPLOY_URL` to `https://spring-anonyvote.onrender.com/health` instead.

If you want, I can add the above `/health` endpoint to the codebase and run the tests locally — tell me and I'll implement it on the current branch.
