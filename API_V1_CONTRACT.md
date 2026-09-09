# `/api/v1` beta contract

Core:
- GET app-bootstrap
- GET active-event
- GET race-control
- POST race-control (Race Control only)
- GET race-program
- POST race-program (Race Control only)

Account/team:
- POST auth/register
- POST auth/login
- POST auth/logout
- GET team
- POST team
- GET/POST notification-preferences

Communication:
- GET/POST announcements
- POST announcement-read
- POST announcement-played
- GET/POST messages
- POST message-read
- GET unread-counts

Device:
- POST push-subscription
- POST device-heartbeat

Rules:
- All write inputs validated server-side.
- Team-scoped resources require verified team membership.
- Rider-scoped resources require verified rider/team relationship.
- System role limits enforced only by server.
- Private message content is returned only to sender/recipient.
- Race Control mutations require explicit race-control/system authorization.

- GET `race-status.php` — authenticated lightweight RACE # / STAGING / GET READY refresh.

- GET/POST `system-role.php` — Super Admin-only system role assignment with server-side caps.
