# Messaging implementation status

Implemented:
- authenticated conversation/history GET
- authenticated send POST
- recipient existence validation
- sender cannot message self
- 2000-character server limit
- send rate limit: 60/minute/user
- recipient-only read-state mutation
- private-message audio remains outside this endpoint and defaults OFF

Still required:
- recipient discovery/search policy
- push job enqueue after successful insert
- integration test against real MySQL/MariaDB
- native deep-link tests
