# API error standard

Mobile clients should treat:
- 400/422: bad input; show actionable form message.
- 401: session expired; return to login without deleting cached race status.
- 403: signed in but insufficient permission.
- 404: requested resource no longer exists.
- 409: conflict such as duplicate membership or role limit.
- 429: rate limited; retry later.
- 500/503: server unavailable; keep cached race status and show OFFLINE/SERVICE indicator.

Never display PHP stack traces or database details to beta users.
