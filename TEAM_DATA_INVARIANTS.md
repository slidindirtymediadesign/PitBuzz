# Team data invariants

- A team has exactly one logical Owner; ownership changes must be explicit.
- Team Admin manages only that team and never receives system/race privileges.
- A managed/dependent rider may have `user_id = NULL`.
- Claiming a dependent rider attaches a user account to the existing rider row; it does not create a duplicate rider.
- A family/crew member may follow every rider or selected riders.
- All member-rider links must remain inside the same team; API validation enforces this even though the join table alone cannot express it.
- Race number is text because motorsports numbers may contain non-numeric suffixes.
- Private messages are user-to-user and never exposed merely because two users share a team.
