# Mobile state rules
- Bottom navigation badges use `/api/unread-counts.php`.
- Android/iPhone send a lightweight device heartbeat after launch/resume.
- Device heartbeat records app version/platform for beta troubleshooting.
- Race status is refreshed independently of announcements/messages.
- Losing network must not erase the last displayed race state; UI should show disconnected/stale status.
- Reconnect triggers race-status, unread-count, announcements, and messages refresh.
- Private messages remain non-audio by default regardless of reconnect/replay.
