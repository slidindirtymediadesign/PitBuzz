# Shared authorization consolidation

Before release, system-role checks should exist once in shared authorization helpers rather than being redefined inside endpoints.

Canonical helpers:
- `require_user()`
- `require_system_role(array $roles)`
- `require_race_control()`
- `require_super_admin()`
- `require_team_member($teamId)`
- `require_team_manager($teamId)`
- `require_rider_in_team($riderId,$teamId)`

Endpoints call these helpers and contain only operation-specific logic. This prevents permission behavior from drifting between Race Control, Active Event, Race Program, Team management and communications.
