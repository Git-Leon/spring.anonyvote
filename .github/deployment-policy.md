## Deployment Policy (CRITICAL)

This repo is connected to CI/CD on https://render.com.
ANY push to `master` triggers a deployment.

Problem:
Frequent pushes to `master` are causing overlapping deployments, preventing successful releases.

## Rules

1. DO NOT push directly to `master` unless you explicitly intend to deploy.
2. For all development work:
   - Create a feature branch: `feature/<short-description>`
   - Commit and push ONLY to that branch
3. Before considering a merge:
   - Run local Maven build (`mvn clean install`)
   - Ensure no build/runtime errors
   - Review GitHub Actions / logs if applicable

4. Deployment workflow:
   - Only merge to `master` when:
     - Feature is complete
     - Build is verified locally
     - Changes are stable
   - After merge → monitor deployment on Render

## Render Access

- App URL: https://spring-anonyvote.onrender.com/
- Render API key is located in `.secrets.yml`
- Use it ONLY if you need to:
  - Check deployment logs
  - Debug failed deployments

## Goal

Minimize unnecessary deployments.
Preserve CI/CD stability.
Ensure only validated code reaches `master`.

## Expected Behavior

Default to:
→ branch → build → verify → merge → deploy

NOT:
→ push → break deployment → repeat