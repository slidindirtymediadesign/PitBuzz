# Offline/reconnect race-alert behavior

- Race Control queues alerts immediately after a committed state change.
- App bootstrap also reconciles the signed-in user's current rider/race relevance.
- `race_alert_state` suppresses alerts already handled for that user/rider/stage.
- notification job dedupe prevents duplicate queue entries.
- race-alert source identity includes event + race + rider + stage, so two riders followed by one user in the same race do not collapse into one job.
- This supports phones that temporarily lose track connectivity and later reconnect.
