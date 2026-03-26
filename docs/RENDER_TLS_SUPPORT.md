# Hosting / TLS Diagnostic: spring.anonyvote.onrender.com

This file contains a concise, ready-to-send support message and repro steps you can use when contacting your hosting provider (Render / CDN / Cloudflare) to fix the TLS/SNI problem that prevents the monitor from reaching the app's `/health` endpoint.

Summary of the issue
--------------------
- Symptom: TLS handshake fails (SSL alert 40 / `sslv3 alert handshake failure`) when clients present SNI `spring.anonyvote.onrender.com`.
- Evidence: TCP connect succeeds, but the TLS handshake is aborted by the edge when SNI = `spring.anonyvote.onrender.com`. The handshake succeeds and a cert is returned when SNI = `onrender.com`.

Key diagnostic commands (run from your environment or share with support)
------------------------------------------------------------------------
1) DNS and CNAME check

```bash
nslookup spring.anonyvote.onrender.com
```

2) TLS handshake repro (SNI = app subdomain)

```bash
openssl s_client -connect spring.anonyvote.onrender.com:443 -servername spring.anonyvote.onrender.com -showcerts
```

Expected: a certificate and successful handshake. Actual: handshake aborts with alert 40.

3) TLS handshake repro (SNI = onrender.com)

```bash
openssl s_client -connect spring.anonyvote.onrender.com:443 -servername onrender.com -showcerts
```

Observed: handshake succeeds and the edge returns a certificate for `onrender.com`.

4) curl verbose test (shows client-side OpenSSL error)

```bash
curl -v https://spring.anonyvote.onrender.com/health
# or forcing the IP: curl -v --resolve spring.anonyvote.onrender.com:443:<IP> https://spring.anonyvote.onrender.com/health
```

What the provider needs to know (paste into support ticket)
-----------------------------------------------------------
Hello — I am seeing a TLS handshake failure for my app subdomain `spring.anonyvote.onrender.com` when clients present that hostname via SNI. The same edge IPs successfully negotiate TLS when SNI is `onrender.com` and return a certificate for `onrender.com`. This indicates the TLS termination layer is not serving a certificate for the app subdomain and is aborting the handshake for that SNI.

Observed evidence (from my tests):
- `openssl s_client -connect spring.anonyvote.onrender.com:443 -servername spring.anonyvote.onrender.com` → handshake fails (SSL alert 40).
- `openssl s_client -connect spring.anonyvote.onrender.com:443 -servername onrender.com` → handshake succeeds and cert CN = onrender.com.
- `curl -v https://spring.anonyvote.onrender.com/health` → `sslv3 alert handshake failure` in curl debug.

Please check the following on the hosting/proxy side:
1. Is `spring.anonyvote.onrender.com` added as a custom domain for the service? If not, please add it and enable HTTPS.
2. If a CDN/proxy (Cloudflare or similar) is in front, verify that the hostname's CNAME/DNS is correctly configured and that the proxy is set to present a certificate for `spring.anonyvote.onrender.com` (or disable the proxy temporarily to test direct TLS).
3. If automatic Let's Encrypt provisioning is used, check whether certificate provisioning failed or is pending for this hostname.
4. If required, provision/attach a valid certificate for `spring.anonyvote.onrender.com` and ensure the TLS edge serves it for that SNI.

How to verify the fix (after provider confirms changes)
-----------------------------------------------------
Run:

```bash
openssl s_client -connect spring.anonyvote.onrender.com:443 -servername spring.anonyvote.onrender.com -showcerts
curl -v https://spring.anonyvote.onrender.com/health -I
```

Expected: TLS handshake succeeds, certificate subjectAltName includes `spring.anonyvote.onrender.com`, and `GET /health` returns HTTP 200.

Notes for Cloudflare users
-------------------------
- If you're using Cloudflare, ensure the DNS entry for the hostname is a CNAME to the Render target and that SSL/TLS mode is set to `Full (strict)` if possible. Consider temporarily disabling the Cloudflare proxy (grey-cloud) to test direct TLS provisioning.

Repository context
------------------
I added this diagnostic file to the repository branch `monitor/fix-issues-perms` and referenced it in PR #5; see `.github/workflows/monitor.yml` and `scripts/health_check.sh` for how the GitHub Actions monitor invokes `/health` and uploads debug artifacts.

-- End of message
