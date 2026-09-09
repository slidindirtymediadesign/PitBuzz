# Unread routing correction

Removed obsolete `announcements.team_id` dependency.

Unread announcement counts now use the same conceptual routing model as delivery:
- active event
- Everyone
- followed rider(s)
- classes assigned to followed rider(s)
- announcement read state

Family/crew members following selected riders do not count unrelated rider/class announcements as unread.
Private-message unread count remains recipient-specific.
