# Track connectivity behavior

Cell service at tracks can be unreliable.

Mobile clients should cache:
- last RACE # / STAGING / GET READY state
- latest announcements already received
- recent private-message conversation metadata
- user notification/audio settings

When offline:
- show a clear OFFLINE / LAST UPDATED indicator
- never invent a newer race number
- queue user-originated writes locally only if the client can safely retry them
- do not repeatedly speak cached announcements

When connection returns:
- refresh race control first
- refresh unread counts
- refresh announcements/messages
- send device heartbeat
