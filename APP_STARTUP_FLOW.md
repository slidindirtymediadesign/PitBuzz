# Fast mobile startup

On Android/iPhone launch:
1. Restore authenticated session.
2. Call one `/api/app-bootstrap.php`.
3. Render active event + RACE # / STAGING / GET READY immediately.
4. Render team identity and message badge.
5. Start device heartbeat.
6. Refresh announcements and remaining unread counts.
7. Register/refresh push token when required.

Goal: do not make the phone wait on many sequential API calls before showing race-day information.

Server config also provides minimum supported Android/iOS versions and maintenance mode so an obsolete beta can be blocked cleanly.
