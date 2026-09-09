# Production hardening gate

Release is blocked until all required items pass.

- HTTPS only.
- Production APP_URL set.
- Strong SESSION_SECRET stored outside public web root.
- Database account uses only the privileges Team PitBuzz requires.
- No database/password/provider secrets committed to application files.
- PHP error display disabled to clients; server logging enabled.
- Authentication cookies: Secure, HttpOnly, SameSite appropriate for app flow.
- CORS restricted to approved PitBuzz origins.
- Rate limiting enabled on sensitive writes.
- Super Admin count capped at 2; Admin 3; Announcer 4.
- Cross-team authorization tests pass.
- Health endpoint passes against live MySQL.
- FCM configured and tested on a real Android device before Android beta signoff.
- APNs configured and tested on a real iPhone before iPhone beta signoff.
- Private messages do not auto-speak by default.
- Backup/restore procedure tested before production data is trusted.
