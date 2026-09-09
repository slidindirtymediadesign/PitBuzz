# Rider-aware race alerts

The persistent bar always shows:
RACE # | STAGING | GET READY

Team members may follow all riders or selected riders.

When a followed rider's class reaches:
- GET READY: send one preparation alert.
- STAGING: send one staging alert.
- RACE #: update the persistent status; avoid redundant spoken alerts unless Race Control explicitly sends one.

Deduplication state prevents repeated alerts when phones reconnect or poll the same status repeatedly.
Dependent riders receive alerts through the team members following them; they do not need their own phone/account.
