# Race Control announcement composer

Required beta fields:
- Target: Everyone / Class / Rider
- Title
- Announcement text
- Priority: Normal / Important / Urgent
- Audio enabled for announcement

Quick race-day presets can be added after the core composer is stable.

Sending an announcement:
1. Store it first.
2. Resolve recipients.
3. Queue per-user notification jobs.
4. Return success to Race Control.
5. Dispatch notifications asynchronously/retry failures.

This avoids losing an announcement because one push provider/device is temporarily unavailable.
