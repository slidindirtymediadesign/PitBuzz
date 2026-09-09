# Notification delivery idempotency

Every user-visible notification job receives a deterministic `dedupe_key`.

Examples:
- announcement: `announcement:{announcementId}:user:{userId}`
- private message: `pm:{messageId}:user:{recipientUserId}`
- staging: `race:{eventId}:staging:{raceNumber}:user:{userId}:rider:{riderId}`
- get ready: `race:{eventId}:ready:{raceNumber}:user:{userId}:rider:{riderId}`

The database unique key prevents duplicate queue entries. A receipt is also unique per job/device subscription. Provider retries therefore cannot create multiple logical deliveries in Team PitBuzz.

Failed jobs use bounded retry/backoff and eventually become `dead`; they are never retried forever.
